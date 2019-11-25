package ru.mumu.bot.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mumu.bot.connection.Connection;
import ru.mumu.bot.constants.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotHelper {

    private static final Logger LOGGER = Logger.getLogger(BotHelper.class.getSimpleName());
    private static final Pattern PATTERN = Pattern.compile("горошек|кукуруза\\)");

    public static String getLunchInfo(String command, String messageDay, String currentDay) {

        String lunchInfo = checkCommand(command, messageDay, currentDay);

        if (lunchInfo == null)
            return Constants.UNEXPECTED_ERROR;

        return lunchInfo;

    }

    private static String checkCommand(String command, String messageDay, String currentDay) {

        switch (command) {
            case Constants.HELP:
                LOGGER.log(Level.INFO, "TextForUser: " + Constants.HELP_TEXT);
                return Constants.HELP_TEXT;
            case Constants.START:
                LOGGER.log(Level.INFO, "TextForUser: " + command);
                return Constants.START_TEXT;
            case Constants.MONDAY:
            case Constants.TUESDAY:
            case Constants.WEDNESDAY:
            case Constants.THURSDAY:
            case Constants.FRIDAY:
                return Connection.getListUrl(command);
            case Constants.VICTORIA:
            case Constants.ADDRESSES:
                return Connection.sendRequest(command);
            case Constants.TODAY:
                return checkCommandToday(currentDay, messageDay);
            default:
                LOGGER.log(Level.INFO, "TextForUser: " + Constants.ERROR_OTHER_INPUT);
                return Constants.ERROR_OTHER_INPUT;
        }
    }

    public static String getTextForUser(Message message, String text) {

        String userName = "";
        String textForUser;

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
            case Constants.UNEXPECTED_ERROR:
                LOGGER.log(Level.INFO, "TextForUser: " + text);
                textForUser = userName.concat(", ").concat(text);
                break;
            case Constants.START_TEXT:
                textForUser = "Hey, ".concat(userName.concat(", ").concat(text));
                break;
            default:
                textForUser = "\uD83D\uDCE2 " + userName.concat(", ").concat(Constants.CAFE_MUMU_TEXT).concat(text);
                break;
        }

        if (text.contains(Constants.ADDRESSES_TEXT) || text.contains(Constants.VICTORIA_TEXT)) {
            textForUser = "\uD83D\uDCE2 " + userName.concat(", ").concat(text);
        }

        return textForUser;
    }

    private static String checkCommandToday(String currentDay, String messageDay) {

        LOGGER.log(Level.INFO, "currentDay:  " + currentDay);
        LOGGER.log(Level.INFO, "messageDay:  " + messageDay);

        if (currentDay == null || currentDay.isEmpty()
                || messageDay == null || messageDay.isEmpty()) {
            LOGGER.log(Level.SEVERE, "currentDay or messageDay is null or is empty");
            return null;
        }

        if (currentDay.toLowerCase().equals("saturday")
                || currentDay.toLowerCase().equals("sunday")) {
            LOGGER.log(Level.SEVERE, "currentDay is saturday or sunday");
            return Constants.ERROR_HOLIDAY_DAY;
        }

        if (!currentDay.equals(messageDay)) {
            LOGGER.log(Level.SEVERE, "Days are not equals");
            return null;
        }

        String weekDay = "/".concat(currentDay).toLowerCase();
        LOGGER.log(Level.INFO, "WeekDay is : " + weekDay);
        return Connection.getListUrl(weekDay);
    }

    public static boolean checkString(String str) {
        Matcher m = PATTERN.matcher(str);
        return m.matches();
    }
}
