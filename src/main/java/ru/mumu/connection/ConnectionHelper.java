package ru.mumu.connection;

import ru.mumu.constants.Constants;

import java.io.IOException;

/**
 * Created by alexeyoblomov on 03.12.16.
 */
class ConnectionHelper {


    static String[] getUrlLunchOneTwo(String command) throws IOException {

        String url[] = new String[2];
        switch (command) {
            case Constants.MONDAY:
                url[0] = Constants.MONDAY_1_URL;
                url[1] = Constants.MONDAY_2_URL;
                return url;
            case Constants.TUESDAY:
                url[0] = Constants.TUESDAY_1_URL;
                url[1] = Constants.TUESDAY_2_URL;
                return url;
            case Constants.WEDNESDAY:
                url[0] = Constants.WEDNESDAY_1_URL;
                url[1] = Constants.WEDNESDAY_2_URL;
                return url;
            case Constants.THURSDAY:
                url[0] = Constants.THURSDAY_1_URL;
                url[1] = Constants.THURSDAY_2_URL;
                return url;
            case Constants.FRIDAY:
                url[0] = Constants.FRIDAY_1_URL;
                url[1] = Constants.FRIDAY_2_URL;
                return url;
            default:
                return new String[]{Constants.BAD_COMMAND.concat(command)};
        }
    }

    static String[] getUrlLunchThreeFour(String command) throws IOException {

        String url[] = new String[2];
        switch (command) {
            case Constants.MONDAY:
                url[0] = Constants.MONDAY_3_URL;
                url[1] = Constants.MONDAY_4_URL;
                return url;
            case Constants.TUESDAY:
                url[0] = Constants.TUESDAY_3_URL;
                url[1] = Constants.TUESDAY_4_URL;
                return url;
            case Constants.WEDNESDAY:
                url[0] = Constants.WEDNESDAY_3_URL;
                url[1] = Constants.WEDNESDAY_4_URL;
                return url;
            case Constants.THURSDAY:
                url[0] = Constants.THURSDAY_3_URL;
                url[1] = Constants.THURSDAY_4_URL;
                return url;
            case Constants.FRIDAY:
                url[0] = Constants.FRIDAY_3_URL;
                url[1] = Constants.FRIDAY_4_URL;
                return url;
            default:
                return new String[]{Constants.BAD_COMMAND.concat(command)};
        }
    }
}
