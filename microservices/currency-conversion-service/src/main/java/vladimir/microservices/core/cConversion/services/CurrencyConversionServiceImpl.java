package vladimir.microservices.core.cConversion.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import vladimir.api.core.conversion.ConversionService;
import vladimir.api.core.conversion.CurrencyConversion;
import vladimir.api.core.exchange.CurrencyExchange;
import vladimir.microservices.core.cExchange.services.CurrencyExchangeServiceImpl;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

public class CurrencyConversionServiceImpl implements ConversionService {

	private static final Logger LOG = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public CurrencyConversionServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public CurrencyConversion getConversion(int conversionId) {
		LOG.debug("/Conversion return the found conversion for conversionId={}", conversionId);

        if (conversionId < 1) throw new InvalidInputException("Invalid conversion: " + conversionId);
        
		return new CurrencyConversion(conversionId,
				new CurrencyExchange(1,"EUR","RSD",Double.valueOf(117.25),"no data"),
				Double.valueOf(100),
				serviceUtil.getServiceAddress());
	}

}
