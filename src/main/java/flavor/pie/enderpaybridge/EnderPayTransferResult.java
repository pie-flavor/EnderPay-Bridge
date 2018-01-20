package flavor.pie.enderpaybridge;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

public class EnderPayTransferResult extends EnderPayTransactionResult implements TransferResult {

    private final EnderPayAccount accountTo;

    public EnderPayTransferResult(EnderPayAccount account, EnderPayAccount accountTo, Currency currency,
                                  BigDecimal amount, Set<Context> contexts, ResultType result, TransactionType type) {
        super(account, currency, amount, contexts, result, type);
        this.accountTo = accountTo;
    }

    public EnderPayTransferResult(EnderPayAccount accountFrom, EnderPayTransactionResult result) {
        super(accountFrom, result.currency, result.amount, result.contexts, result.result, TransactionTypes.TRANSFER);
        this.accountTo = result.account;
    }

    public EnderPayTransferResult(EnderPayTransactionResult result, EnderPayAccount accountTo) {
        super(result.account, result.currency, result.amount, result.contexts, result.result, TransactionTypes.TRANSFER);
        this.accountTo = accountTo;
    }

    @Override
    public Account getAccountTo() {
        return accountTo;
    }
}
