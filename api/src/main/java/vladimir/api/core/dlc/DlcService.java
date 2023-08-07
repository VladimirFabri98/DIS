package vladimir.api.core.dlc;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DlcService {

	@GetMapping("dlc/{gameId}")
	List<Dlc> getDlcs(@PathVariable int gameId);
	
	@PostMapping(value = "/dlc" , consumes = "application/json")
	Dlc createDlc(@RequestBody Dlc body);
	
	@DeleteMapping("dlc/{gameId}")
	void deleteDlcs(@PathVariable int gameId);
}
