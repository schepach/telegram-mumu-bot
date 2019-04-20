package ru.mumu.bot;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;
import ru.mumu.bot.utils.BotHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MumuBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(MumuBot.class.getSimpleName());
    public static final Jedis REDIS_STORE = new Jedis("localhost", 6379);

    @Override
    public void onUpdateReceived(Update update) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            Date messageDate = new Date((long) message.getDate() * 1000);
            String messageDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(messageDate.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            Date currentDate = calendar.getTime();

            LOGGER.info("FirstName: " + message.getFrom().getFirstName());
            LOGGER.info("LastName: " + message.getFrom().getLastName());
            LOGGER.info("UserName: " + message.getFrom().getUserName());
            LOGGER.info("UserId: " + message.getFrom().getId());
            LOGGER.info("ChatId: " + message.getChatId());
            LOGGER.info("CommandInput: " + message.getText());
            LOGGER.info("Current Date: " + currentDate);

            checkRedisStore(String.valueOf(message.getChatId()));
            sendMsg(message, BotHelper.getLunchInfo(message.getText(), messageDay, currentDay));
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotHelper.getTextForUser(message, text));
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            LOGGER.log(Level.ERROR, "TelegramApiException: ", ex);
        }
    }

    protected static void checkRedisStore(String chatId) {

        List<String> redisList = REDIS_STORE.lrange("MUMU_CHATID", 0, -1);

        boolean isContains = false;

        if (redisList == null || redisList.isEmpty()) {
            LOGGER.info("Redis list MUMU_CHATID is empty, put first element");
            REDIS_STORE.rpush("MUMU_CHATID", chatId);
            return;
        }

        for (String elemOfRedis : redisList) {
            if (chatId.equals(elemOfRedis)) {
                isContains = true;
                break;
            }
        }

        if (!isContains) {
            LOGGER.info("chatId = " + chatId + " does not exist in redis...put it");
            REDIS_STORE.rpush("MUMU_CHATID", chatId);
        } else {
            LOGGER.info("chatId = " + chatId + " already exist in redis...go on");
        }
    }

    @Override
    public String getBotUsername() {
        return "botname";
    }

    @Override
    public String getBotToken() {
        return "token";
    }
}
