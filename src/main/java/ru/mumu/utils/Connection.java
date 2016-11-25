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

    public static String checkDay(String currentDay, String messageDay, int dayOfMonth) throws IOException {
        LOGGER.info("currentDay:  " + currentDay);
        LOGGER.info("messageDay:  " + messageDay);

        if (currentDay.toLowerCase().equals("saturday") || currentDay.toLowerCase().equals("sunday")) {
            return Constants.ERROR_HOLIDAY_DAY;
        } else {
            if (currentDay.equals(messageDay)) {
                String weekDay = "/".concat(currentDay).toLowerCase();
                LOGGER.info("WeekDay is : " + weekDay);
                return sendRequest(dayOfMonth, weekDay);
            } else {
                return "Days are not equals!";
            }
        }
    }

    public static String sendRequest(String command) throws IOException {
        String url[] = new String[2];
        switch (command) {
            case Constants.VICTORIA:
                url[0] = Constants.VICTORIA_URL;
                return getVictoriaLunch(url[0]);
            case Constants.ADDRESSES:
                url[0] = Constants.ADDRESSES_URL;
                return getAddresses(url[0]);
            default:
                return "Bad command! ".concat(command);
        }
    }


    private static String sendRequestLunchOneTwo(String command) throws IOException {
        String url[] = new String[2];
        switch (command) {
            case Constants.MONDAY:
                url[0] = Constants.MONDAY_1_URL;
                url[1] = Constants.MONDAY_2_URL;
                return getMumuLunch(url);
            case Constants.TUESDAY:
                url[0] = Constants.TUESDAY_1_URL;
                url[1] = Constants.TUESDAY_2_URL;
                return getMumuLunch(url);
            case Constants.WEDNESDAY:
                url[0] = Constants.WEDNESDAY_1_URL;
                url[1] = Constants.WEDNESDAY_2_URL;
                return getMumuLunch(url);
            case Constants.THURSDAY:
                url[0] = Constants.THURSDAY_1_URL;
                url[1] = Constants.THURSDAY_2_URL;
                return getMumuLunch(url);
            case Constants.FRIDAY:
                url[0] = Constants.FRIDAY_1_URL;
                url[1] = Constants.FRIDAY_2_URL;
                return getMumuLunch(url);
            default:
                return "Bad command! ".concat(command);
        }
    }

    private static String sendRequestLunchThreeFour(String command) throws IOException {
        String url[] = new String[2];
        switch (command) {
            case Constants.MONDAY:
                url[0] = Constants.MONDAY_3_URL;
                url[1] = Constants.MONDAY_4_URL;
                return getMumuLunch(url);
            case Constants.TUESDAY:
                url[0] = Constants.TUESDAY_3_URL;
                url[1] = Constants.TUESDAY_4_URL;
                return getMumuLunch(url);
            case Constants.WEDNESDAY:
                url[0] = Constants.WEDNESDAY_3_URL;
                url[1] = Constants.WEDNESDAY_4_URL;
                return getMumuLunch(url);
            case Constants.THURSDAY:
                url[0] = Constants.THURSDAY_3_URL;
                url[1] = Constants.THURSDAY_4_URL;
                return getMumuLunch(url);
            case Constants.FRIDAY:
                url[0] = Constants.FRIDAY_3_URL;
                url[1] = Constants.FRIDAY_4_URL;
                return getMumuLunch(url);
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

            int count = 1;
            for (Element item : elements) {

                if (item.attr("class").equals("text")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        date += str.concat(".");
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

    public static String sendRequest(int currentDayOfMonth, String command) {
        LOGGER.info("Checking Day Of Month...");
        try {
            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();
            Elements elements = doc.select("a");

            boolean flag = false;
            for (Element item : elements) {
                if (item.attr("href").equals("/menu/group/2/")) {
                    String[] arrStr = item.select("span").first().text().split("\n");
                    LOGGER.info("Group info: " + arrStr[0]);
                    flag = checkDayOfMonth(arrStr[0].trim().replace(" ", ""), currentDayOfMonth);
                    break;
                }
            }
            if (flag) {
                return sendRequestLunchOneTwo(command);
            } else {
                return sendRequestLunchThreeFour(command);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return "Что-то пошло не так: ".concat(e.getMessage());
        }
    }

    private static boolean checkDayOfMonth(String str, int currentDayOfMonth) {
        String[] arrString = str.split("");
        String StartDate = arrString[6] + arrString[7];
        String EndDate = arrString[13] + arrString[14];
        LOGGER.info("StartDate: " + StartDate);
        LOGGER.info("EndDate: " + EndDate);

        if (currentDayOfMonth >= Integer.parseInt(StartDate) && currentDayOfMonth <= Integer.parseInt(EndDate)
                || currentDayOfMonth - 2 == Integer.parseInt(EndDate) || currentDayOfMonth - 1 == Integer.parseInt(EndDate)) {
            LOGGER.info("Get lunch #1 and #2....");
            return true;
        } else {
            LOGGER.info("Get lunch #3 and #4....");
            return false;
        }
    }
}