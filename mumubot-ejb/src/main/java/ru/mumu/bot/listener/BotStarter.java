package ru.mumu.bot.listener;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.schedulers.BroadcastScheduler;
import ru.mumu.bot.schedulers.CachingScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class BotStarter {

    @Inject
    CachingScheduler cachingScheduler;
    @Inject
    BroadcastScheduler broadcastScheduler;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private BotSession botSession;

    @PostConstruct
    public void init() {
        logger.log(Level.SEVERE, "ApiContextInitializer...");
        ApiContextInitializer.init();
        logger.log(Level.SEVERE, "Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            logger.log(Level.SEVERE, "OK!");
            logger.log(Level.SEVERE, "Register MumuBot....");
            botSession = telegramBotsApi.registerBot(new MumuBot());
            logger.log(Level.SEVERE, "Register done!");
            logger.log(Level.SEVERE, "MumuBot was started...");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }

    @Schedule(dayOfWeek = "Mon-Fri", hour = "11")
    public void cachingMenu() {
        cachingScheduler.run();
    }

    @Schedule(dayOfWeek = "Mon-Fri", hour = "11", minute = "5")
    public void broadcast() {
        broadcastScheduler.run();
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Stop botSession...");
            botSession.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Destroyed exception: ", ex);
        }
    }

}
