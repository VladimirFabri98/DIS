package vladimir.api.core.bankAccount;

import java.util.HashMap;

public class BankAccount {

	private int accountId;
	private HashMap<String, Double> currencies;
	private final String serviceAdress;
	
	public BankAccount(int accountId, HashMap<String, Double> currencies,String serviceAdress) {
		super();
		this.accountId = accountId;
		this.currencies = currencies;
		this.serviceAdress = serviceAdress;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public HashMap<String, Double> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(HashMap<String, Double> currencies) {
		this.currencies = currencies;
	}

	public String getServiceAdress() {
		return serviceAdress;
	}

	
}
