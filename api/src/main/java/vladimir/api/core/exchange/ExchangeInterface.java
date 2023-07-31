package vladimir.api.core.exchange;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ExchangeInterface {

	@GetMapping("currency-exchange/{exchangeId}")
	CurrencyExchange getExchange(@PathVariable int exchangeId);
}
