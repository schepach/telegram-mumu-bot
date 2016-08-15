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

/**
 * Created by alexey on 08.08.16.
 */
public class MumuBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(MumuBot.class.getSimpleName());

    public void onUpdateReceived(Update update) {
        String result;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            LOGGER.info("FirstName: " + ReflectionObjectPrinter.toString(message.getFrom().getFirstName()));
            LOGGER.info("LastName: " + ReflectionObjectPrinter.toString(message.getFrom().getLastName()));
            LOGGER.info("UserName: " + ReflectionObjectPrinter.toString(message.getFrom().getUserName()));
            LOGGER.info("UserId: " + ReflectionObjectPrinter.toString(message.getFrom().getId()));
            LOGGER.info("CommandInput: " + message.getText());

            try {
                if (message.getText().equals("/help")) {
                    LOGGER.info("Response: " + Constants.HELP);
                    sendMsg(message, Constants.HELP);
                } else if (message.getText().equals("/start")) {
                    LOGGER.info("Response: " + Constants.START);
                    sendMsg(message, Constants.START);
                } else if (message.getText().toLowerCase().equals(Constants.MONDAY) || message.getText().toLowerCase().equals(Constants.MONDAY_1)) {
                    result = Connection.sendRequest(Constants.MONDAY);
                    sendMsg(message, result);

                } else if (message.getText().toLowerCase().equals(Constants.TUESDAY) || message.getText().toLowerCase().equals(Constants.TUESDAY_2)) {
                    result = Connection.sendRequest(Constants.TUESDAY);
                    sendMsg(message, result);

                } else if (message.getText().toLowerCase().equals(Constants.WEDNESDAY) || message.getText().toLowerCase().equals(Constants.WEDNESDAY_3)) {
                    result = Connection.sendRequest(Constants.WEDNESDAY);
                    sendMsg(message, result);

                } else if (message.getText().toLowerCase().equals(Constants.THURSDAY) || message.getText().toLowerCase().equals(Constants.THURSDAY_4)) {
                    result = Connection.sendRequest(Constants.THURSDAY);
                    sendMsg(message, result);
                } else if (message.getText().toLowerCase().equals(Constants.FRIDAY) || message.getText().toLowerCase().equals(Constants.FRIDAY_5)) {
                    result = Connection.sendRequest(Constants.FRIDAY);
                    sendMsg(message, result);

                } else if (message.getText().toLowerCase().equals(Constants.SATURDAY) || message.getText().toLowerCase().equals(Constants.SUNDAY)
                        || message.getText().toLowerCase().equals(Constants.SATURDAY_6) || message.getText().toLowerCase().equals(Constants.SUNDAY_7)) {
                    LOGGER.info("Response: " + Constants.ERROR_HOLIDAY);
                    sendMsg(message, Constants.ERROR_HOLIDAY);
                } else if (message.getText().toLowerCase().equals(Constants.VIСTORIA)) {
                    result = Connection.sendRequestVictoria(Constants.VIСTORIA);
                    sendMsg(message, result);
                } else {
                    LOGGER.info("Response: " + Constants.ERROR_OTHER_INPUT);
                    sendMsg(message, Constants.ERROR_OTHER_INPUT);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
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
            LOGGER.error(e.getMessage());
        }
    }

    public String getBotUsername() {
        return "botname";
    }

    public String getBotToken() {
        return "bottoken";
    }
}
