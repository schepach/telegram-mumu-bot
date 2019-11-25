package ru.mumu.bot.schedulers;

import ru.mumu.bot.connection.Connection;
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
            Connection.URL_MAP.clear();
            String caching = Connection.cachingUrls(Constants.MUMU_MAIN_PAGE_URL);

            if (caching.equals(Constants.UNEXPECTED_ERROR)) {
                logger.log(Level.SEVERE, "Don't caching, because unexpected error");
                return;
            }

            Connection.URL_MAP.put("done", "true");
            if (!caching.isEmpty() && caching.equals("caching")) {
                logger.log(Level.SEVERE, "Caching done...");
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
