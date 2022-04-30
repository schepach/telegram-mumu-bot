package ru.mumu.bot.bean.scheduler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.db.IDBOperations;
import ru.mumu.bot.redis.RedisEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class BroadcastingScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Inject
    IDBOperations idbOperations;

    public void run() {

        logger.log(Level.SEVERE, "Start broadcasting menu...");

        try {
            List<String> chatIds = RedisEntity.getInstance().getElements("MUMU_CHATID");

            if (chatIds == null || chatIds.isEmpty()) {
                logger.log(Level.SEVERE, "chatIds MUMU_CHATID is null or is empty");
                return;
            }

            String lunchInfo = idbOperations.selectDataFromDB(Constants.TODAY);

            if (lunchInfo == null) {
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

            for (String chatId : chatIds) {
                logger.log(Level.SEVERE, "Broadcasting for chatId - {0}", chatId);
                try {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(lunchInfo);
                    new MumuBot().execute(sendMessage);
                } catch (TelegramApiException ex) {
                    logger.log(Level.SEVERE, "Broadcasting error for chatId - {0}, because - {1}", new Object[]{chatId, ex.getMessage()});
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
