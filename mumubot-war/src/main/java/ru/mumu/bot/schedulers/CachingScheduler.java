package ru.mumu.bot.schedulers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ru.mumu.bot.connection.Connection;
import ru.mumu.bot.constants.Constants;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

public class CachingScheduler extends TimerTask {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void run() {

        LOGGER.log(Level.INFO, "From CachingScheduler...");

        try {

            Calendar calendar = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            LOGGER.info("Current time for caching pages = " + currentTime);

            if (!currentTime.equals(Constants.START_TIME)
                    && !(currentTime.isAfter(Constants.START_TIME)
                    && currentTime.isBefore(Constants.END_TIME))) {
                LOGGER.info("Don't caching menu pages, because the current time isn't valid");
                return;
            }

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
