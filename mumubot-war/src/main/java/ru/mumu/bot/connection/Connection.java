package ru.mumu.bot.connection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.bot.constants.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    public static Map<String, String> URL_MAP = new HashMap<>();
    private static final Pattern PATTERN_DATE = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\d\\d");
    private static final Pattern PATTERN_MENU = Pattern.compile("^([а-яА-я]*\\s?-?([а-яА-я]*)?,?){1,}(\\([а-яА-Я]*(,\\s?[а-яА-Я]*){1,}\\))?,?\\s?([а-яА-я]*\\s?-?([а-яА-я]*)?,?){1,}\\.");
    private static final Pattern PATTERN_MENU_REPLACE = Pattern.compile("(\\([а-яА-Я]*(,\\s?[а-яА-Я]*){1,}\\))");

    public static String getListUrl(String command) {

        if (URL_MAP == null
                || URL_MAP.isEmpty()
                || URL_MAP.get("done") == null
                || URL_MAP.get("done").isEmpty()) {
            LOGGER.log(Level.SEVERE, "Caching in progress...");
            return Constants.CACHING_MESSAGE_FOR_USER;
        }

        if (URL_MAP.get("lunchInfoHoliday") != null
                && !URL_MAP.get("lunchInfoHoliday").isEmpty()) {
            LOGGER.log(Level.SEVERE, "Today is Holiday");
            return "Ближайшие: " + URL_MAP.get("lunchInfoHoliday");
        }

        List<String> listURLS = new ArrayList<>();
        try {

            for (Map.Entry<String, String> entry : URL_MAP.entrySet()) {

                Document doc = Jsoup.parse(entry.getValue());
                Elements elements = doc.select("h1");

                for (Element item : elements) {
                    if (item.text().contains("понедельник") && command.equals(Constants.MONDAY)
                            || (item.text().contains("вторник") && command.equals(Constants.TUESDAY))
                            || (item.text().contains("среда") && command.equals(Constants.WEDNESDAY))
                            || (item.text().contains("четверг") && command.equals(Constants.THURSDAY))
                            || (item.text().contains("пятница") && command.equals(Constants.FRIDAY))) {

                        listURLS.add(entry.getKey());
                        break;
                    }
                }
            }

            return getMumuLunch(listURLS);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
        }

        return "";
    }

    private static String getMumuLunch(List<String> urls) {

        String price = null;
        String caption = null;
        StringBuilder stringBuilder = new StringBuilder();
        int countLunchItem = 0;

        stringBuilder.append(Constants.TIME_LUNCH.concat("\n").concat("\uD83E\uDD57\uD83C\uDF72\uD83C\uDF5D\uD83E\uDD64"));

        try {

            //Если отсутствует url для текущего дня недели, то ланчей на текущий день нет
            // Возможно, потому что праздник или выходной день
            if (urls == null || urls.isEmpty()) {
                return Constants.INFO_HOLIDAY_DAY;
            }

            for (String keyOfMap : urls) {

                String value = URL_MAP.get(keyOfMap);
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
                                LOGGER.log(Level.INFO, "caption = " + caption);
                                LOGGER.log(Level.INFO, "countLunchItem = " + countLunchItem);
                            }
                        }
                    }

                    // Get Price
                    if (price == null || price.isEmpty()) {
                        if (element.attr("class").equals("price")) {
                            price = element.text().substring(0, 3);
                            LOGGER.log(Level.INFO, "price = " + price);
                            stringBuilder.insert(0, "Стоимость обеда: ".concat(price).concat(" руб.\n"));
                        }
                    }

                    // Get Menu Items
                    if (element.attr("class").equals("info-item js-compositions")) {
                        LOGGER.log(Level.INFO, "element.text() = " + element.text());
                        String menu = null;
                        Matcher matcher = PATTERN_MENU.matcher(element.text());
                        // Find substring on pattern
                        if (matcher.find()) {
                            menu = matcher.group();
                        }
                        LOGGER.log(Level.INFO, "menu = " + menu);

                        if (menu == null || menu.isEmpty()) {
                            LOGGER.log(Level.SEVERE, "menu is null or is empty!");
                            return Constants.UNEXPECTED_ERROR;
                        }
                        // Replace elements from menu, which match of pattern and point by empty
                        menu = menu.replaceAll(PATTERN_MENU_REPLACE.pattern(), "").replaceAll("\\.", "");

                        String[] menuItems = menu.split(",");

                        stringBuilder.append("\n").append(caption).append(": \n");
                        int count = 1;
                        for (String item : menuItems) {
                            LOGGER.log(Level.INFO, "menuItem = " + item);
                            stringBuilder.append(count).append(". ").append(item.trim().concat("\n"));
                            count++;
                        }
                        break;
                    }
                }
            }
            LOGGER.log(Level.INFO, "LUNCH_MUMU: \n" + stringBuilder.toString());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        // Перестраховка, если есть url с ланчем, но в нем нет информации по меню
        if (stringBuilder.toString().isEmpty())
            return Constants.INFO_HOLIDAY_DAY;

        return stringBuilder.toString();
    }

    public static String getVictoriaLunch(String url) {

        String lunchItems = "";
        String date = "";
        String info = "Бизнес ланч на ";
        String elemMenu = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {

            connectToURL(url);
            Document doc = Jsoup.connect(url).get();

            stringBuilder.append(Constants.VICTORIA_TEXT).append(Constants.TIME_LUNCH + "\n").append(info);

            // Get items in <div class="container">
            Elements elementsDate = doc.select("div").attr("class", "container");

            for (Element itemDate : elementsDate) {

                // Get date of lunch
                // If item (tag h3) not null/is empty and doesn't match of pattern - get next element
                if (itemDate.select("h3") != null
                        && itemDate.select("h3").text() != null
                        && !itemDate.select("h3").text().isEmpty()) {
                    Matcher matcher = PATTERN_DATE.matcher(itemDate.select("h3").text());

                    if (matcher.matches()) {
                        date = itemDate.select("h3").text();
                        break;
                    }
                }
            }

            stringBuilder.append(date).append("\uD83E\uDD57\uD83C\uDF72\uD83C\uDF5D\uD83E\uDD64\n");

            //Get items of lunch
            Elements elements = doc.select("div").attr("class", "menu-section").select("tr");

            int count = 1;
            for (Element item : elements) {

                if (item == null
                        || !item.hasText()
                        || item.text() == null
                        || item.text().isEmpty()) {
                    continue;
                }

                elemMenu = item.select("td").select("span").first().text();
                stringBuilder.append(count).append(". ").append(elemMenu).append("\n");
                count++;

            }
            LOGGER.log(Level.INFO, "Date: " + date);
            LOGGER.log(Level.INFO, "LUNCH_VICTORIA: \n" + lunchItems);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }
        return stringBuilder.toString();
    }

    public static String getMumuAddresses(String url) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            connectToURL(url);
            Document doc = Jsoup.connect(url).get();
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
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        return Constants.ADDRESSES_TEXT.concat(stringBuilder.toString());
    }

    private static void connectToURL(String url) throws IOException {
        LOGGER.log(Level.SEVERE, "ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.log(Level.SEVERE, "Response Code: " + response.getStatusLine().getStatusCode());
    }

    public static String cachingUrls(String url) {

        try {
            connectToURL(Constants.MUMU_MAIN_PAGE_URL);
            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();
            Elements elements = doc.select("a");
            String lunchInfo;

            for (Element elem : elements) {

                if (!elem.attr("href").contains("/catalog/lanchi")) {
                    continue;
                }

                lunchInfo = elem.select("div").text();

                if (lunchInfo == null || lunchInfo.isEmpty()) {
                    LOGGER.log(Level.SEVERE, "lunchInfo from cachingUrls method is null or is empty");
                    LOGGER.log(Level.SEVERE, "Break caching pages ...");
                    return Constants.UNEXPECTED_ERROR;
                }
                url = elem.attr("abs:href");
                LOGGER.log(Level.SEVERE, "UrlWithLunches = " + url);
                break;
            }

            // Get url list for lunches
            connectToURL(url);
            doc = Jsoup.connect(url).get();
            elements = doc.select("div");

            for (Element item : elements) {
                if (!item.attr("class").equals("food-item spec-block product-labels-wrapper")) {
                    continue;
                }
                url = item.select("a").attr("abs:href");
                URL_MAP.put(url, Jsoup.connect(url).get().html());
            }

            if (URL_MAP == null || URL_MAP.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Произошла непредвиденная ошибка: URL_MAP is null or is empty");
                return Constants.UNEXPECTED_ERROR;
            }

            for (Map.Entry<String, String> entry : URL_MAP.entrySet()) {
                LOGGER.log(Level.INFO, "UrlOfLunch = " + entry.getKey());
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            LOGGER.log(Level.SEVERE, "Caching url " + url + " again ...");
            cachingUrls(url);
        }

        return "caching";
    }
}