package ru.mumu.bot.listener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.schedulers.BroadcastScheduler;
import ru.mumu.bot.schedulers.CachingScheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;


@WebListener
public class MumuBotListener implements ServletContextListener {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());
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

            BroadcastScheduler broadcastScheduler = new BroadcastScheduler();
            time.schedule(broadcastScheduler, 0, 7_200_000); //2 hour


        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            LOGGER.info("ContextDestroyed: botSession stop....");
            botSession.stop();
            time.cancel();
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Destroyed exception: ", ex);
        }
    }
}
