package vladimir.microservices.core.cConversion.pojo;

import vladimir.microservices.core.cExchange.pojo.CurrencyExchange;

public class CurrencyConversion {

	private int conversionId;
	private CurrencyExchange exchangeRate;
	private double quantity;
	
	public CurrencyConversion() {
		
	}

	public CurrencyConversion(int conversionId, CurrencyExchange exchangeRate, double quantity) {
		super();
		this.conversionId = conversionId;
		this.exchangeRate = exchangeRate;
		this.quantity = quantity;
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
	
	

}
