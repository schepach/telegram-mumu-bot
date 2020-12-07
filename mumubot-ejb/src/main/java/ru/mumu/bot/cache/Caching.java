package ru.mumu.bot.cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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

    public static boolean cachingUrls() {

        try {
            String lunchUrl;
            Utils.connectToURL(Constants.LUNCHES_URL);
            // Get urls with lunches
            Document doc = Jsoup.connect(Constants.LUNCHES_URL).get();
            Elements elements = doc.select("div.menu-container-text > div.food-item-title.lunch-item-title");
            for (Element item : elements) {
                lunchUrl = item.select("a").attr("abs:href");
                logger.log(Level.INFO, "lunchUrl - {0}", lunchUrl);
                // Connect to url and remove comments for perfect parsing
                doc = Jsoup.connect(lunchUrl).get();
                removeComments(doc);
                URL_MAP.put(lunchUrl, doc.html());
            }

            if (URL_MAP == null || URL_MAP.isEmpty()) {
                logger.log(Level.SEVERE, "Произошла непредвиденная ошибка: URL_MAP is null or is empty");
                return false;
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Caching exception: ", ex);
            URL_MAP.clear();
            tryCount++;
            if (tryCount == 4) {
                logger.log(Level.SEVERE, "After {0} trying don't caching urls...", tryCount);
                return false;
            }
            logger.log(Level.SEVERE, "Caching urls again, trying - {0}", new Object[]{tryCount});
            cachingUrls();
        }

        return true;
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }
}
