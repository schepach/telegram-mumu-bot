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
            "I have next commands: \n" +
                    "/today - меню на сегодня\n" +
                    "/monday - понедельник\n" +
                    "/tuesday - вторник\n" +
                    "/wednesday - среда\n" +
                    "/thursday - четверг\n" +
                    "/friday - пятница\n" +
                    "/addresses - адреса кафе му-му\n" +
                    "/victoria - кафе \"Виктория\"\n" +
                    "/help - помощь\n" +
                    "Command '/victoria' for lunch in Grand Victoria cafe \uD83D\uDE0A \n" +
                    "Feedback(Обратная связь): @schepach";

    public static final String TIME_LUNCH = "Время обеда: 12:00-16:00";
    public static final String ERROR_OTHER_INPUT = "bad command! \n If I can help you - enter /help";
    public static final String ERROR_HOLIDAY_DAY = "today is holiday! The menu is only from Monday to Friday.";
    public static final String INFO_HOLIDAY_DAY = "В праздничный/выходной день ланчей нет\uD83D\uDE10";
    public static final String UNEXPECTED_ERROR = "\uD83D\uDE31Что-то пошло не так, попробуйте повторить попытку позднее";
    public static final String ADDRESSES_TEXT = "cafe Mumu address: \n";
    public static final String CAFE_MUMU_TEXT = "in Mumu cafe for you: \n\n";
    public static final String START_TEXT = "if you want to eat - enter the command. \n If I can help you - enter /help";
    public static final String VICTORIA_TEXT = "in Grand Victoria cafe for you: \n\n";

    public static final String VICTORIA_URL = "http://restaurantgrandvictoria.ru/lunch";
    public static final String ADDRESSES_URL = "https://vk.com/topic-39139909_28681126";
    public static final String MUMU_MAIN_PAGE_URL = "http://cafemumu.ru/catalog/";

    // Scheduling Broadcasting time
    public static final LocalTime START_BROADCASTING = LocalTime.of(11, 0, 0);
    public static final LocalTime END_BROADCASTING = LocalTime.of(12, 0, 0);

    // Scheduling caching time
    // Caching only when server is restart (so far)
    // public static final LocalTime START_TIME = LocalTime.of(0, 0, 0);
    // public static final LocalTime END_TIME = LocalTime.of(23, 59, 59);
    public static final String CACHING_MESSAGE_FOR_USER = "В данный момент идет обработка меню. Пожалуйста, подождите⏳";

}
