package ru.mumu.bot;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.mumu.utils.Connection;
import ru.mumu.utils.Constants;
import ru.mumu.utils.reflection.ReflectionObjectPrinter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexey on 08.08.16.
 */
public class MumuBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(MumuBot.class.getSimpleName());

    public void onUpdateReceived(Update update) {
        String textForUser;
        Calendar calendar = Calendar.getInstance();

        Message message = update.getMessage();
        if (message != null && message.hasText()) {

            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            Date messageDate = new Date((long) message.getDate() * 1000);
            String messageDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(messageDate.getTime());
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            LOGGER.info("FirstName: " + ReflectionObjectPrinter.toString(message.getFrom().getFirstName()));
            LOGGER.info("LastName: " + ReflectionObjectPrinter.toString(message.getFrom().getLastName()));
            LOGGER.info("UserName: " + ReflectionObjectPrinter.toString(message.getFrom().getUserName()));
            LOGGER.info("UserId: " + ReflectionObjectPrinter.toString(message.getFrom().getId()));
            LOGGER.info("CommandInput: " + message.getText());
            LOGGER.info("Day Of Month: " + dayOfMonth);

            try {
                if (message.getText().equals(Constants.HELP)) {
                    LOGGER.info("TextForUser: " + Constants.HELP_TEXT);
                    sendMsg(message, Constants.HELP_TEXT);
                } else if (message.getText().equals(Constants.START)) {
                    LOGGER.info("TextForUser: " + Constants.START_TEXT);
                    sendMsg(message, Constants.START_TEXT);
                } else if (message.getText().toLowerCase().equals(Constants.MONDAY)) {
                    textForUser = Connection.sendRequest(dayOfMonth, Constants.MONDAY);
                    sendMsg(message, textForUser);

                } else if (message.getText().toLowerCase().equals(Constants.TUESDAY)) {
                    textForUser = Connection.sendRequest(dayOfMonth, Constants.TUESDAY);
                    sendMsg(message, textForUser);

                } else if (message.getText().toLowerCase().equals(Constants.WEDNESDAY)) {
                    textForUser = Connection.sendRequest(dayOfMonth, Constants.WEDNESDAY);
                    sendMsg(message, textForUser);

                } else if (message.getText().toLowerCase().equals(Constants.THURSDAY)) {
                    textForUser = Connection.sendRequest(dayOfMonth, Constants.THURSDAY);
                    sendMsg(message, textForUser);
                } else if (message.getText().toLowerCase().equals(Constants.FRIDAY)) {
                    textForUser = Connection.sendRequest(dayOfMonth, Constants.FRIDAY);
                    sendMsg(message, textForUser);

                } else if (message.getText().toLowerCase().equals(Constants.VICTORIA)) {
                    textForUser = Connection.sendRequest(Constants.VICTORIA);
                    sendMsg(message, textForUser);
                } else if (message.getText().toLowerCase().equals(Constants.ADDRESSES)) {
                    textForUser = Connection.sendRequest(Constants.ADDRESSES);
                    sendMsg(message, textForUser);
                } else if (message.getText().toLowerCase().equals(Constants.TODAY)) {
                    textForUser = Connection.checkDay(currentDay, messageDay, dayOfMonth);
                    sendMsg(message, textForUser);
                } else {
                    LOGGER.info("TextForUser: " + Constants.ERROR_OTHER_INPUT);
                    sendMsg(message, Constants.ERROR_OTHER_INPUT);
                }
            } catch (IOException e) {
                LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage()));
            }
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplayToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage()));
        }
    }

    public String getBotUsername() {
        return "botname";
    }

    public String getBotToken() {
        return "bottoken";
    }
}
