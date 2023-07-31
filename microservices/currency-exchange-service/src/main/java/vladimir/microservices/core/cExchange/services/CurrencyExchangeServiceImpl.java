package vladimir.microservices.core.cExchange.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.exchange.CurrencyExchange;
import vladimir.api.core.exchange.ExchangeInterface;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
import vladimir.util.http.ServiceUtil;

@RestController
public class CurrencyExchangeServiceImpl implements ExchangeInterface {

	private static final Logger LOG = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public CurrencyExchangeServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public CurrencyExchange getExchange(int exchangeId) {
		LOG.debug("/Exchange return the found exchange for exchangeId={}", exchangeId);

        if (exchangeId < 1) throw new InvalidInputException("Invalid productId: " + exchangeId);

        if (exchangeId == 13) throw new NotFoundException("No product found for productId: " + exchangeId);
		
		return new CurrencyExchange(exchangeId,"EUR","RSD",117.25,serviceUtil.getServiceAddress());
	}

}
