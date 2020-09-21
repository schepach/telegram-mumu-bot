package ru.mumu.bot.schedulers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.redis.RedisManager;
import ru.mumu.bot.utils.BotHelper;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BroadcastScheduler extends TimerTask {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void run() {

        logger.log(Level.SEVERE, "From BroadcastScheduler...");

        try {

            Calendar calendar = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            logger.log(Level.SEVERE, "Current time for broadcasting = " + currentTime);

            if (!currentTime.equals(Constants.START_BROADCASTING)
                    && !(currentTime.isAfter(Constants.START_BROADCASTING)
                    && currentTime.isBefore(Constants.END_BROADCASTING))) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because the current time isn't valid");
                return;
            }

            List<String> redisList = RedisManager.REDIS_STORE.lrange("MUMU_CHATID", 0, -1);

            if (redisList == null || redisList.isEmpty()) {
                logger.log(Level.SEVERE, "redisList MUMU_CHATID is null or is empty");
                return;
            }

            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            logger.log(Level.SEVERE, "Today is " + today);

            if (today.equals("Saturday") || today.equals("Sunday")) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because today is holiday");
                return;
            }

            String lunchInfo = BotHelper.getInfo("/".concat(today.toLowerCase()), today, today);

            // Don't broadcasting menu, if lunchInfo is null or is empty
            if (lunchInfo == null || lunchInfo.isEmpty()) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because lunchInfo is null or is empty");
                return;
            }

            if (lunchInfo.equals(Constants.UNEXPECTED_ERROR)) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because UNEXPECTED_ERROR");
                return;
            }

            // Don't broadcasting menu, if holiday
            if (lunchInfo.equals(Constants.INFO_HOLIDAY_DAY)) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because holiday");
                return;
            }

            for (String elemOfRedis : redisList) {
                logger.log(Level.SEVERE, "Broadcasting for chatId = " + elemOfRedis);
                try {
                    new MumuBot().execute(new SendMessage().setChatId(elemOfRedis).setText(lunchInfo));
                } catch (TelegramApiException ex) {
                    logger.log(Level.SEVERE, "Broadcasting error for chatId = " + elemOfRedis + ", because " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }

    }
}
