package ru.mumu.main;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.mumu.bot.MumuBot;

/**
 * Created by alexey on 11.08.16.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) {
        ApiContextInitializer.init();
        System.out.println("Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            System.out.println("OK!");
            System.out.println("Register MumuBot....");
            telegramBotsApi.registerBot(new MumuBot());
            System.out.println("Register done.");
            System.out.println("Start MumuBot...");
            LOGGER.info("Start MumuBot...");
            System.out.println("See your log...");
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
