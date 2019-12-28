package ru.mumu.bot.utils;

import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.model.CafeMumu;
import ru.mumu.bot.model.CafeVictoria;
import ru.mumu.bot.redis.RedisManager;

public class BotHelper {

    public static String getInfo(String command, String chatId, String messageDay, String currentDay) {
        RedisManager.checkRedisStore(chatId);
        return getInfo(command, messageDay, currentDay);
    }

    public static String getInfo(String command, String messageDay, String currentDay) {

        String info = checkCommand(command, messageDay, currentDay);

        if (info == null)
            return Constants.UNEXPECTED_ERROR;

        return info;
    }

    private static String checkCommand(String command, String messageDay, String currentDay) {

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
                return new CafeMumu(command).getMenu();
            case Constants.VICTORIA:
                return new CafeVictoria().getMenu();
            case Constants.ADDRESSES:
                return new CafeMumu().getAddresses();
            case Constants.TODAY:
                return new CafeMumu(Utils.checkCommandToday(currentDay, messageDay)).getMenu();
            default:
                return Constants.ERROR_OTHER_INPUT;
        }
    }

    public static String checkDayForHoliday(String command, String today) {
        if (today.equals("Saturday") || today.equals("Sunday")) {
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
                    return new CafeMumu().getAddresses();
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
