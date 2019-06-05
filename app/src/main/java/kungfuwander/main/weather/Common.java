package kungfuwander.main.weather;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String APP_ID ="810cd0f73be65b8d2211a0022269a1af";
    public static Location current_lcation = null;

    public static String convertToDate(int dt) {
        Date d = new Date(dt *1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm: dd EEE MM YYYY");
        String format = sdf.format(d);
        return format;
    }
    public static String convertToHout(long dt)
    {
        Date d = new Date(dt *1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(d);
        return format;
    }
}
