package ru.mumu.utils;

/**
 * Created by alexeyoblomov on 03.12.16.
 */
public class BotHelper {

    public static String checkDayOfWeek(String day) {

        switch (day) {
            case Constants.MONDAY:
                day = Constants.MONDAY;
                break;
            case Constants.TUESDAY:
                day = Constants.TUESDAY;
                break;
            case Constants.WEDNESDAY:
                day = Constants.WEDNESDAY;
                break;
            case Constants.THURSDAY:
                day = Constants.THURSDAY;
                break;
            case Constants.FRIDAY:
                day = Constants.FRIDAY;
                break;
        }
        return day;
    }
}
