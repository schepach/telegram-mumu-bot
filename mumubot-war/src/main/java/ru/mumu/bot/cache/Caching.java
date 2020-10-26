package ru.mumu.bot.cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Caching {

    private static final Logger LOGGER = Logger.getLogger(Caching.class.getSimpleName());
    public static Map<String, String> URL_MAP = new HashMap<>();

    public static boolean cachingUrls(String url) {

        try {
            Utils.connectToURL(Constants.MUMU_MAIN_PAGE_URL);
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
                    return false;
                }
                url = elem.attr("abs:href");
                LOGGER.log(Level.SEVERE, "UrlWithLunches = " + url);
                break;
            }

            // Get url list for lunches
            Utils.connectToURL(url);
            doc = Jsoup.connect(url).get();
            elements = doc.select("div");

            for (Element item : elements) {
                if (!item.attr("class").trim().equals("food-item spec-block product-labels-wrapper")) {
                    continue;
                }
                url = item.select("a").attr("abs:href");
                LOGGER.log(Level.INFO, "UrlOfLunch = " + url);
                URL_MAP.put(url, Jsoup.connect(url).get().html());
            }

            if (URL_MAP == null || URL_MAP.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Произошла непредвиденная ошибка: URL_MAP is null or is empty");
                return false;
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
            LOGGER.log(Level.SEVERE, "Caching url " + url + " again ...");
            cachingUrls(url);
        }
        return true;
    }

}
