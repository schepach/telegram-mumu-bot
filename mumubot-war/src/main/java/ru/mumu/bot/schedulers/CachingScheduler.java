package ru.mumu.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import ru.mumu.bot.connection.Connection;
import ru.mumu.bot.constants.Constants;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

public class CachingScheduler extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(CachingScheduler.class.getSimpleName());

    @Override
    public void run() {

        try {
            Calendar calendar = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            LOGGER.info("currentTimeForCachingPages = " + currentTime);

            if (currentTime.equals(Constants.START_TIME)
                    || (currentTime.isAfter(Constants.START_TIME)
                    && currentTime.isBefore(Constants.END_TIME))) {

                Connection.URL_MAP.clear();
                String caching = Connection.cachingUrls(Constants.MUMU_MAIN_PAGE_URL);
                Connection.URL_MAP.put("done", "true");
                if (!caching.isEmpty() && caching.equals("caching")) {
                    LOGGER.info("Done caching...");
                    System.out.println("Done caching...");
                }
            } else if (currentTime.isAfter(LocalTime.of(1, 10, 0)) && currentTime.isBefore(Constants.START_TIME)) {
                LOGGER.info("Waiting....");
            }
        } catch (JSONException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}
