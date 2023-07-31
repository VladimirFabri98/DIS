package vladimir.api.core.bankAccount;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface BankAccountService {

	@GetMapping("bank-account/{bankAccountId}")
	BankAccount getAccount(@PathVariable int bankAccountId);
}
