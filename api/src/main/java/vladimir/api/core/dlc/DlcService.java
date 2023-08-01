package vladimir.api.core.dlc;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DlcService {

	@GetMapping("dlc/{gameId}")
	List<Dlc> getDlcs(@PathVariable int gameId);
}
