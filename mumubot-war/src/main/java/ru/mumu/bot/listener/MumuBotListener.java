package ru.mumu.bot.listener;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class MumuBotListener implements ServletContextListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private BotSession botSession;
    private Timer time;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.log(Level.SEVERE, "ContextInitialized: botSession start....");
        ApiContextInitializer.init();
        logger.log(Level.SEVERE, "Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            logger.log(Level.SEVERE, "OK!");
            logger.log(Level.SEVERE, "Register MumuBot....");
            botSession = telegramBotsApi.registerBot(new MumuBot());
            logger.log(Level.SEVERE, "Register done.");
            logger.log(Level.SEVERE, "Start MumuBot...");

            time = new Timer();
            CachingScheduler cachingScheduler = new CachingScheduler();
            // Caching only when server is restart
            time.schedule(cachingScheduler, 0);

            BroadcastScheduler broadcastScheduler = new BroadcastScheduler();
            time.schedule(broadcastScheduler, 0, 7_200_000); //2 hour

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            logger.log(Level.SEVERE, "ContextDestroyed: botSession stop....");
            botSession.stop();
            time.cancel();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Destroyed exception: ", ex);
        }
    }
}
