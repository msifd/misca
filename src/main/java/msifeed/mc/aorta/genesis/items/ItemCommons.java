package msifeed.mc.aorta.genesis.items;

import msifeed.mc.aorta.genesis.rename.RenameProvider;
import msifeed.mc.aorta.sys.utils.L10n;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemCommons {
    public static void addInformation(ItemGenesisUnit unit, ItemStack itemStack, List<String> lines) {
        final List<String> customDesc = RenameProvider.getDescription(itemStack);
        if (!customDesc.isEmpty())
            lines.addAll(customDesc);
        else if (unit.desc != null)
            Collections.addAll(lines, unit.desc);

        final LinkedHashMap<String, String> values = new LinkedHashMap<>(unit.values);
        values.putAll(RenameProvider.getOverriddenValues(itemStack));
        for (Map.Entry<String, String> entry : values.entrySet())
            if (!entry.getValue().isEmpty())
                lines.add("\u00A7r" + entry.getKey() + "\u00A7r: " + entry.getValue());

        if (unit.specialAttackCost > 0 && unit.maxUsages > 0)
            lines.add("\u00A7r" + L10n.fmt("aorta.gen.special_attack_cost", unit.specialAttackCost));

        if (unit.maxUsages > 0) {
            int usages = unit.maxUsages;

            if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("usages"))
                usages = itemStack.getTagCompound().getInteger("usages");

            String usageLine = L10n.fmt("aorta.gen.uses_left", usages);

            if (usages == 0)
                usageLine = L10n.fmt("aorta.gen.needs_reload");

            lines.add("\u00A7r" + usageLine);
        }

        if (unit.durData.maxDurability > 0) {
            if (itemStack.getItemDamage() == 1)
                lines.add("\u00A74" + L10n.fmt("aorta.gen.broken"));
            else
                lines.add("\u00A7r" + L10n.fmt("aorta.gen.durability", itemStack.getItemDamage(), unit.durData.maxDurability,
                        Math.round(((double)itemStack.getItemDamage() / unit.durData.maxDurability) * 100)));
        }
    }
}
