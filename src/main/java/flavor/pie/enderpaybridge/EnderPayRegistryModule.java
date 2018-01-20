package flavor.pie.enderpaybridge;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.service.economy.Currency;

import java.util.Collection;
import java.util.Optional;

public class EnderPayRegistryModule implements CatalogRegistryModule<Currency> {
    @Override
    public Optional<Currency> getById(String id) {
        EnderPayCurrency currency = EnderPayEconomyService.instance.getDefaultCurrency();
        if (id.equals(currency.getId())) {
            return Optional.of(currency);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Currency> getAll() {
        return ImmutableSet.of(EnderPayEconomyService.instance.getDefaultCurrency());
    }
}
