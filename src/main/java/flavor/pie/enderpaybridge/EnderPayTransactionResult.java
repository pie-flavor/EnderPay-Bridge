package flavor.pie.enderpaybridge;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Set;

public class EnderPayTransactionResult implements TransactionResult {
    protected final EnderPayAccount account;
    protected final Currency currency;
    protected final BigDecimal amount;
    protected final Set<Context> contexts;
    protected final ResultType result;
    protected final TransactionType type;

    public EnderPayTransactionResult(EnderPayAccount account, Currency currency, BigDecimal amount,
                                     Set<Context> contexts, ResultType result, TransactionType type) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.result = result;
        this.type = type;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Set<Context> getContexts() {
        return contexts;
    }

    @Override
    public ResultType getResult() {
        return result;
    }

    @Override
    public TransactionType getType() {
        return type;
    }
}
