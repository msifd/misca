package msifeed.mc.aorta.genesis.rename;

import msifeed.mc.aorta.genesis.items.IItemTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class RenameProvider {

    public static boolean hasTitle(ItemStack itemStack) {
        return itemStack.hasTagCompound()
                && itemStack.stackTagCompound.hasKey("display", 10)
                && itemStack.stackTagCompound.getCompoundTag("display").hasKey(Tags.title);
    }

    public static void setTitle(ItemStack itemStack, String title) {
        if (title == null) {
            if (itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey("display", 10))
                itemStack.stackTagCompound.getCompoundTag("display").removeTag(Tags.title);
        } else {
            itemStack.setStackDisplayName(fromAmpersandFormatting(title));
        }
    }

    public static boolean hasDescription(ItemStack itemStack) {
        return itemStack.hasTagCompound()
                && itemStack.stackTagCompound.hasKey("display", 10)
                && itemStack.stackTagCompound.getCompoundTag("display").hasKey(getDescKey(itemStack));
    }

    public static List<String> getDescription(ItemStack itemStack) {
        if (!hasDescription(itemStack))
            return Collections.emptyList();
        final NBTTagList tags = itemStack.stackTagCompound
                .getCompoundTag("display")
                .getTagList(getDescKey(itemStack), 8);
        final ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < tags.tagCount(); i++)
            lines.add(intoAmpersandFormatting(tags.getStringTagAt(i).substring(2)));
        return lines;
    }

    public static void setDescription(ItemStack itemStack, NBTTagList desc) {
        if (desc.tagCount() == 0)
            return;
        createDescriptionIfNeeded(itemStack);
        itemStack.stackTagCompound
                .getCompoundTag("display")
                .setTag(getDescKey(itemStack), desc);
    }

    public static void clearAll(ItemStack itemStack) {
        itemStack.stackTagCompound.getCompoundTag("display").removeTag(Tags.title);
        itemStack.stackTagCompound.getCompoundTag("display").removeTag(Tags.description);
        itemStack.stackTagCompound.getCompoundTag("display").removeTag(Tags.vanillaDescription);
    }

    private static void createDescriptionIfNeeded(ItemStack itemStack) {
        createCompoundIfNeeded(itemStack);
        if (!itemStack.stackTagCompound.hasKey("display"))
            itemStack.stackTagCompound.setTag("display", new NBTTagCompound());
        if (!itemStack.stackTagCompound.getCompoundTag("display").hasKey(getDescKey(itemStack)))
            itemStack.stackTagCompound.getCompoundTag("display").setTag(getDescKey(itemStack), new NBTTagList());
    }

    public static boolean hasOverriddenValues(ItemStack itemStack) {
        return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(Tags.values);
    }

    public static void setValue(ItemStack itemStack, String key, String value) {
        if (value == null && !hasOverriddenValues(itemStack))
            return;

        createOverriddenValuesIfNeeded(itemStack);
        final NBTTagCompound values = itemStack.stackTagCompound.getCompoundTag(Tags.values);
        if (value == null)
            values.removeTag(key);
        else
            values.setString(fromAmpersandFormatting(key), fromAmpersandFormatting(value));
    }

    public static Map<String, String> getOverriddenValues(ItemStack itemStack) {
        if (!hasOverriddenValues(itemStack))
            return Collections.emptyMap();

        final NBTTagCompound values = itemStack.stackTagCompound.getCompoundTag(Tags.values);
        final HashMap<String, String> m = new HashMap<>();
        for (String k : (Set<String>) values.func_150296_c())
            m.put(k, values.getString(k));
        return m;
    }

    private static void createOverriddenValuesIfNeeded(ItemStack itemStack) {
        createCompoundIfNeeded(itemStack);
        if (!itemStack.stackTagCompound.hasKey(Tags.values))
            itemStack.stackTagCompound.setTag(Tags.values, new NBTTagCompound());
    }

    private static void createCompoundIfNeeded(ItemStack itemStack) {
        if (!itemStack.hasTagCompound())
            itemStack.stackTagCompound = new NBTTagCompound();
    }

    private static String getDescKey(ItemStack itemStack) {
        return itemStack.getItem() instanceof IItemTemplate
                ? Tags.description
                : Tags.vanillaDescription;
    }

    static String fromAmpersandFormatting(String str) {
        return str.replace('&', '\u00A7');
    }

    public static String intoAmpersandFormatting(String str) {
        return str.replace('\u00A7', '&');
    }

    static class Tags {
        static final String title = "Name";
        static final String description = "aorta.desc";
        static final String vanillaDescription = "Lore";
        static final String values = "aorta.values";
    }
}
