package co.uk.gauntface.android.libs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 10/12/12
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class GFDateUtils {

    public static String formatCalendarForSQLite(Calendar calendar) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        return dateFormatter.format(calendar.getTime());
    }

    public static Date getCalendarFromSqlite(String dateString) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        try {
            return dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
