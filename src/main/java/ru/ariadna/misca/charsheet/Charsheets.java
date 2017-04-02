package ru.ariadna.misca.charsheet;

import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Чаршиты!
 * Клиент отправляет запросы серверу (get, upload), создает локальный чаршит (init) и показывает его (preview).
 * Сервер выводит текст (get) и оперирует файлами (upload).
 */
public class Charsheets {
    static Logger logger = LogManager.getLogger("Misca-Charsheets");
    static CharsheetProvider provider = new CharsheetProvider();
    private ICommand command = new CommandCharsheet(provider);

    public void init() {
        provider.init();
        ClientCommandHandler.instance.registerCommand(command);
    }
}
