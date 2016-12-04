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
import java.util.Date;

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

            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();
            Elements elements = doc.select("a");

            boolean flag12 = false;
            boolean flag34 = false;
            String dateInfo12 = "";
            String dateInfo34 = "";
            String groupInfo;

            for (Element item : elements) {

                if (!item.attr("href").equals("/menu/group/2/") && !item.attr("href").equals("/menu/group/48/")) {
                    continue;
                }

                if (item.attr("href").equals("/menu/group/2/")) {
                    if (!dateInfo12.isEmpty()) continue;

                    groupInfo = item.getElementsByAttributeValue("href", "/menu/group/2/").first().text();
                    flag12 = BotHelper.checkDayOfMonth(groupInfo.replace(" ", ""), dateCurrent);
                    dateInfo12 = groupInfo;
                } else if (item.attr("href").equals("/menu/group/48/")) {
                    if (!dateInfo34.isEmpty()) continue;

                    groupInfo = item.getElementsByAttributeValue("href", "/menu/group/48/").first().text();
                    flag34 = BotHelper.checkDayOfMonth(groupInfo.replace(" ", ""), dateCurrent);
                    dateInfo34 = groupInfo;
                }
            }

            if (flag12) {
                LOGGER.info("DateInfo: " + dateInfo12);
                LOGGER.info("Get lunches #1 and #2...");
                return getMumuLunch(ConnectionHelper.getUrlLunchOneTwo(command), dateInfo12);
            } else if (flag34) {
                LOGGER.info("DateInfo: " + dateInfo34);
                LOGGER.info("Get lunches #3 and #4...");
                return getMumuLunch(ConnectionHelper.getUrlLunchThreeFour(command), dateInfo34);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }
        LOGGER.info("Today is Holiday!");

        return Constants.ERROR_HOLIDAY_DAY;
    }

    private static String getMumuLunch(String[] url, String dateInfo) throws IOException {

        dateInfo = "☝\uD83C\uDFFB\uD83C\uDF74".concat(dateInfo).concat("\uD83D\uDC4C\uD83C\uDFFB\uD83C\uDF7D");
        String lunchItems = "";
        String price = "";

        try {

            for (String strURL : url) {

                getResponseCode(strURL);

                Document doc = Jsoup.connect(strURL).get();
                Elements elements = doc.select("div");

                for (Element element : elements) {

                    if (element.attr("class").equals("price")) {
                        price = element.select("figcaption").first().text();
                        price = "Стоимость обеда: ".concat(price.substring(0, 3)).concat(" рублей\n");
                    }

                    if (element.attr("class").equals("caption")) {
                        String[] arrStr = element.select("span").first().text().split(",");
                        String item = "";
                        int count = 1;
                        for (String s : arrStr) {
                            if (s.contains("(перец")) {
                                s = s.replaceAll("\\(перец", "");
                            }
                            if (BotHelper.checkString(s.trim())) {
                                continue;
                            }
                            item += String.valueOf(count).concat(". ").concat(s.trim().concat("\n"));
                            count++;
                        }
                        lunchItems += element.select("h1").first().text().concat(": ").concat("\n").concat(item).concat("\n");
                        break;
                    }
                }
            }
            LOGGER.info("LUNCH_MUMU: \n" + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }

        return Constants.TIME_LUNCH_MUMU.concat("\n").concat(price.concat(dateInfo.toUpperCase().concat("\n")).concat("\n").concat(lunchItems));
    }


    private static String getVictoriaLunch(String url) throws IOException {

        String lunchItems = "";
        String date = "";
        String info = "☝\uD83C\uDFFB\uD83C\uDF74".concat("Бизнес ланч на ");

        try {

            getResponseCode(url);

            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("td");

            int count = 1;
            for (Element item : elements) {

                if (item.attr("class").equals("text")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        date += str;
                    }
                }
                if (item.attr("class").equals("mdish") || item.attr("class").equals("mtext")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        lunchItems += String.valueOf(count).concat(". ").concat(str.concat("\n"));
                        count++;
                    }
                }
            }
            LOGGER.info("Date: " + date);
            LOGGER.info("LUNCH_VICTORIA: \n" + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage() + e);
            return Constants.UNEXPECTED_ERROR.concat(e.getMessage());
        }
        return Constants.VICTORIA_TEXT.concat(info.concat(date.concat("\uD83D\uDC4C\uD83C\uDFFB\uD83C\uDF7D").concat("\n").concat(lunchItems)));
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