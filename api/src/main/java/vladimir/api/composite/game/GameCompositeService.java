package vladimir.api.composite.game;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@Api(description = "REST API for composite game information.")
public interface GameCompositeService {

	@ApiOperation( value = "${api.game-composite.get-composite-game.description}",
	        	   notes = "${api.game-composite.get-composite-game.notes}")
	@ApiResponses(value = {
	        @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
	        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
	        @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
	    })
	@GetMapping("game-composite/{gameCompositeId}")
	Mono<GameAggregate> getCompositeGame(@PathVariable int gameCompositeId);
	
	
	 @ApiOperation(
		        value = "${api.game-composite.create-composite-game.description}",
		        notes = "${api.game-composite.create-composite-game.notes}")
	 @ApiResponses(value = {
		     @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
		     @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
		    })
		    @PostMapping(
				value = "/game-composite",
		        consumes = "application/json")
		    void createCompositeGame(@RequestBody GameAggregate body);
	
	
	 @ApiOperation(
		        value = "${api.game-composite.delete-composite-game.description}",
		        notes = "${api.game-composite.delete-composite-game.notes}")
	 @ApiResponses(value = {
		     @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
		     @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
		    })
		    @DeleteMapping(value = "/game-composite/{gameId}")
		void deleteCompositeGame(@PathVariable int gameId);
}
