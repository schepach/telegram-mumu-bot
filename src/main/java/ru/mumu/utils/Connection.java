package ru.mumu.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexey on 08.08.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());

    public static String checkDay(String currentDay, String messageDay) throws IOException {
        LOGGER.info("currentDay:  " + currentDay);
        LOGGER.info("messageDay:  " + messageDay);

        if (currentDay.toLowerCase().equals("saturday") || currentDay.toLowerCase().equals("sunday")) {
            return Constants.ERROR_HOLIDAY_DAY;
        } else {
            if (currentDay.equals(messageDay)) {
                String weekDay = "/".concat(currentDay).toLowerCase();
                LOGGER.info("WeekDay is : " + weekDay);
                return sendRequest(weekDay);
            } else {
                return "Days are not equals!";
            }
        }
    }

    public static String sendRequest(String command) throws IOException {

        String url[] = new String[2];
        switch (command) {
            case Constants.MONDAY:
                url[0] = "http://cafemumu.ru/menu/item/133/";
                url[1] = "http://cafemumu.ru/menu/item/134/";
                return getMumuLunch(url);
            case Constants.TUESDAY:
                url[0] = "http://cafemumu.ru/menu/item/135/";
                url[1] = "http://cafemumu.ru/menu/item/136/";
                return getMumuLunch(url);
            case Constants.WEDNESDAY:
                url[0] = "http://cafemumu.ru/menu/item/137/";
                url[1] = "http://cafemumu.ru/menu/item/138/";
                return getMumuLunch(url);
            case Constants.THURSDAY:
                url[0] = "http://cafemumu.ru/menu/item/139/";
                url[1] = "http://cafemumu.ru/menu/item/140/";
                return getMumuLunch(url);
            case Constants.FRIDAY:
                url[0] = "http://cafemumu.ru/menu/item/141/";
                url[1] = "http://cafemumu.ru/menu/item/142/";
                return getMumuLunch(url);
            case Constants.VICTORIA:
                url[0] = "http://restaurantgrandvictoria.ru/lunch";
                return getVictoriaLunch(url[0]);
            case Constants.ADDRESSES:
                url[0] = "http://zoon.ru/msk/restaurants/network/mu-mu/";
                return getAddresses(url[0]);
            default:
                return "Bad command! ".concat(command);
        }
    }

    private static String getMumuLunch(String[] url) throws IOException {
        String lunchItems = "";
        String price = "";
        String timeLunch = "Время обедов в МУ-МУ: 12:00-16:00";
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
                        int count = 0;
                        for (String s : arrStr) {
                            if (s.contains("(перец")) {
                                s = s.replaceAll("\\(перец", "");
                            }
                            if (checkString(s.trim())) {
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
            LOGGER.info("\n" + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return "Что-то пошло не так: ".concat(e.getMessage());
        }

        return timeLunch.concat("\n").concat(price.concat("\n").concat(lunchItems));
    }


    private static String getVictoriaLunch(String url) throws IOException {
        String lunchItems = "";
        String date = "";

        try {
            getResponseCode(url);

            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("td");

            for (Element item : elements) {
                if (item.attr("class").equals("text")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        date += str.concat(".");
                    }
                }
                if (item.attr("class").equals("mdish")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        lunchItems += str.concat("\n");
                    }
                }
            }
            LOGGER.info("date: " + date);
            LOGGER.info("LUNCH_VICTORIA: \n" + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return "Что-то пошло не так: ".concat(e.getMessage());
        }
        return date.concat("\n").concat(lunchItems);
    }

    private static String getAddresses(String url) throws IOException {
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
            LOGGER.error(e.getMessage());
            return "Что-то пошло не так: ".concat(e.getMessage());
        }
        return "Адреса кафе му-му: \n".concat(addresses);
    }

    private static void getResponseCode(String url) throws IOException {
        LOGGER.info("ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());
    }

    private static boolean checkString(String str) {
        Pattern p = Pattern.compile("горошек|кукуруза\\)");
        Matcher m = p.matcher(str);
        return m.matches();
    }
}