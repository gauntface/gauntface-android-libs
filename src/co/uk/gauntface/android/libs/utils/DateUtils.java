package co.uk.gauntface.android.libs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 10/12/12
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {

    public static String formatCalendarForSQLite(Calendar calendar) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        return dateFormatter.format(calendar.getTime());
    }
}
