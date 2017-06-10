package ru.ariadna.misca.charsheet;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Чаршиты!
 * Клиент отправляет запросы серверу (get, upload), создает локальный чаршит (preInit) и показывает его (preview).
 * Сервер выводит текст (get) и оперирует файлами (upload).
 */
public class Charsheets {
    static Logger logger = LogManager.getLogger("Misca-Charsheets");
    static CharsheetProvider provider = new CharsheetProvider();
    private ICommand command = new CommandCharsheet(provider);

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
        provider.init();
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(command);
    }
}
