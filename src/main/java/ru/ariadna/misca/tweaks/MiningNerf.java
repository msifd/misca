package ru.ariadna.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import ru.ariadna.misca.Misca;

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
    public final CommandStamina commandStamina = new CommandStamina(this);
    private ConfigSection config;
    private File stamina_file;
    private HashMap<String, Stamina> playerStamina = new HashMap<>();
    private Set<Block> oreBlocks = new HashSet<>();
    private LinkedHashMap<Block, Boolean> oreBlocksCache = new CompactLinkedMap<>();

    private long last_stamina_write = System.currentTimeMillis();

    MiningNerf() {
        Misca.eventBus().register(this);
    }

    public void preInit() {
        config = Tweaks.config.config().slow_mining;
        stamina_file = new File(Misca.config_dir, "mining_stamina.dat");

        readStaminaFile();
        MinecraftForge.EVENT_BUS.register(this);

        try {
            tinkerToolClass = Class.forName("tconstruct.library.tools.ToolCore");
            Tweaks.logger.info("Tinker Construct found. Slowing it down...");
        } catch (ClassNotFoundException e) {
            Tweaks.logger.warn("Tinker Construct not found. Ignore.");
        }
    }

    @Subscribe
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
    }

//    @SubscribeEvent
//    public void onOreRegistered(OreDictionary.OreRegisterEvent event) {
//        Block block = Block.getBlockFromItem(event.Ore.getItem());
//        if (block != Blocks.air) {
//            oreBlocks.add(block);
//        }
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.entityPlayer.getHeldItem();
        if (heldItem == null || !isTool(heldItem.getItem())) return;

        if (updateStamina(event.entityPlayer, event.block)) {
            if (event.newSpeed == 0) event.newSpeed = event.originalSpeed;
            event.newSpeed /= config.tool_coefficient;
        } else {
            event.newSpeed = 0;
        }

        // Записываем стамину в файл каждые 30 сек
        if (System.currentTimeMillis() - last_stamina_write > 30000) {
            writeStaminaFile();
        }
    }

    private boolean isTool(Item tool) {
        return tool instanceof ItemTool ||
                (tinkerToolClass != null && tinkerToolClass.isInstance(tool));
    }

    private boolean updateStamina(EntityPlayer player, Block block) {
        // Ахуеть как можно! Жджава уосемь уууху!
        Boolean is_ore = oreBlocksCache.computeIfAbsent(block, b -> oreBlocks.contains(b));
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

    static class ConfigSection {
        float tool_coefficient = 5.0f;
        float stamina_max = 500f;
        float stamina_restoration = 0.005f;
        float stamina_cost = 1.0f;
    }

    static class CompactLinkedMap<K, V> extends LinkedHashMap<K, V> {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_CACHED_BLOCKS;
        }
    }

    public static class CommandStamina extends CommandBase {
        private MiningNerf module;

        CommandStamina(MiningNerf module) {
            this.module = module;
        }

        @Override
        public String getCommandName() {
            return "stamina";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/stamina BWA-HA-HA!";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            float stamina = module.playerStamina.get(sender.getCommandSenderName().toLowerCase()).value;
            String msg = LanguageRegistry.instance().getStringLocalization("misca.mining_stamina.msg");
            String formatted = String.format(msg, (int) Math.floor(stamina));
            sender.addChatMessage(new ChatComponentText(formatted));
        }
    }
}
