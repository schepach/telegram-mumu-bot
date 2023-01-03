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

    private static final Logger logger = Logger.getLogger(Utils.class.getSimpleName());

    public static void connectToURL(String url) throws IOException {
        logger.log(Level.SEVERE, "Connect to {0}", url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        logger.log(Level.SEVERE, "Response code - {0}", response.getStatusLine().getStatusCode());
    }

    public static int connect(String url) throws IOException {
        logger.log(Level.SEVERE, "Connect to {0}", url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        logger.log(Level.SEVERE, "Response code - {0}", response.getStatusLine().getStatusCode());
        return response.getStatusLine().getStatusCode();
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
                logger.log(Level.INFO, "TextForUser: {0}", text);
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

}
