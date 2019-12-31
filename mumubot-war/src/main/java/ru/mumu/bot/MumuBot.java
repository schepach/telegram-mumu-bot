package ru.mumu.bot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.redis.RedisManager;
import ru.mumu.bot.utils.BotHelper;
import ru.mumu.bot.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MumuBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(MumuBot.class.getSimpleName());

    @Override
    public void onUpdateReceived(Update update) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
        LOGGER.log(Level.INFO, "Today is " + today);

        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            RedisManager.checkRedisStore(String.valueOf(message.getChatId()));

            //If user wants to get menu in holiday or weekend, he will get message, that menu is only from Monday to Friday
            // Besides commands: ADDRESSES, HELP and START
            String holidayInfo = BotHelper.checkDayForHoliday(message.getText(), today);
            if (holidayInfo != null) {
                sendMessage(message, holidayInfo);
                return;
            }

            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            Date messageDate = new Date((long) message.getDate() * 1000);
            String messageDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(messageDate.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            Date currentDate = calendar.getTime();

            LOGGER.log(Level.INFO, "FirstName: " + message.getFrom().getFirstName());
            LOGGER.log(Level.INFO, "LastName: " + message.getFrom().getLastName());
            LOGGER.log(Level.INFO, "UserName: " + message.getFrom().getUserName());
            LOGGER.log(Level.INFO, "UserId: " + message.getFrom().getId());
            LOGGER.log(Level.INFO, "ChatId: " + message.getChatId());
            LOGGER.log(Level.INFO, "CommandInput: " + message.getText());
            LOGGER.log(Level.INFO, "Current Date: " + currentDate);

            String info = BotHelper.getInfo(message.getText(), messageDay, currentDay);
            sendMessage(message, info);
        }
    }

    private void sendMessage(Message message, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Utils.getTextForUser(message, textMessage));
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            LOGGER.log(Level.SEVERE, "TelegramApiException: ", ex);
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
