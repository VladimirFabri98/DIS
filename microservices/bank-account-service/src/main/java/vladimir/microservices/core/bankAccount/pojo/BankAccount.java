package vladimir.microservices.core.bankAccount.pojo;

import java.util.HashMap;

public class BankAccount {

	private int accountId;
	private HashMap<String, Double> currencies;

	public BankAccount() {
		
	}
	
	public BankAccount(int accountId, HashMap<String, Double> currencies) {
		super();
		this.accountId = accountId;
		this.currencies = currencies;
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

}
