package org.basketball;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-20
 * Time: 下午2:02
 * Let's RocknRoll
 */
public class Time {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateParser.setTimeZone(TimeZone.getTimeZone("Z"));
        Date date = dateParser.parse("2013-01-06T16:00:00Z");
        System.out.println(date);
    }
}
