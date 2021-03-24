package ru.mumu.bot.utils;

import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.entity.CafeMumuEntity;
import ru.mumu.bot.entity.CafeVictoriaEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BotHelper {

    public static String getInfo(String command) {

        String info = checkCommand(command);

        if (info == null)
            return Constants.UNEXPECTED_ERROR;

        return info;
    }

    private static String checkCommand(String command) {

        switch (command) {
            case Constants.HELP:
                return Constants.HELP_TEXT;
            case Constants.START:
                return Constants.START_TEXT;
            case Constants.MONDAY:
            case Constants.TUESDAY:
            case Constants.WEDNESDAY:
            case Constants.THURSDAY:
            case Constants.FRIDAY:
                return new CafeMumuEntity(command).getMenu();
            case Constants.VICTORIA:
                return new CafeVictoriaEntity().getMenu();
            case Constants.ADDRESSES:
                return new CafeMumuEntity().getAddresses();
            case Constants.TODAY:
                Calendar calendar = Calendar.getInstance();
                String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
                return new CafeMumuEntity("/".concat(today.toLowerCase())).getMenu();
            default:
                return Constants.ERROR_OTHER_INPUT;
        }
    }

    public static String checkDayForHoliday(String command, String today) {
        if (today.toLowerCase().equals("saturday") || today.toLowerCase().equals("sunday")) {
            switch (command) {
                case Constants.MONDAY:
                case Constants.TUESDAY:
                case Constants.WEDNESDAY:
                case Constants.THURSDAY:
                case Constants.FRIDAY:
                case Constants.VICTORIA:
                case Constants.TODAY:
                    return Constants.ERROR_HOLIDAY_DAY;
                case Constants.ADDRESSES:
                    return new CafeMumuEntity().getAddresses();
                case Constants.START:
                    return Constants.START_TEXT;
                case Constants.HELP:
                    return Constants.HELP_TEXT;
                default:
                    return Constants.ERROR_OTHER_INPUT;
            }
        }
        return null;
    }
}
