package me.pdthx.Helpers;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {
    /**
     * <p>
     * Parses a Microsoft .NET style JSON timestamp and returns a Java Date irrespective of time zones (but can parse them)
     * </p>
     *
     * <a href="http://weblogs.asp.net/bleroy/archive/2008/01/18/dates-and-json.aspx">Microsoft .NET JSON Format Reference</a>
     * <a href="http://benjii.me/2010/04/deserializing-json-in-android-using-gson/">GSON Code Reference</a>
     *
     * @param msJsonDateTime The String representation of a Microsoft style timestamp
     * @return Java Date that represents the timestamp
     */
    public static Date parseMsTimestampToDate(final String msJsonDateTime) {
        if(msJsonDateTime == null) return null;
        String JSONDateToMilliseconds = "\\\\/(Date\\\\((-*.*?)([\\\\+\\\\-].*)?\\\\))\\\\/";
        Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
        Matcher matcher = pattern.matcher(msJsonDateTime);
        String ts = matcher.replaceAll("$2");
        Date retValue = new Date(new Long(ts));
        return retValue;
    }
}