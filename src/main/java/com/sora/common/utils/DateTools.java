package com.sora.common.utils;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author LangWu
 * @date 2020-06-30
 */
public class DateTools implements Converter<String, Date> {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date convert(String s) {
        if ("".equals(s) || s == null) {
            return null;
        }
        try {
            return simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public static Date subMonth(){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MONTH,1);
        return rightNow.getTime();
    }

    public static Date stringToDate(String str) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            if (str.indexOf("/") != -1) {
                date = sdf1.parse(str);
            } else if (str.indexOf("-") != -1) {
                date = sdf2.parse(str);
            } else if (str.indexOf("月") != -1) {
                date = sdf3.parse(str);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Long getDays(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
    }

}
