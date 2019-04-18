package ru.mumu.bot.constants;

import java.time.LocalTime;

public class Constants {

    public static final String HELP = "/help";
    public static final String START = "/start";
    public static final String TODAY = "/today";
    public static final String MONDAY = "/monday";
    public static final String TUESDAY = "/tuesday";
    public static final String WEDNESDAY = "/wednesday";
    public static final String THURSDAY = "/thursday";
    public static final String FRIDAY = "/friday";
    public static final String ADDRESSES = "/addresses";
    public static final String VICTORIA = "/victoria";

    public static final String HELP_TEXT =
            "I have next commands:\n" +
                    "/today\n" +
                    "/monday\n" +
                    "/tuesday\n" +
                    "/wednesday\n" +
                    "/thursday\n" +
                    "/friday\n" +
                    "/addresses\n" +
                    "/victoria\n" +
                    "/help\n" +
                    "Command '/victoria' for lunch in Grand Victoria cafe :)";

    public static final String BAD_COMMAND = "Bad command! ";
    public static final String TIME_LUNCH = "Время обеда: 12:00-16:00";
    public static final String ERROR_OTHER_INPUT = "bad command! \n If I can help you - enter /help";
    public static final String ERROR_HOLIDAY_DAY = "today is holiday! The menu will be from Monday to Friday.";
    public static final String UNEXPECTED_ERROR = "Что-то пошло не так, попробуйте повторить попытку позднее...";
    public static final String ADDRESSES_TEXT = "cafe Mumu address: \n";
    public static final String CAFE_MUMU_TEXT = "in Mumu cafe for you: \n\n";
    public static final String START_TEXT = "if you want eat - enter command. \n If I can help you - enter /help";
    public static final String VICTORIA_TEXT = "in Grand Victoria cafe for you: \n\n";

    public static final String VICTORIA_URL = "http://restaurantgrandvictoria.ru/lunch";
    public static final String ADDRESSES_URL = "https://vk.com/topic-39139909_28681126";
    public static final String MUMU_MAIN_PAGE_URL = "http://cafemumu.ru/catalog/";

    public static final LocalTime START_TIME = LocalTime.of(0, 0, 0);
    public static final LocalTime END_TIME = LocalTime.of(23, 59, 59);
    public static final String CACHING_MESSAGE = "В данный момент идет кэширование. Пожалуйста, подождите...";

}
