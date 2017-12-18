package msifeed.mc.misca.tweaks.mining;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.config.JsonConfig;
import msifeed.mc.misca.tweaks.Tweaks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Замедляет копание иструментами. Пустую руку не затрагивает.
 * Добавляет стамину, ограничивающую копание блоков из ore dictionary
 */
public class MiningNerf {
    private static final int MAX_CACHED_BLOCKS = 20;
    private static Class<?> tinkerToolClass;
    HashMap<String, Stamina> playerStamina = new HashMap<>();
    private CommandMiningStamina commandStamina = new CommandMiningStamina(this);
    private JsonConfig<ConfigSection> config = ConfigManager.getConfigFor(ConfigSection.class, "mining_nerf.json");
    private Set<Block> oreBlocks = new HashSet<>();
    private LinkedHashMap<Block, Boolean> oreBlocksCache = new CompactLinkedMap<>();

    private File stamina_file;
    private long last_stamina_write = System.currentTimeMillis();

    public void preInit(FMLPreInitializationEvent event) {
        stamina_file = new File(ConfigManager.config_dir, "mining_stamina.dat");

        readStaminaFile();

        try {
            tinkerToolClass = Class.forName("tconstruct.library.tools.ToolCore");
            Tweaks.logger.info("Tinker Construct found. Slowing it down...");
        } catch (ClassNotFoundException e) {
            Tweaks.logger.warn("Tinker Construct not found. Ignore.");
        }
    }

    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onPostInit(FMLPostInitializationEvent event) {
        // Bake ore set
        try {
            Field oresListField = OreDictionary.class.getDeclaredField("idToStackUn");
            oresListField.setAccessible(true);
            ArrayList<ArrayList<ItemStack>> ores = (ArrayList<ArrayList<ItemStack>>) oresListField.get(null);

            for (ArrayList<ItemStack> stack_list : ores) {
                for (ItemStack stack : stack_list) {
                    Block block = Block.getBlockFromItem(stack.getItem());
                    if (block != Blocks.air) {
                        oreBlocks.add(block);
                    }
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Tweaks.logger.info("MiningNerf initialized");
    }

    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(commandStamina);
    }

    public void onServerStop(FMLServerStoppingEvent event) {
        writeStaminaFile();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.entityPlayer.getHeldItem();
        if (heldItem == null || !isTool(heldItem.getItem())) return;

        if (updateStamina(event.entityPlayer, event.block)) {
            if (event.newSpeed == 0) event.newSpeed = event.originalSpeed;
            event.newSpeed /= config.get().tool_coefficient;
        } else {
            event.newSpeed = 0;
        }

        // Записываем стамину в файл каждые 10 сек
        if (System.currentTimeMillis() - last_stamina_write > 10000) {
            writeStaminaFile();
        }
    }

    private boolean isTool(Item tool) {
        return tool instanceof ItemTool ||
                (tinkerToolClass != null && tinkerToolClass.isInstance(tool));
    }

    private boolean updateStamina(EntityPlayer player, Block block) {
        ConfigSection config = this.config.get();

        // Ахуеть как можно! Жджава уосемь уууху!
        Boolean is_ore = oreBlocksCache.computeIfAbsent(block, oreBlocks::contains);
        if (!is_ore) return true;

        String name = player.getDisplayName().toLowerCase();
        Stamina stamina = playerStamina.get(name);
        if (stamina == null) {
            stamina = new Stamina();
            stamina.value = config.stamina_max;
        }

        long now = System.currentTimeMillis();
        long passed = now - stamina.last_mined;

        // Если прошло больше секунды, то стамина восстанавливается
        if (passed > 1000) {
            stamina.value = Math.min(config.stamina_max, stamina.value + passed / 1000 * config.stamina_restoration);
        } else {
            stamina.value = Math.max(0f, stamina.value - ((float) passed) * config.stamina_cost / 1000f);
        }

        stamina.last_mined = now;
        playerStamina.put(name, stamina);

        return stamina.value > 0;
    }

    private void readStaminaFile() {
        try {
            if (!stamina_file.canRead()) return;
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stamina_file));
            playerStamina = (HashMap<String, Stamina>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            Tweaks.logger.error("Failed to read stamina file", e);
        }
    }

    private void writeStaminaFile() {
        try {
            if (!stamina_file.canWrite()) return;
            stamina_file.delete();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stamina_file));
            oos.writeObject(playerStamina);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Tweaks.logger.error("Failed to write stamina file", e);
        }
        last_stamina_write = System.currentTimeMillis();
    }

    static class Stamina implements Serializable {
        long last_mined = System.currentTimeMillis();
        float value;
    }

    static class ConfigSection implements Serializable {
        Float tool_coefficient = 6.0f;
        Float stamina_max = 500f;
        Float stamina_restoration = 0.005f;
        Float stamina_cost = 1.0f;
    }

    static class CompactLinkedMap<K, V> extends LinkedHashMap<K, V> {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_CACHED_BLOCKS;
        }
    }

}
