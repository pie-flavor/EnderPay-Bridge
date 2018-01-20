package flavor.pie.enderpaybridge;

import com.google.common.collect.ImmutableSet;
import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import com.kamildanak.minecraft.enderpay.api.NoSuchAccountException;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EnderPayEconomyService implements EconomyService {
    public static EnderPayEconomyService instance = new EnderPayEconomyService();
    private EnderPayCurrency currency = new EnderPayCurrency();

    private EnderPayEconomyService() {}

    @Override
    public EnderPayCurrency getDefaultCurrency() {
        return currency;
    }

    @Override
    public Set<Currency> getCurrencies() {
        return ImmutableSet.of(currency);
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return true;
    }

    @Override
    public boolean hasAccount(String identifier) {
        return false;
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        try {
            EnderPayApi.getBalance(uuid);
            return Optional.of(new EnderPayAccount(uuid));
        } catch (NoSuchAccountException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        return Optional.empty();
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {
        throw new UnsupportedOperationException();
    }
}
