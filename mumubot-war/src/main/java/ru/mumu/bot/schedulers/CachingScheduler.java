package ru.mumu.bot.schedulers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.mumu.bot.connection.Connection;
import ru.mumu.bot.constants.Constants;

import java.util.TimerTask;

public class CachingScheduler extends TimerTask {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void run() {

        LOGGER.log(Level.INFO, "From CachingScheduler...");

        try {
            Connection.URL_MAP.clear();
            String caching = Connection.cachingUrls(Constants.MUMU_MAIN_PAGE_URL);

            if (caching.equals(Constants.UNEXPECTED_ERROR))
                return;

            Connection.URL_MAP.put("done", "true");
            if (!caching.isEmpty() && caching.equals("caching")) {
                LOGGER.info("Caching done...");
            }

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
        }
    }
}
