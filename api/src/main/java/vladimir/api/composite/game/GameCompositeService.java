package vladimir.api.composite.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(description = "REST API for composite product information.")
public interface GameCompositeService {

	@ApiOperation( value = "${api.game-composite.get-composite-game.description}",
	        	   notes = "${api.game-composite.get-composite-game.notes}")
	@ApiResponses(value = {
	        @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
	        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
	        @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
	    })
	@GetMapping("game-composite/{gameCompositeId}")
	GameAggregate getAggregate(@PathVariable int gameCompositeId);
}