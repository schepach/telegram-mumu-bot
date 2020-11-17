package ru.mumu.bot.schedulers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.redis.RedisManager;
import ru.mumu.bot.utils.BotHelper;

import javax.ejb.Singleton;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class BroadcastScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public void run() {

        logger.log(Level.SEVERE, "Start broadcasting menu...");

        try {
            List<String> redisList = RedisManager.REDIS_STORE.lrange("MUMU_CHATID", 0, -1);

            if (redisList == null || redisList.isEmpty()) {
                logger.log(Level.SEVERE, "redisList MUMU_CHATID is null or is empty");
                return;
            }

            Calendar cal = Calendar.getInstance();
            String dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            logger.log(Level.SEVERE, "Today is " + dayOfWeek);

            String lunchInfo = BotHelper.getInfo("/".concat(dayOfWeek.toLowerCase()));

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
                logger.log(Level.SEVERE, "Broadcasting for chatId - {0}", elemOfRedis);
                try {
                    new MumuBot().execute(new SendMessage().setChatId(elemOfRedis).setText(lunchInfo));
                } catch (TelegramApiException ex) {
                    logger.log(Level.SEVERE, "Broadcasting error for chatId - {0}, because - {1}", new Object[]{elemOfRedis, ex.getMessage()});
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
