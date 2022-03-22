package com.rychly.bp_backend.comparators;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;

/**
 * This comparator compares two indexed logs based on their timestamp (lower first).
 * @author www.codejava.net
 *
 */

public class logComparator implements Comparator<Log> {

    @Override
    public int compare(Log log1, Log log2) {

        //todo
        //I am loosing milisecond part here - check seconds ->
        LocalDateTime log1_timestamp = LocalDateTime.of(Integer.parseInt(log1.getYear2TS()), Integer.parseInt(log1.getMonth2TS()), Integer.parseInt(log1.getDay2TS()), Integer.parseInt(log1.getHour2TS()), Integer.parseInt(log1.getMinute2TS()),(int)Double.parseDouble(log1.getSecond2TS()));
        LocalDateTime log2_timestamp = LocalDateTime.of(Integer.parseInt(log2.getYear2TS()), Integer.parseInt(log2.getMonth2TS()), Integer.parseInt(log2.getDay2TS()), Integer.parseInt(log2.getHour2TS()), Integer.parseInt(log2.getMinute2TS()),(int)Double.parseDouble(log2.getSecond2TS()));

        return log1_timestamp.compareTo(log2_timestamp);
    }
}





