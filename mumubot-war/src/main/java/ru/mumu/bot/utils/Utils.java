package ru.mumu.bot.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mumu.bot.constants.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getSimpleName());

    public static void connectToURL(String url) throws IOException {
        LOGGER.log(Level.SEVERE, "ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.log(Level.SEVERE, "Response Code: " + response.getStatusLine().getStatusCode());
    }

    public static String getTextForUser(Message message, String text) {

        String userName = null;
        String textForUser;

        if (message.getFrom().getFirstName() != null && !message.getFrom().getFirstName().isEmpty()) {
            userName = message.getFrom().getFirstName();
        } else if (message.getFrom().getUserName() != null && !message.getFrom().getUserName().isEmpty()) {
            userName = message.getFrom().getUserName();
        } else if (message.getFrom().getLastName() != null && !message.getFrom().getLastName().isEmpty()) {
            userName = message.getFrom().getLastName();
        }

        if (userName == null) {
            return Constants.UNEXPECTED_ERROR;
        }

        switch (text) {
            case Constants.ERROR_HOLIDAY_DAY:
            case Constants.ERROR_OTHER_INPUT:
            case Constants.HELP_TEXT:
            case Constants.UNEXPECTED_ERROR:
                LOGGER.log(Level.INFO, "TextForUser: " + text);
                textForUser = userName.concat(", ").concat(text);
                break;
            case Constants.START_TEXT:
                textForUser = "Hey, ".concat(userName.concat(", ").concat(text));
                break;
            default:
                textForUser = "\uD83D\uDCE3 " + userName.concat(", ").concat(Constants.CAFE_MUMU_TEXT).concat(text);
                break;
        }

        if (text.contains(Constants.ADDRESSES_TEXT) || text.contains(Constants.VICTORIA_TEXT)) {
            textForUser = "\uD83D\uDCE3 " + userName.concat(", ").concat(text);
        }

        return textForUser;
    }

    public static String checkCommandToday(String currentDay, String messageDay) {

        LOGGER.log(Level.INFO, "currentDay:  " + currentDay);
        LOGGER.log(Level.INFO, "messageDay:  " + messageDay);

        if (currentDay == null || currentDay.isEmpty()
                || messageDay == null || messageDay.isEmpty()) {
            LOGGER.log(Level.SEVERE, "currentDay or messageDay is null or is empty");
            return null;
        }

        if (!currentDay.equals(messageDay)) {
            LOGGER.log(Level.SEVERE, "Days are not equals");
            return null;
        }

        String weekDay = "/".concat(currentDay).toLowerCase();
        LOGGER.log(Level.INFO, "WeekDay is : " + weekDay);
        return weekDay;
    }

}
