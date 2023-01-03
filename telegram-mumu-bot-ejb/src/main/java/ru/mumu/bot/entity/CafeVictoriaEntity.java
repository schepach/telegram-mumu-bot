package ru.mumu.bot.entity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.utils.Utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CafeVictoriaEntity extends AbstractCafe {

    private static final Logger logger = Logger.getLogger(CafeVictoriaEntity.class.getSimpleName());
    private static final Pattern PATTERN_DATE = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\d\\d");

    @Override
    public String getMenu() {
        String date = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {

            Utils.connectToURL(Constants.VICTORIA_URL);
            Document doc = Jsoup.connect(Constants.VICTORIA_URL).get();

            stringBuilder.append(Constants.VICTORIA_TEXT).append(Constants.TIME_LUNCH + "\n").append("\uD83E\uDD57\uD83C\uDF72\uD83C\uDF5D\uD83E\uDD64\n").append("Бизнес ланч на ");

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

            if (date == null) {
                return Constants.UNEXPECTED_ERROR;
            }

            stringBuilder.append(date.concat("\n"));

            //Get items of lunch
            Elements elements = doc.select("div").attr("class", "menu-section").select("tr");

            int count = 1;
            String elemMenu;
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

            logger.log(Level.INFO, "Date - {0} ", date);
            logger.log(Level.INFO, "Final menu in Victoria: {0}\n", stringBuilder.toString());

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception: ", ex);
            return Constants.UNEXPECTED_ERROR;
        }
        return stringBuilder.toString();
    }

    @Override
    public String getAddresses() {
        return null;
    }

}
