package msifeed.misca;

import msifeed.misca.supplies.ItemSuppliesInvoice;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Misca.MODID)
public class MiscaThings {
    @GameRegistry.ObjectHolder(ItemSuppliesInvoice.ID)
    public static final ItemSuppliesInvoice invoice = null;
}
