package vladimir.microservices.core.cConversionComposite.pojo;

import vladimir.microservices.core.bankAccount.pojo.BankAccount;
import vladimir.microservices.core.cConversion.pojo.CurrencyConversion;

public class ConversionComposite {

	private int compositeId;
	private CurrencyConversion conversion;
	private BankAccount account;
	private String conversionInfo;

	public ConversionComposite() {

	}

	public ConversionComposite(int compositeId, CurrencyConversion conversion, BankAccount account,
			String conversionInfo) {
		super();
		this.compositeId = compositeId;
		this.conversion = conversion;
		this.account = account;
		this.conversionInfo = conversionInfo;
	}

	public int getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(int compositeId) {
		this.compositeId = compositeId;
	}

	public CurrencyConversion getConversion() {
		return conversion;
	}

	public void setConversion(CurrencyConversion conversion) {
		this.conversion = conversion;
	}

	public BankAccount getAccount() {
		return account;
	}

	public void setAccount(BankAccount account) {
		this.account = account;
	}

	public String getConversionInfo() {
		return conversionInfo;
	}

	public void setConversionInfo(String conversionInfo) {
		this.conversionInfo = conversionInfo;
	}

}
