package ru.mumu.bot.utils;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mumu.bot.connection.Connection;
import ru.mumu.bot.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotHelper {

    private static final Logger LOGGER = Logger.getLogger(BotHelper.class.getSimpleName());
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
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
                LOGGER.info("TextForUser: " + Constants.HELP_TEXT);
                return Constants.HELP_TEXT;
            case Constants.START:
                LOGGER.info("TextForUser: " + command);
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
                LOGGER.info("TextForUser: " + Constants.ERROR_OTHER_INPUT);
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
                LOGGER.info("TextForUser: " + text);
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

    public static Boolean checkDayOfMonth(String groupInfo, Date dateCurrent) throws Exception {
        LOGGER.info("Check groupInfo = " + groupInfo);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] arrGroupInfo = groupInfo.split("");
        String dateStart = arrGroupInfo[6] + arrGroupInfo[7] + "/" + arrGroupInfo[9] + arrGroupInfo[10] + "/" + currentYear;
        String dateEnd = arrGroupInfo[16] + arrGroupInfo[17] + "/" + arrGroupInfo[19] + arrGroupInfo[20] + "/" + currentYear;

        Date startDate;
        Date endDate;

        try {
            startDate = convertStringToDate(dateStart.replace(".", ""));
            endDate = convertStringToDate(dateEnd.replace(".", ""));
        } catch (ParseException ex) {
            throw new ParseException(Constants.UNEXPECTED_ERROR, ex.getErrorOffset());
        }

        LOGGER.info("CurrentDate = " + dateCurrent);
        LOGGER.info("StartDate  = " + startDate);
        LOGGER.info("EndDate  = " + endDate);

        return dateCurrent.after(startDate) && dateCurrent.before(endDate) || dateCurrent.toString().equals(endDate.toString())
                || dateCurrent.getTime() > endDate.getTime();
    }

    private static Date convertStringToDate(String strDate) throws ParseException {
        Date date = null;

        try {
            date = FORMATTER.parse(strDate);
            LOGGER.info("Date from converter: " + date);

        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new ParseException(ex.getMessage(), ex.getErrorOffset());
        }
        return date;
    }

    private static String checkCommandToday(String currentDay, String messageDay) {
        LOGGER.info("currentDay:  " + currentDay);
        LOGGER.info("messageDay:  " + messageDay);

        if (currentDay.toLowerCase().equals("saturday") || currentDay.toLowerCase().equals("sunday")) {
            return Constants.ERROR_HOLIDAY_DAY;
        } else {
            if (currentDay.equals(messageDay)) {
                String weekDay = "/".concat(currentDay).toLowerCase();
                LOGGER.info("WeekDay is : " + weekDay);
                return Connection.getListUrl(weekDay);
            } else {
                LOGGER.info("Days are not equals!");
                return null;
            }
        }
    }

    public static boolean checkString(String str) {
        Matcher m = PATTERN.matcher(str);
        return m.matches();
    }
}
