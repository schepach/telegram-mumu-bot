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

/**
 * Created by alexey on 08.08.16.
 */

public class Connection {
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());

    public static String sendRequest(String command) throws IOException {

        LOGGER.info("CommandMumu: " + command);
        String lunchItems = "";
        String url[] = new String[2];
        if (command.equals(Constants.MONDAY)) {
            url[0] = "http://cafemumu.ru/menu/item/133/";
            url[1] = "http://cafemumu.ru/menu/item/134/";
        }

        if (command.equals(Constants.TUESDAY)) {
            url[0] = "http://cafemumu.ru/menu/item/135/";
            url[1] = "http://cafemumu.ru/menu/item/136/";
        }

        if (command.equals(Constants.WEDNESDAY)) {
            url[0] = "http://cafemumu.ru/menu/item/137/";
            url[1] = "http://cafemumu.ru/menu/item/138/";
        }

        if (command.equals(Constants.THURSDAY)) {
            url[0] = "http://cafemumu.ru/menu/item/139/";
            url[1] = "http://cafemumu.ru/menu/item/140/";
        }
        if (command.equals(Constants.FRIDAY)) {
            url[0] = "http://cafemumu.ru/menu/item/141/";
            url[1] = "http://cafemumu.ru/menu/item/142/";
        }

        String price = "";
        String timeLunch = "Время обедов в МУ-МУ: 12:00-16:00";
        try {
            for (String strURL : url) {

                LOGGER.info("ConnectTo: " + strURL);
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(strURL);
                HttpResponse response = client.execute(request);
                LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());

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
                        for (String s : arrStr) {
                            item += s.concat("\n");
                        }
                        lunchItems += element.select("h1").first().text().concat(": ").concat("\n").concat(item).concat("\n");
                        break;
                    }
                }
            }
            LOGGER.info("LUNCH = " + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return timeLunch.concat("\n").concat(price.concat("\n").concat(lunchItems));
    }

    public static String sendRequestVictoria(String commandVictoria) throws IOException {
        LOGGER.info("CommandVictoria: " + commandVictoria);
        String lunchItems = "";
        String url = "";

        if (commandVictoria.equals(Constants.VIСTORIA)) {
            url = "http://restaurantgrandvictoria.ru/lunch";
        }
        String info = "";
        try {
            LOGGER.info("ConnectTo: " + url);
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());

            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("td");

            for (Element item : elements) {
                if (item.attr("class").equals("text")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        info += str.concat(".");
                    }
                }
                if (item.attr("class").equals("mdish")) {
                    String[] arrStr = item.text().split("\n");

                    for (String str : arrStr) {
                        lunchItems += str.concat("\n");

                    }
                }
            }
            LOGGER.info("info: " + info);
            LOGGER.info("LUNCH_VICTORIA = " + lunchItems);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return info.concat("\n").concat(lunchItems);
    }
}