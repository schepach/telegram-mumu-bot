package ru.mumu.bot.entity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.bot.cache.Caching;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CafeMumuEntity extends AbstractCafe {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private static final Pattern PATTERN_MENU = Pattern.compile("([а-яА-я]*\\s?-?([а-яА-я]*)?,?){1,}(\\([а-яА-Я]*(,\\s?[а-яА-Я]*){1,}\\))?,?\\s?([а-яА-я]*\\s?-?([а-яА-я]*)?,?){1,}");
    private static final Pattern PATTERN_MENU_REPLACE = Pattern.compile("(\\([а-яА-Я]*(,\\s?[а-яА-Я]*){1,}\\))");
    private final String command;

    public CafeMumuEntity() {
        this.command = null;
    }

    public CafeMumuEntity(String command) {
        this.command = command;
    }

    @Override
    public String getMenu() {
        String lunchInfo;
        // Если кэширование не завершено, сообщаем пользователю, что идет обработка меню
        if (Caching.URL_MAP == null
                || Caching.URL_MAP.isEmpty()
                || Caching.URL_MAP.get("done") == null
                || Caching.URL_MAP.get("done").isEmpty()) {
            logger.log(Level.SEVERE, "Caching in progress...");
            return Constants.CACHING_MESSAGE_FOR_USER;
        }
        // Получаем список url'ов с меню
        List<String> urlList = getUrlList(command);

        if (urlList == null) {
            return Constants.UNEXPECTED_ERROR;
        }

        // По этим url'ам получаем меню
        lunchInfo = getLunches(urlList);

        // Возвращаем меню
        return lunchInfo;
    }

    @Override
    public String getAddresses() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Utils.connectToURL(Constants.ADDRESSES_URL);
            Document doc = Jsoup.connect(Constants.ADDRESSES_URL).get();
            Elements elements = doc.select("div");

            for (Element elem : elements) {

                if (elem.select("div").attr("id").equals("bp_data-39139909_120")) {

                    String text = elem.select("div").attr("id", "bp_data-39139909_120")
                            .attr("class", "bp_text").first().text();

                    text = text.substring(text.indexOf("1)"), text.lastIndexOf("contacts") + 8).trim();
                    String[] splitText = text.split("\\d\\d?\\)");

                    int count = 1;

                    for (String item : splitText) {

                        if (item.isEmpty()) continue;

                        if (count == 37) {
                            String[] lastItem = item.split("\\. ");
                            stringBuilder.append(String.valueOf(count).concat(".").concat(lastItem[0])).append("\n");
                            stringBuilder.append(lastItem[1]);
                            break;
                        }
                        stringBuilder.append(String.valueOf(count).concat(".").concat(item)).append("\n");
                        count++;
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        return Constants.ADDRESSES_TEXT.concat(stringBuilder.toString());
    }

    private List<String> getUrlList(String command) {

        List<String> urlList = new ArrayList<>();
        try {

            for (Map.Entry<String, String> entry : Caching.URL_MAP.entrySet()) {

                Document doc = Jsoup.parse(entry.getValue());
                Elements elements = doc.select("h1");

                for (Element item : elements) {
                    if (item.text().contains("понедельник") && command.equals(Constants.MONDAY)
                            || (item.text().contains("вторник") && command.equals(Constants.TUESDAY))
                            || (item.text().contains("среда") && command.equals(Constants.WEDNESDAY))
                            || (item.text().contains("четверг") && command.equals(Constants.THURSDAY))
                            || (item.text().contains("пятница") && command.equals(Constants.FRIDAY))) {

                        urlList.add(entry.getKey());
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception from getUrlList method: ", ex);
            return null;
        }

        return urlList;
    }

    private String getLunches(List<String> urls) {

        // Если отсутствует url для текущего дня недели, то ланчей на текущий день нет
        // Возможно, потому что праздник или выходной день
        // UPD от 26.10.2020 - Теперь на сайте доступно меню только на текущий день
        if (urls == null || urls.isEmpty()) {
            return "На этот день меню неизвестно";
        }

        String price = null;
        String caption = null;
        StringBuilder stringBuilder = new StringBuilder();
        int countLunchItem = 0;

        stringBuilder.append(Constants.TIME_LUNCH.concat("\n").concat("\uD83E\uDD57\uD83C\uDF72\uD83C\uDF5D\uD83E\uDD64"));

        try {

            for (String keyOfMap : urls) {

                String value = Caching.URL_MAP.get(keyOfMap);
                Document doc = Jsoup.parse(value);
                Elements elements = doc.select("div");

                for (Element element : elements) {

                    // Get caption
                    if (countLunchItem <= 1) {
                        if (element.attr("class").equals("food-container")) {
                            if (element.select("h1") != null
                                    && !element.select("h1").isEmpty()) {
                                caption = element.select("h1").first().text();
                                countLunchItem++;
                                logger.log(Level.INFO, "caption = " + caption);
                                logger.log(Level.INFO, "countLunchItem = " + countLunchItem);
                            }
                        }
                    }

                    // Get Price
                    if (price == null || price.isEmpty()) {
                        if (element.attr("class").equals("price")) {
                            price = element.text().substring(0, 3);
                            logger.log(Level.INFO, "price = " + price);
                            stringBuilder.insert(0, "Стоимость обеда: ".concat(price).concat(" руб.\n"));
                        }
                    }

                    // Get Menu Items
                    if (element.attr("class").equals("info-item js-compositions")) {
                        logger.log(Level.INFO, "element.text() = " + element.text());
                        String temp = element.text().replaceAll("\"", "");
                        String menu = null;
                        Matcher matcher = PATTERN_MENU.matcher(temp);
                        // Find substring on pattern
                        if (matcher.find()) {
                            menu = matcher.group();
                        }
                        logger.log(Level.INFO, "menu = " + menu);

                        if (menu == null || menu.isEmpty()) {
                            logger.log(Level.SEVERE, "menu is null or is empty!");
                            return Constants.UNEXPECTED_ERROR;
                        }
                        // Replace elements from menu, which match of pattern and point by empty
                        menu = menu.replaceAll(PATTERN_MENU_REPLACE.pattern(), "").replaceAll("\\.", "");

                        String[] menuItems = menu.split(",");

                        stringBuilder.append("\n").append(caption).append(": \n");
                        int count = 1;
                        for (String item : menuItems) {
                            logger.log(Level.INFO, "menuItem = " + item);
                            stringBuilder.append(count).append(". ").append(item.trim().concat("\n"));
                            count++;
                        }
                        break;
                    }
                }
            }
            logger.log(Level.INFO, "LUNCH_MUMU: \n" + stringBuilder.toString());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        // Перестраховка, если есть url с ланчем, но в нем нет информации по меню
        if (stringBuilder.toString().isEmpty())
            return Constants.INFO_HOLIDAY_DAY;

        return stringBuilder.toString();
    }

}
