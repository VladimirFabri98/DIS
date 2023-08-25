: ${HOST=localhost}
: ${PORT=8081}
: ${GAME_ID_DLCS_REVS_EVENTS=2}
: ${GAME_ID_NOT_FOUND=13}
: ${GAME_ID_NO_DLCS=114}
: ${GAME_ID_NO_REVS=214}
: ${GAME_ID_NO_EVENTS=314}

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
        return 0
    else
        echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
        echo  "- Failing command: $curlCmd"
        echo  "- Response Body: $RESPONSE"
        return 1
    fi
}

function assertEqual() {

    local expected=$1
    local actual=$2

    if [ "$actual" = "$expected" ]
    then
        echo "Test OK (actual value: $actual)"
        return 0
    else
        echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
        return 1
    fi
}

function testUrl() {
    url=$@
    if $url -ks -f -o /dev/null
    then
          return 0
    else
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
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo "DONE, continues..."
}

function testCompositeCreated() {

    # Expect that the Game Composite for gameId $GAME_ID_DLCS_REVS_EVENTS has been created with three dlc,three reviews and three events
    if ! assertCurl 200 "curl http://$HOST:$PORT/game-composite/$GAME_ID_DLCS_REVS_EVENTS -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$GAME_ID_DLCS_REVS_EVENTS" $(echo $RESPONSE | jq .gameId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".dlcs | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
    if [ "$?" -eq "1" ] ; then return 1; fi
	
	assertEqual 3 $(echo $RESPONSE | jq ".events | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 20 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local gameId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/game-composite/${gameId} -s"
    curl -X POST http://$HOST:$PORT/game-composite -H "Content-Type: application/json" --data "$composite"
}

function setupTestdata() {

	body="{\"gameId\":$GAME_ID_NO_DLCS"
    body+=\
',"name":"game 114","producer":"producer 114","publishYear": 2023, "reviews":[
        {"reviewId":7,"rating": 5},
        {"reviewId":8,"rating": 4},
        {"reviewId":9,"rating": 3}
    ], "events":[
		{"eventId":7, "type":"Steam Sale", "name":"Summer Sale","dateOfStart":"2013-08-08"},
		{"eventId":8, "type":"Steam Sale", "name":"Autumn Sale","dateOfStart":"2015-09-08"},
		{"eventId":9, "type":"Steam Sale", "name":"Winter Sale","dateOfStart":"2018-10-08"}
    ]
}'
    recreateComposite "$GAME_ID_NO_DLCS" "$body"


    body="{\"gameId\":$GAME_ID_NO_REVS"
    body+=\
',"name":"game 214","producer":"producer 214","publishYear": 2023, "dlcs":[
        {"dlcId":7,"name": "The Monkey King", "price": 20},
        {"dlcId":8,"name": "The Void Spirit", "price": 25},
        {"dlcId":9,"name": "The Ember Spirit", "price": 15}
    ], "events":[
		{"eventId":4, "type":"Tournament", "name":"TI3","dateOfStart":"2013-08-08"},
		{"eventId":5, "type":"Tournament", "name":"TI5","dateOfStart":"2015-09-08"},
		{"eventId":6, "type":"Tournament", "name":"TI8","dateOfStart":"2018-10-08"}
    ]
}'
    recreateComposite "$GAME_ID_NO_REVS" "$body"

    body="{\"gameId\":$GAME_ID_NO_EVENTS"
    body+=\
',"name":"game 314","producer":"producer 314","publishYear": 2023, "dlcs":[
        {"dlcId":4,"name": "Knights of Caledor", "price": 20},
        {"dlcId":5,"name": "Forge of the Chaos Dwarfs", "price": 25},
        {"dlcId":6,"name": "Heirs of Sigmar", "price": 15}
    ], "reviews":[
        {"reviewId":4,"rating": 5},
        {"reviewId":5,"rating": 4},
        {"reviewId":6,"rating": 3}
    ]
}'
    recreateComposite "$GAME_ID_NO_EVENTS" "$body"

	
	body="{\"gameId\":$GAME_ID_DLCS_REVS_EVENTS"
	body+=\
',"name":"game 2","producer":"producer 2","publishYear": 2023, "dlcs":[
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
    recreateComposite "$GAME_ID_DLCS_REVS_EVENTS" "$body"

}

set -e

echo "Start Tests:" `date`

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

waitForService curl http://$HOST:$PORT/actuator/health

setupTestdata

waitForMessageProcessing

# Verify that a normal request works, expect three dlcs,reviews and events
assertCurl 200 "curl http://$HOST:$PORT/game-composite/$GAME_ID_DLCS_REVS_EVENTS -s"
assertEqual "$GAME_ID_DLCS_REVS_EVENTS" $(echo $RESPONSE | jq .gameId)
assertEqual 3 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 3 $(echo $RESPONSE | jq ".events | length")

# Verify that a 404 (Not Found) error is returned for a non existing gameId ($GAME_ID_NOT_FOUND)
assertCurl 404 "curl http://$HOST:$PORT/game-composite/$GAME_ID_NOT_FOUND -s"

# Verify that no dlcs are returned for gameId $GAME_ID_NO_DLCS
assertCurl 200 "curl http://$HOST:$PORT/game-composite/$GAME_ID_NO_DLCS -s"
assertEqual "$GAME_ID_NO_DLCS" $(echo $RESPONSE | jq .gameId)
assertEqual 0 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 3 $(echo $RESPONSE | jq ".events | length")

# Verify that no reviews are returned for gameId $GAME_ID_NO_REVS
assertCurl 200 "curl http://$HOST:$PORT/game-composite/$GAME_ID_NO_REVS -s"
assertEqual "$GAME_ID_NO_REVS" $(echo $RESPONSE | jq .gameId)
assertEqual 3 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 3 $(echo $RESPONSE | jq ".events | length")

# Verify that no events are returned for gameId $GAME_ID_NO_EVENTS
assertCurl 200 "curl http://$HOST:$PORT/game-composite/$GAME_ID_NO_EVENTS -s"
assertEqual "$GAME_ID_NO_EVENTS" $(echo $RESPONSE | jq .gameId)
assertEqual 3 $(echo $RESPONSE | jq ".dlcs | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
assertEqual 0 $(echo $RESPONSE | jq ".events | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a gameId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/game-composite/-1 -s"
assertEqual "\"Invalid gameId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a gameId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/game-composite/invalidGameId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo "End, all tests OK:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "Stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi