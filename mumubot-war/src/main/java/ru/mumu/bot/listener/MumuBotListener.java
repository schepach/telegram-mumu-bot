package ru.mumu.bot.listener;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.schedulers.CachingScheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;

/**
 * Created by alexey on 11.08.16.
 */

@WebListener
public class MumuBotListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(MumuBotListener.class.getSimpleName());
    private BotSession botSession;
    private Timer time;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.info("ContextInitialized: botSession start....");
        ApiContextInitializer.init();
        LOGGER.info("Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            LOGGER.info("OK!");
            LOGGER.info("Register MumuBot....");
            botSession = telegramBotsApi.registerBot(new MumuBot());
            LOGGER.info("Register done.");
            LOGGER.info("Start MumuBot...");

            time = new Timer();
            CachingScheduler cachingScheduler = new CachingScheduler();
            time.schedule(cachingScheduler, 0, 36_000_000); //10 hours

        } catch (TelegramApiException | JSONException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION: " + ex.getMessage() + ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            LOGGER.info("ContextDestroyed: botSession stop....");
            botSession.stop();
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}
