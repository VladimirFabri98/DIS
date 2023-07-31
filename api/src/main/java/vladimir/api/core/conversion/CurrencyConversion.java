package vladimir.api.core.conversion;

import vladimir.api.core.exchange.CurrencyExchange;

public class CurrencyConversion {

	private int conversionId;
	private CurrencyExchange exchangeRate;
	private double quantity;
	private final String serviceAdress;

	public CurrencyConversion(int conversionId, CurrencyExchange exchangeRate, double quantity, String serviceAdress) {
		super();
		this.conversionId = conversionId;
		this.exchangeRate = exchangeRate;
		this.quantity = quantity;
		this.serviceAdress = serviceAdress;
	}

	public int getConversionId() {
		return conversionId;
	}

	public void setConversionId(int conversionId) {
		this.conversionId = conversionId;
	}

	public CurrencyExchange getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(CurrencyExchange exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getServiceAdress() {
		return serviceAdress;
	}
	
	

}
