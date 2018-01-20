package flavor.pie.enderpaybridge;

import com.google.inject.Inject;
import org.bstats.sponge.MetricsLite;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

@Plugin(id = "enderpaybridge", name = "EnderPay Bridge", version = "1.0-SNAPSHOT", authors = "pie_flavor",
        description = "A bridge between EnderPay and the Sponge economy API",
        dependencies = @Dependency(id = "enderpay", version = "[1.12.2-1.0.0.0,)"))
@SuppressWarnings("unused")
public class EnderPayBridge {
    @Inject
    MetricsLite metrics;

    @Listener
    public void preInit(GamePreInitializationEvent e) {
        Sponge.getServiceManager().setProvider(this, EconomyService.class, EnderPayEconomyService.instance);
        Sponge.getRegistry().registerModule(Currency.class, new EnderPayRegistryModule());
    }
}
