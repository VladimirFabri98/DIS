package vladimir.api.core.conversion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ConversionService {

	@GetMapping("currency-conversion/{conversionId}")
	CurrencyConversion getConversion(@PathVariable int conversionId);
}
