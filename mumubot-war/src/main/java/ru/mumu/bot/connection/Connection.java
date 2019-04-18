package ru.mumu.bot.connection;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.BotHelper;

import java.io.IOException;
import java.util.*;

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    public static Map<String, String> URL_MAP = new HashMap<>();

    public static String sendRequest(String command) {
        String[] url = new String[2];
        switch (command) {
            case Constants.VICTORIA:
                url[0] = Constants.VICTORIA_URL;
                return getVictoriaLunch(url[0]);
            case Constants.ADDRESSES:
                url[0] = Constants.ADDRESSES_URL;
                return getMumuAddresses(url[0]);
            default:
                return Constants.BAD_COMMAND.concat(command);
        }
    }

    public static String getListUrl(String command) {

        if (URL_MAP == null
                || URL_MAP.isEmpty()
                || URL_MAP.get("done") == null
                || URL_MAP.get("done").isEmpty()) {
            System.out.println(Constants.CACHING_MESSAGE);
            return Constants.CACHING_MESSAGE;
        }

        if (URL_MAP.get("lunchInfoHoliday") != null
                && !URL_MAP.get("lunchInfoHoliday").isEmpty()) {

            LOGGER.info("Today is Holiday!");
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

            return getMumuLunch(listURLS, URL_MAP.get("lunchInfo"));

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
        }

        return "";
    }

    private static String getMumuLunch(List<String> urls, String dateInfo) throws IOException {

        String price = "";
        String caption = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.TIME_LUNCH).append("\n").append("\uD83C\uDF74".concat(dateInfo.trim()).concat("\uD83C\uDF7D").toUpperCase());

        try {

            for (String keyOfMap : urls) {

                String value = URL_MAP.get(keyOfMap);
                Document doc = Jsoup.parse(value);
                Elements elements = doc.select("div");

                for (Element element : elements) {

                    // Get caption
                    if (element.attr("class").equals("food-container")) {
                        if (element.select("h1") != null
                                && !element.select("h1").isEmpty()) {
                            caption = element.select("h1").first().text();
                            if (!caption.isEmpty()) {
                                continue;
                            }
                        }
                    }

                    // Get Price
                    if (element.attr("class").equals("price")) {
                        price = element.text().substring(0, 3);
                        if (!price.isEmpty()) {
                            continue;
                        }
                        stringBuilder.insert(0, price).append("\n");
                    }

                    // Get Menu Items
                    if (element.attr("class").equals("info-item js-compositions")) {
                        String[] arrStr = element.text().substring(0, element.text().indexOf("ассорти") + 12).split(",");
                        stringBuilder.append("\n").append(caption).append(": ").append("\n");
                        int count = 1;
                        for (String s : arrStr) {

                            if (s.equals("морковь)")) continue;

                            if (s.contains("(горошек")) {
                                s = s.replaceAll("\\(горошек", "");
                            }
                            if (s.contains("(перец")) {
                                s = s.replaceAll("\\(перец", "");
                            }
                            if (BotHelper.checkString(s.trim())) {
                                continue;
                            }
                            stringBuilder.append(count).append(". ").append(s.trim()).append("\n");
                            count++;
                        }
                        break;
                    }
                }
            }
            LOGGER.info("LUNCH_MUMU: \n" + stringBuilder.toString());
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        return stringBuilder.toString();
    }

    private static String getVictoriaLunch(String url) {

        String lunchItems = "";
        String date = "";
        String info = "\uD83C\uDF74".concat("Бизнес ланч на ");
        String elemMenu = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {

            getResponseCode(url);
            Document doc = Jsoup.connect(url).get();

            stringBuilder.append(Constants.VICTORIA_TEXT).append(Constants.TIME_LUNCH + "\n").append(info);

            Elements elements = doc.select("td");

            int count = 1;
            for (Element item : elements) {

                if (item.attr("class").equals("text")) {
                    date = item.text();
                    if (!date.isEmpty()) {
                        stringBuilder.append(date).append("\uD83C\uDF7D").append("\n");
                        stringBuilder.append("\n");
                    }
                }
                if (item.attr("class").equals("mdish") || item.attr("class").equals("mtext")) {
                    elemMenu = item.text();
                    stringBuilder.append(count).append(". ").append(elemMenu).append("\n");
                    count++;
                }
            }
            LOGGER.info("Date: " + date);
            LOGGER.info("LUNCH_VICTORIA: \n" + lunchItems);

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }
        return stringBuilder.toString();
    }

    private static String getMumuAddresses(String url) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            getResponseCode(url);
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
            LOGGER.log(Level.ERROR, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }

        return Constants.ADDRESSES_TEXT.concat(stringBuilder.toString());
    }

    private static void getResponseCode(String url) throws IOException {
        LOGGER.info("ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());
    }

    public static String cachingUrls(String url) {

        try {
            getResponseCode(Constants.MUMU_MAIN_PAGE_URL);
            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();
            Elements elements = doc.select("a");

            boolean isNotHoliday = false;
            String lunchInfo = null;
            StringBuilder lunchInfoHoliday = new StringBuilder();

            for (Element elem : elements) {

                if (!elem.attr("href").contains("/catalog/lanchi")) {
                    continue;
                }

                lunchInfo = elem.select("div").text();

                if (lunchInfo == null || lunchInfo.isEmpty()) {
                    LOGGER.log(Level.ERROR, "lunchInfo from cachingUrls method is null or is empty!");
                    LOGGER.log(Level.ERROR, "Break caching pages ...");
                    return Constants.UNEXPECTED_ERROR;
                }

                try {
                    isNotHoliday = BotHelper.checkDayOfMonth(lunchInfo.replace(" ", ""), Calendar.getInstance().getTime());
                    LOGGER.info("isNotHoliday = " + isNotHoliday);
                } catch (ParseException ex) {
                    return Constants.UNEXPECTED_ERROR;
                }
                if (isNotHoliday) {
                    url = elem.attr("abs:href");
                    LOGGER.info("UrlWithLunches = " + url);
                    break;
                } else {
                    lunchInfoHoliday.append("\n").append(lunchInfo).append("\n");
                }
            }

            if (!isNotHoliday) {
                URL_MAP.put("lunchInfoHoliday", lunchInfoHoliday.toString());
                return "holiday";
            }

            getResponseCode(url);
            doc = Jsoup.connect(url).get();
            elements = doc.select("div");

            for (Element item : elements) {

                if (!item.attr("class").equals("food-item spec-block")) {
                    continue;
                }
                url = item.select("a").attr("abs:href");
                URL_MAP.put(url, Jsoup.connect(url).get().html());
                URL_MAP.put("lunchInfo", lunchInfo);
            }

            for (Map.Entry<String, String> entry : URL_MAP.entrySet()) {
                System.out.println("UrlOfLunch = " + entry.getKey());
            }

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception: ", ex);
            LOGGER.log(Level.ERROR, "Caching url " + url + " again ...");
            cachingUrls(url);
        }

        return "caching";
    }
}