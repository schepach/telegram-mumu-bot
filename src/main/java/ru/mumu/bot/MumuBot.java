package ru.mumu.bot;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.mumu.utils.BotHelper;
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
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            Date currentDate = calendar.getTime();

            LOGGER.info("FirstName: " + ReflectionObjectPrinter.toString(message.getFrom().getFirstName()));
            LOGGER.info("LastName: " + ReflectionObjectPrinter.toString(message.getFrom().getLastName()));
            LOGGER.info("UserName: " + ReflectionObjectPrinter.toString(message.getFrom().getUserName()));
            LOGGER.info("UserId: " + ReflectionObjectPrinter.toString(message.getFrom().getId()));
            LOGGER.info("CommandInput: " + message.getText());
            LOGGER.info("Current Date: " + currentDate);

            try {
                switch (message.getText()) {
                    case Constants.HELP:
                        LOGGER.info("TextForUser: " + Constants.HELP_TEXT);
                        sendMsg(message, Constants.HELP_TEXT);
                        break;
                    case Constants.START:
                        LOGGER.info("TextForUser: " + Constants.START_TEXT);
                        sendMsg(message, Constants.START_TEXT);
                        break;
                    case Constants.MONDAY:
                    case Constants.TUESDAY:
                    case Constants.WEDNESDAY:
                    case Constants.THURSDAY:
                    case Constants.FRIDAY:
                        textForUser = Connection.sendRequest(currentDate, BotHelper.checkDayOfWeek(message.getText()));
                        sendMsg(message, textForUser);
                        break;
                    case Constants.VICTORIA:
                        textForUser = Connection.sendRequest(Constants.VICTORIA);
                        sendMsg(message, textForUser);
                        break;
                    case Constants.ADDRESSES:
                        textForUser = Connection.sendRequest(Constants.ADDRESSES);
                        sendMsg(message, textForUser);
                        break;
                    case Constants.TODAY:
                        textForUser = Connection.checkDay(currentDay, messageDay, currentDate);
                        sendMsg(message, textForUser);
                        break;
                    default:
                        LOGGER.info("TextForUser: " + Constants.ERROR_OTHER_INPUT);
                        sendMsg(message, Constants.ERROR_OTHER_INPUT);
                        break;
                }
            } catch (IOException e) {
                LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage()) + e);
            }
        }
    }

    private void sendMsg(Message message, String text) {

        SendMessage sendMessage = new SendMessage();
        String userName = "";

        if (message.getFrom().getFirstName() != null && !message.getFrom().getFirstName().isEmpty()) {
            userName = message.getFrom().getFirstName();
        } else if (message.getFrom().getUserName() != null && !message.getFrom().getUserName().isEmpty()) {
            userName = message.getFrom().getUserName();
        } else if (message.getFrom().getLastName() != null && !message.getFrom().getLastName().isEmpty()) {
            userName = message.getFrom().getLastName();
        }

        switch (text) {
            case Constants.ERROR_HOLIDAY_DAY:
            case Constants.ERROR_OTHER_INPUT:
            case Constants.HELP_TEXT:
                LOGGER.info("TextForUser: " + text);
                sendMessage.setText(userName.concat(", ").concat(text));
                break;
            case Constants.START_TEXT:
                sendMessage.setText("Hi, ".concat(userName.concat(", ").concat(text)));
                break;
            default:
                sendMessage.setText(userName.concat(", ").concat(Constants.CAFE_MUMU_TEXT).concat(text));
                break;
        }

        if (text.contains(Constants.ADDRESSES_TEXT) || text.contains(Constants.VICTORIA_TEXT)) {
            sendMessage.setText(userName.concat(", ").concat(text));
        }

        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplayToMessageId(message.getMessageId());

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage()) + e);
        }
    }

    public String getBotUsername() {
        return "botname";
    }

    public String getBotToken() {
        return "bottoken";
    }
}
