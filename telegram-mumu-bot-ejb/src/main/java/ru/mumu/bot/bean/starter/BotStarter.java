package ru.mumu.bot.bean.starter;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.bean.broadcasting.IBroadcast;
import ru.mumu.bot.bean.db.IDBOperations;
import ru.mumu.bot.bean.scheduler.broadcast.IBroadcastSchedule;
import ru.mumu.bot.bean.scheduler.cache.ICacheSchedule;

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
    private ICacheSchedule cachingScheduler;
    @Inject
    private IBroadcastSchedule broadcastScheduler;
    @Inject
    private IDBOperations idbOperations;
    @Inject
    private IBroadcast broadcast;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private BotSession botSession;

    @PostConstruct
    public void init() {
        try {
            logger.log(Level.SEVERE, "ApiContextInitializer...");
            logger.log(Level.SEVERE, "Initialization BotsApi....");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            logger.log(Level.SEVERE, "OK!");
            logger.log(Level.SEVERE, "Register MumuBot....");
            botSession = telegramBotsApi.registerBot(new MumuBot(idbOperations, broadcast));
            logger.log(Level.SEVERE, "Register done!");
            logger.log(Level.SEVERE, "MumuBot was started...");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }

    @Schedule(dayOfWeek = "Mon-Fri", hour = "11")
    public void cachingMenu() {
        cachingScheduler.startCachingMenu();
    }

    @Schedule(dayOfWeek = "Mon-Fri", hour = "11", minute = "5")
    public void broadcastingMenu() {
        broadcastScheduler.sendLunchMenu();
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Stop botSession...");
            if (botSession == null) {
                logger.log(Level.SEVERE, "botSession is null, return...");
                return;
            }
            botSession.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Destroyed exception: ", ex);
        }
    }

}
