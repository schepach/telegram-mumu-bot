package ru.mumu.bot.bean.scheduler.broadcast;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.bean.db.IDBOperations;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.redis.RedisEntity;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class BroadcastingScheduler implements IBroadcastSchedule {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Inject
    private IDBOperations idbOperations;

    @Override
    public void sendLunchMenu() {

        logger.log(Level.SEVERE, "Start broadcasting menu...");

        try {
            List<String> userIds = RedisEntity.getInstance().getElements("MUMU_CHATID");

            if (userIds == null || userIds.isEmpty()) {
                logger.log(Level.SEVERE, "Don't broadcasting menu, because userIds is null or is empty");
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

            userIds.forEach(userId -> {
                try {
                    logger.log(Level.SEVERE, "Broadcasting to userId - {0}", userId);
                    new MumuBot().execute(SendMessage.builder()
                            .chatId(userId)
                            .text(lunchInfo)
                            .build());
                } catch (TelegramApiException ex) {
                    if (ex instanceof TelegramApiRequestException) {
                        if (((TelegramApiRequestException) ex).getErrorCode() != null) {
                            if (((TelegramApiRequestException) ex).getErrorCode() == 403
                                    && (ex.getMessage().contains("blocked by the user")
                                    || ex.getMessage().contains("user is deactivated"))) {
                                logger.log(Level.SEVERE, "ErrorCode 403 - {0}. UserId - {1} is not active. Remove this userId from Redis ...", new Object[]{ex.getMessage(), userId});
                                RedisEntity.getInstance().remElement("MUMU_CHATID", userId);
                                return;
                            }
                        }
                    }
                    logger.log(Level.SEVERE, null, ex);
                }
            });

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
        }
    }
}
