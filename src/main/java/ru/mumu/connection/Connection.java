package ru.mumu.connection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.constants.Constants;
import ru.mumu.utils.helper.BotHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 08.08.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());

    public static String sendRequest(String command) throws IOException {
        String url[] = new String[2];
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


    public static String sendRequest(Date dateCurrent, String command) {

        try {

            getResponseCode(Constants.MUMU_MAIN_PAGE_URL);
            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();
            Elements elements = doc.select("a");

            boolean flag = false;
            StringBuilder lunchInfo = new StringBuilder();
            String groupInfo = "";
            String url = "";

            for (Element item : elements) {

                if (!item.attr("href").contains("lanchi")) {
                    continue;
                }

                groupInfo = item.select("div").text();
                flag = BotHelper.checkDayOfMonth(groupInfo.replace(" ", ""), dateCurrent);
                LOGGER.info("FLAG = " + flag);

                if (flag) {
                    url = item.attr("abs:href");
                    LOGGER.info("URL = " + url);
                    break;
                }
            }

            lunchInfo.append("\n").append(groupInfo).append("\n");
            LOGGER.info("LunchInfo = " + lunchInfo.toString());

            if (flag) {
                LOGGER.info("DateInfo: " + lunchInfo);
                LOGGER.info("Get lunches...");
                return getListUrl(url, command, lunchInfo.toString());
            } else {
                LOGGER.info("Today is Holiday!");
                return "Ближайшие: " + lunchInfo;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }
    }


    private static String getListUrl(String url, String command, String dateInfo) {
        List<String> listURLS = new ArrayList<>();

        try {

            getResponseCode(url);
            Document doc = Jsoup.connect(url).get();

            String title = "";
            Elements elements = doc.select("div");

            for (Element item : elements) {

                if (!item.attr("class").equals("elem double")) {
                    continue;
                }

                title = (item.select("a").attr("title"));

                if (title.contains("понедельник") && command.equals(Constants.MONDAY)
                        || (title.contains("вторник") && command.equals(Constants.TUESDAY))
                        || (title.contains("среда") && command.equals(Constants.WEDNESDAY))
                        || (title.contains("четверг") && command.equals(Constants.THURSDAY))
                        || (title.contains("пятница") && command.equals(Constants.FRIDAY))) {

                    listURLS.add(item.select("a").attr("abs:href"));
                }
            }

            return getMumuLunch(listURLS, dateInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getMumuLunch(List<String> urls, String dateInfo) throws IOException {

        String price = "";
        String h1;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.TIME_LUNCH).append("\n").append("\uD83C\uDF74".concat(dateInfo.trim()).concat("\uD83C\uDF7D").toUpperCase());


        try {

            for (String strURL : urls) {

                if (strURL == null || strURL.isEmpty()) {
                    continue;
                }

                getResponseCode(strURL);
                Document doc = Jsoup.connect(strURL).get();
                Elements elements = doc.select("div");


                for (Element element : elements) {

                    if (element.attr("class").equals("price")) {
                        if (!price.isEmpty()) {
                            continue;
                        }
                        price = "Стоимость обеда: ".concat(elements.select("figcaption").first().text().substring(0, 3)).concat(" рублей\n");
                        stringBuilder.insert(0, price).append("\n");
                    }

                    if (element.attr("class").equals("caption")) {
                        String[] arrStr = element.select("p").first().text().split(",");
                        h1 = element.select("h1").first().text();
                        stringBuilder.append("\n").append(h1).append(": ").append("\n");
                        int count = 1;
                        for (String s : arrStr) {
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

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }

        return stringBuilder.toString();
    }

    private static String getVictoriaLunch(String url) throws IOException {

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

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }
        return stringBuilder.toString();
    }

    private static String getMumuAddresses(String url) throws IOException {

        String addresses = "";

        try {

            getResponseCode(url);
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("address");

            int count = 1;
            for (Element item : elements) {
                if (item.attr("class").equals("invisible-links")) {
                    addresses += String.valueOf(count).concat(". ").concat(item.text().concat("\n"));
                    count++;
                }
            }
            LOGGER.info("ADDRESSES: \n" + addresses);

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }
        return Constants.ADDRESSES_TEXT.concat(addresses);
    }

    private static void getResponseCode(String url) throws IOException {
        LOGGER.info("ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());
    }
}