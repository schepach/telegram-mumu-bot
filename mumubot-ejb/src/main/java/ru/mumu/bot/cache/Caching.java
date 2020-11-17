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

    private static final Logger logger = Logger.getLogger(Caching.class.getSimpleName());
    public static Map<String, String> URL_MAP = new HashMap<>();
    private static int tryCount = 0;

    public static boolean cachingUrls(String url) {

        try {
            String urlWithLunches = null;
            String lunchUrl;
            Utils.connectToURL(Constants.MUMU_MAIN_PAGE_URL);
            Document doc = Jsoup.connect(Constants.MUMU_MAIN_PAGE_URL).get();

            // Get url with all lunches
            Elements elements = doc.select("body div.wrap-main.test div div.content div.menu-page");
            for (Element item : elements) {
                urlWithLunches = item.getElementsByAttributeValue("id", "bx_1847241719_245").select("a").attr("abs:href");
            }

            if (urlWithLunches == null || urlWithLunches.isEmpty()) {
                logger.log(Level.SEVERE, "urlWithLunches is null or is empty");
                logger.log(Level.SEVERE, "Break caching pages ...");
                return false;
            }

            // Get urls with lunches
            doc = Jsoup.connect(urlWithLunches).get();
            elements = doc.select("div.menu-container-text > div.food-item-title.lunch-item-title");
            for (Element item : elements) {
                lunchUrl = item.select("a").attr("abs:href");
                logger.log(Level.INFO, "lunchUrl = " + lunchUrl);
                URL_MAP.put(lunchUrl, Jsoup.connect(url).get().html());
            }

            if (URL_MAP == null || URL_MAP.isEmpty()) {
                logger.log(Level.SEVERE, "Произошла непредвиденная ошибка: URL_MAP is null or is empty");
                return false;
            }

        } catch (Exception ex) {
            tryCount++;
            if (tryCount == 4) {
                logger.log(Level.SEVERE, "After {0} trying return false, clear map and don't caching...", tryCount);
                URL_MAP.clear();
                return false;
            }
            logger.log(Level.SEVERE, "Caching exception: ", ex);
            logger.log(Level.SEVERE, "Caching url - {0}, try - {1}", new Object[]{url, tryCount});
            cachingUrls(url);
        }

        return true;
    }
}
