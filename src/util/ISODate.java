package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ISODate {
    private String dateIso;

    public ISODate(){
        this(TimeZone.getTimeZone("UTC"));
    }
    public ISODate(TimeZone tz){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // Conversion
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(tz);
        this.dateIso = sdf.format(date);
    }

    @Override
    public String toString(){
        return this.dateIso;
    }

    // Input

// Output
// "2017-02-16T20:22:28.000+01:00"
}
