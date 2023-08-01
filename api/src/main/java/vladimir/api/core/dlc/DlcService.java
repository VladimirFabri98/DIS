package vladimir.api.core.dlc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface DlcService {

	@GetMapping("dlc/{dlcId}")
	Dlc getDlc(@PathVariable int dlcId);
}
