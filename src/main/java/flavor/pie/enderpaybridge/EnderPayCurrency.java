package flavor.pie.enderpaybridge;

import com.kamildanak.minecraft.enderpay.api.EnderPayApi;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

@SuppressWarnings("deprecated")
public class EnderPayCurrency implements Currency {
    @Override
    public Text getDisplayName() {
        return Text.of(EnderPayApi.getCurrencyNameSingular());
    }

    @Override
    public Text getPluralDisplayName() {
        return Text.of(EnderPayApi.getCurrencyNameMultiple());
    }

    @Override
    public Text getSymbol() {
        return getDisplayName();
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        return Text.of(amount.longValue()).concat(getSymbol());
    }

    @Override
    public int getDefaultFractionDigits() {
        return 0;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String getId() {
        return "enderpay:credits";
    }

    @Override
    public String getName() {
        return EnderPayApi.getCurrencyNameMultiple();
    }
}
