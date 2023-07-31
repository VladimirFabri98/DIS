package vladimir.microservices.core.bankAccount.services;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import vladimir.api.core.bankAccount.BankAccount;
import vladimir.api.core.bankAccount.BankAccountService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

public class BankAccountServiceImpl implements BankAccountService {

	private static final Logger LOG = LoggerFactory.getLogger(BankAccountServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	private HashMap<String,Double> currencies;
	
	@Autowired
	public BankAccountServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
		currencies.put("EUR", Double.valueOf(1200));
		currencies.put("USD", Double.valueOf(1200));
	}
	

	@Override
	public BankAccount getAccount(int bankAccountId) {
		LOG.debug("/Account return the found account for accountId={}", bankAccountId);

        if (bankAccountId < 1) throw new InvalidInputException("Invalid account: " + bankAccountId);
		
		return new BankAccount(bankAccountId,currencies,serviceUtil.getServiceAddress());
	}

}
