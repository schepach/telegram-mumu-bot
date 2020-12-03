package ru.mumu.bot.bean.scheduler;

import ru.mumu.bot.cache.Caching;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.db.IDBOperations;
import ru.mumu.bot.utils.BotHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class CachingScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Inject
    private IDBOperations idbOperations;

    public void run() {

        logger.log(Level.SEVERE, "Start caching info about menu...");

        try {
            Caching.URL_MAP.clear();
            boolean idCached = Caching.cachingUrls(Constants.MUMU_MAIN_PAGE_URL);

            if (!idCached) {
                logger.log(Level.SEVERE, "Don't caching, because unexpected error");
                return;
            }

            Caching.URL_MAP.put("done", "true");
            logger.log(Level.SEVERE, "Caching done...");

            String menuInfo;
            for (String day : Constants.getDaysOfWeek()) {
                menuInfo = BotHelper.getInfo(day);

                if (idbOperations.selectDataFromDB(day) == null) {
                    idbOperations.insertDataToDB(day, menuInfo);
                    continue;
                }
                idbOperations.updateDataToDB(day, menuInfo);
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
