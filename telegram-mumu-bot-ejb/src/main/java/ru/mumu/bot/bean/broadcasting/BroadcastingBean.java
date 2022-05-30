package ru.mumu.bot.bean.broadcasting;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.mumu.bot.MumuBot;
import ru.mumu.bot.redis.RedisEntity;

import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Default
@Singleton
public class BroadcastingBean implements IBroadcast {

    private static final Logger logger = Logger.getLogger(MumuBot.class.getSimpleName());

    @Override
    public void broadcastMessage(String message) {
        String finalMessage = message.replaceAll("/broadcasting", "");

        List<String> userIds = RedisEntity.getInstance().getElements("MUMU_CHATID");

        if (userIds == null || userIds.isEmpty()) {
            logger.log(Level.SEVERE, "Don't broadcasting message, because userIds is null or is empty");
            return;
        }

        userIds.forEach(userId -> {
            try {
                new MumuBot().execute(SendMessage.builder()
                        .text(finalMessage)
                        .chatId(userId)
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
    }
}
