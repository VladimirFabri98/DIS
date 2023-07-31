package vladimir.api.core.exchange;

public class CurrencyExchange {

	private int exchangeId;
	private String currency_from;
	private String currency_to;
	private double exchangeRate;
	private String serviceAddress;

	public CurrencyExchange() {
		
	}
	
	public CurrencyExchange(int exchangeId, String currency_from, String currency_to, double exchangeRate,String serviceAdress) {
		super();
		this.exchangeId = exchangeId;
		this.currency_from = currency_from;
		this.currency_to = currency_to;
		this.exchangeRate = exchangeRate;
		this.serviceAddress = serviceAdress;
		
	}

	public int getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(int exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getCurrency_from() {
		return currency_from;
	}

	public void setCurrency_from(String currency_from) {
		this.currency_from = currency_from;
	}

	public String getCurrency_to() {
		return currency_to;
	}

	public void setCurrency_to(String currency_to) {
		this.currency_to = currency_to;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

}
