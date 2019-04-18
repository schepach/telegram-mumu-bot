package ru.mumu.bot;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.BotHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MumuBot extends TelegramLongPollingBot {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());

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
            LOGGER.info("CommandInput: " + message.getText());
            LOGGER.info("Current Date: " + currentDate);

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
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(ex.getMessage()) + ex);
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
