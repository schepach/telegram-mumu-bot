package ru.mumu.bot;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.mumu.constants.Constants;
import ru.mumu.utils.helper.BotHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexey on 08.08.16.
 */
public class MumuBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(MumuBot.class.getSimpleName());

    @Override
    public void onUpdateReceived(Update update) {

        String textForUser;
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
            LOGGER.info("CommandInput: " + message.getText());
            LOGGER.info("Current Date: " + currentDate);

            textForUser = BotHelper.checkMessage(message.getText(), currentDate, messageDay, currentDay);
            sendMsg(message, textForUser);
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(BotHelper.getTextForUser(message, text));
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage()) + e);
        }
    }

    @Override
    public String getBotUsername() {
        return "botname";
    }

    @Override
    public String getBotToken() {
        return "bottoken";
    }
}
