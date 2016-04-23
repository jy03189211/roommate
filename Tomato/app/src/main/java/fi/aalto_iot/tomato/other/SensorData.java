package fi.aalto_iot.tomato.other;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SensorData implements Comparable<SensorData> {
    private int data;
    private Calendar time;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public void setTime(String time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        try {
            //Ugly hack to get simpleDateFormat working by removing last three digits from micros
            Date date = format.parse(time.substring(0, time.length() - 3));
            calendar.setTime(date);
            this.time = calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(SensorData another) {
        return this.data - another.getData();
    }
}
