#!/usr/bin/env bash
#
# ./grdelw clean build
# docker-compose build
# docker-compose up -d
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8081}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 10 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local gameId=$1
    local composite=$2

    assertCurl 404 "curl -X GET http://$HOST:$PORT/game-composite/\"$gameId\" "
    assertCurl -g 200 "curl -X POST http://$HOST:$PORT/game-composite \"Content-Type: application/json\" --data \"$composite\" "
	curl -X POST http://$HOST:$PORT/game-composite -H "Content-Type: application/json" --data "$composite"
	assertCurl 200 "curl -X GET http://$HOST:$PORT/game-composite/\"$gameId\" "
}

function setupTestdata() {

    body=\
'{"gameId":1,"name":"game 1","producer":"producer 1","publishYear": 2023, "dlcs":[
        {"dlcId":1,"name": "Wrath of the Lich King", "price": 20},
        {"dlcId":2,"name": "Burning crusade", "price": 5},
        {"dlcId":3,"name": "Cataclysm", "price": 15}
    ], "reviews":[
        {"reviewId":1,"rating": 5},
        {"reviewId":2,"rating": 4},
        {"reviewId":3,"rating": 3}
    ], "events":[
		{"eventId":1, "type":"Conference", "name":"Blizzcon","dateOfStart":"2023-08-08"},
		{"eventId":2, "type":"Conference", "name":"Blizzcon","dateOfStart":"2023-09-08"},
		{"eventId":3, "type":"Conference", "name":"Blizzcon","dateOfStart":"2023-10-08"}
	]}'
    recreateComposite 1 "$body"
	
	body=\
'{"gameId":200,"name":"game 200","producer":"producer 200","publishYear": 200}'
	recreateComposite 200 "$body"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

setupTestdata

# Verify that a normal request works, expect three dlcs,reviews and events
assertCurl 200 "curl http://$HOST:$PORT/game-composite/1 -s"
assertEqual 1 $(echo $RESPONSE | jq .gameId)
assertEqual 3 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 3 $(echo $RESPONSE | jq ".events | length")

# Verify that a 404 (Not Found) error is returned for a non existing gameId (50)
assertCurl 404 "curl http://$HOST:$PORT/game-composite/50 -s"

# Verify that no reviews,dlcs and events are returned for gameId 200
assertCurl 200 "curl http://$HOST:$PORT/game-composite/200 -s"
assertEqual 200 $(echo $RESPONSE | jq .gameId)
assertEqual 0 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 0 $(echo $RESPONSE | jq ".events | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a gameId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/game-composite/-1 -s"
assertEqual "\"Invalid gameId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a gameId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/game-composite/invalidGameId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End:" `date`