package flavor.pie.enderpaybridge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kamildanak.minecraft.enderpay.EnderPay;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.InsufficientCreditException;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EnderPayAccount implements UniqueAccount {
    private UUID id;

    public EnderPayAccount(UUID id) {
        this.id = id;
    }

    @Override
    public Text getDisplayName() {
        Optional<Player> player = Sponge.getServer().getPlayer(id);
        if (player.isPresent()) {
            return Text.of(player.get().getName());
        } else {
            Optional<User> user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(id);
            return Text.of(user.map(User::getName).orElseGet(id::toString));
        }
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        return BigDecimal.valueOf(EnderPay.settings.getStartBalance());
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        try {
            EnderPayApi.getBalance(id);
            return true;
        } catch (NoSuchAccountException e) {
            return false;
        }
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        if (hasBalance(currency, contexts)) {
            try {
                return BigDecimal.valueOf(EnderPayApi.getBalance(id));
            } catch (NoSuchAccountException e) {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Currency curr = EnderPayEconomyService.instance.getDefaultCurrency();
        if (!hasBalance(curr)) {
            return ImmutableMap.of();
        }
        try {
            return ImmutableMap.of(EnderPayEconomyService.instance.getDefaultCurrency(),
                    BigDecimal.valueOf(EnderPayApi.getBalance(id)));
        } catch (NoSuchAccountException e) {
            return ImmutableMap.of();
        }
    }

    @Override
    public EnderPayTransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        } else {
            long total = amount.longValue();
            if (total < 0) {
                return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.FAILED,
                        TransactionTypes.DEPOSIT);
            }
            try {
                long existingTotal = EnderPayApi.getBalance(id);
                EnderPayApi.addToBalance(id, total - existingTotal);
                return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS,
                        TransactionTypes.DEPOSIT);
            } catch (NoSuchAccountException e) {
                return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.FAILED,
                        TransactionTypes.DEPOSIT);
            }
        }
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        Currency currency = EnderPayEconomyService.instance.getDefaultCurrency();
        return ImmutableMap.of(currency, resetBalance(currency, cause, contexts));
    }

    @Override
    public EnderPayTransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        return setBalance(currency, getDefaultBalance(currency), cause, contexts);
    }

    @Override
    public EnderPayTransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount");
        }
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        try {
            EnderPayApi.addToBalance(id, amount.longValue());
            return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS,
                    TransactionTypes.DEPOSIT);
        } catch (NoSuchAccountException e) {
            return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.FAILED,
                    TransactionTypes.DEPOSIT);
        }
    }

    @Override
    public EnderPayTransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount");
        }
        if (currency != EnderPayEconomyService.instance.getDefaultCurrency()) {
            throw new IllegalArgumentException("currency");
        }
        try {
            EnderPayApi.takeFromBalance(id, amount.longValue());
            return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS,
                    TransactionTypes.WITHDRAW);
        } catch (NoSuchAccountException e) {
            return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.FAILED,
                    TransactionTypes.WITHDRAW);
        } catch (InsufficientCreditException e) {
            return new EnderPayTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS,
                    TransactionTypes.WITHDRAW);
        }
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (!(to instanceof EnderPayAccount)) {
            throw new IllegalArgumentException("to");
        }
        EnderPayAccount toAccount = (EnderPayAccount) to;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount");
        }
        EnderPayTransactionResult result = withdraw(currency, amount, cause, contexts);
        if (result.getResult() != ResultType.SUCCESS) {
            return new EnderPayTransferResult(result, toAccount);
        }
        EnderPayTransactionResult result2 = toAccount.deposit(currency, amount, cause, contexts);
        if (result2.getResult() != ResultType.SUCCESS) {
            deposit(currency, amount, cause, contexts);
        }
        return new EnderPayTransferResult(this, result2);
    }

    @Override
    public String getIdentifier() {
        return id.toString();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return ImmutableSet.of();
    }

    @Override
    public UUID getUniqueId() {
        return id;
    }
}
