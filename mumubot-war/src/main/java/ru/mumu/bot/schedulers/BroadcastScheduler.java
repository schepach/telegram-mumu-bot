package ru.mumu.bot.schedulers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.BotHelper;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

public class BroadcastScheduler extends TimerTask {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void run() {

        LOGGER.log(Level.INFO, "From BroadcastScheduler...");

        try {

            Calendar calendar = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            LOGGER.info("Current time for broadcasting = " + currentTime);

            if (!currentTime.equals(Constants.START_BROADCASTING)
                    && !(currentTime.isAfter(Constants.START_BROADCASTING)
                    && currentTime.isBefore(Constants.END_BROADCASTING))) {
                LOGGER.info("Don't broadcasting menu, because the current time isn't valid");
                return;
            }

            List<String> redisList = MumuBot.REDIS_STORE.lrange("MUMU_CHATID", 0, -1);

            if (redisList == null || redisList.isEmpty()) {
                LOGGER.log(Level.INFO, "redisList MUMU_CHATID is null or is empty");
                return;
            }

            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            String messageDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

            LOGGER.log(Level.INFO, "currentDay = " + currentDay);
            LOGGER.log(Level.INFO, "messageDay = " + messageDay);

            String lunchInfo = BotHelper.getLunchInfo(Constants.TODAY, messageDay, currentDay);

            // Don't broadcasting menu, if holiday
            if (lunchInfo != null
                    && !lunchInfo.isEmpty()
                    && lunchInfo.equals(Constants.ERROR_HOLIDAY_DAY)) {
                return;
            }

            for (String elemOfRedis : redisList) {
                LOGGER.log(Level.INFO, "Broadcasting for chatId = " + elemOfRedis);
                try {
                    new MumuBot().execute(new SendMessage().setChatId(elemOfRedis).setText(lunchInfo));
                } catch (TelegramApiException ex) {
                    LOGGER.log(Level.ERROR, "Broadcasting error for chatId = " + elemOfRedis, ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
        }

    }
}
