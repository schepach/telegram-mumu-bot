package ru.mumu.bot.schedulers;

import ru.mumu.bot.cache.Caching;
import ru.mumu.bot.constants.Constants;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CachingScheduler extends TimerTask {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void run() {

        logger.log(Level.SEVERE, "From CachingScheduler...");

        try {
            Caching.URL_MAP.clear();
            boolean idCached = Caching.cachingUrls(Constants.MUMU_MAIN_PAGE_URL);

            if (!idCached) {
                logger.log(Level.SEVERE, "Don't caching, because unexpected error");
                return;
            }

            Caching.URL_MAP.put("done", "true");
            logger.log(Level.SEVERE, "Caching done...");

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
