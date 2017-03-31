package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;

/**
 * Чаршиты!
 * Клиент отправляет запросы серверу (get, upload), создает локальный чаршит (init) и показывает его (preview).
 * Сервер выводит текст (get) и оперирует файлами (upload).
 */
public class Charsheets {
    private ICommand command = new CommandCharsheet();

    public void init() {
        CharsheetProvider.init();
        ClientCommandHandler.instance.registerCommand(command);
    }

    public void init(FMLServerStartingEvent event) {
        event.registerServerCommand(command);
    }
}
