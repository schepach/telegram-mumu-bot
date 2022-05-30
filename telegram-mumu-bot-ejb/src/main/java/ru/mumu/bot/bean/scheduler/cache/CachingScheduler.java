package ru.mumu.bot.bean.scheduler.cache;

import ru.mumu.bot.bean.db.IDBOperations;
import ru.mumu.bot.cache.Caching;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.BotHelper;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CachingScheduler implements ICacheSchedule {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Inject
    private IDBOperations idbOperations;

    @Override
    public void startCachingMenu() {

        logger.log(Level.SEVERE, "Start caching info about menu...");

        try {
            Caching.URL_MAP.clear();
            idbOperations.deleteDataFromDB();
            boolean isCached = Caching.cachingUrls();

            if (!isCached) {
                logger.log(Level.SEVERE, "Don't caching, because unexpected error");
                return;
            }

            Caching.URL_MAP.put("done", "true");
            logger.log(Level.SEVERE, "Caching done...\nInserting menu items to DB...");

            AtomicInteger count = new AtomicInteger();

            Constants.getDaysOfWeek().forEach(day -> {
                String menuInfo = BotHelper.getInfo(day);

                if (menuInfo.equals(Constants.UNEXPECTED_ERROR))
                    return;

                if (idbOperations.selectDataFromDB(day) == null) {
                    idbOperations.insertDataToDB(day, menuInfo);
                } else {
                    idbOperations.updateDataToDB(day, menuInfo);
                }
                count.getAndIncrement();
            });
            logger.log(Level.SEVERE, "Inserted of menu items - {0}... Done.", count.get());

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
