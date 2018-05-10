package gisluq.lib.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/3/19.
 */
public class DateUtils {
    /**
     * 获取当前系统时间--标准格式yyyyMMddHHmmss
     * @return 当前时间点
     */
    public static String getTimeNow() {
        //获取当前系统时间并用系统时间作为文件名保存照片文件
        SimpleDateFormat formatter  =   new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate= new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取datez之后的时间--标准格式yyyyMMddHHmmss
     * @return 当前时间点
     */
    public static String getTimeAfter(int date) {
        //获取当前系统时间并用系统时间作为文件名保存照片文件
        SimpleDateFormat formatter  =   new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate= new Date(System.currentTimeMillis());//获取当前时间
        long Time=(curDate.getTime()/1000)+60*60*24*date;
        curDate.setTime(Time*1000);
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 字符串转日期
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getDateFromString(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date= formatter.parse(str);//获取当前时间
        return date;
    }

    /**
     * 字符串转日期
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getDateFromString2(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date= formatter.parse(str);//获取当前时间
        return date;
    }

    /**
     * 获取日期信息
     * @param date
     * @return
     */
    public static String getDataString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = formatter.format(date);
        return str;
    }

    /**
     * 更改当前系统时间--自定自定义格式yyyy-MM-dd HH$mm$ss
     * @return 当前时间点
     */
    public static String updateTimeFormat(String time) {
        Date curDate;
        try {
            curDate = getDateFromString(time);
            //获取当前系统时间并用系统时间作为文件名保存照片文件
            SimpleDateFormat formatter  =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = formatter.format(curDate);
            return str;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

//    /**
//     * 更改当前系统时间--自定自定义格式yyyy-MM-dd HH$mm$ss
//     * @return 当前时间点
//     */
//    public static String updateTimeFormat2(String time) {
//        Date curDate;
//        try {
//            curDate = getDateFromString2(time);
//            //获取当前系统时间并用系统时间作为文件名保存照片文件
//            SimpleDateFormat formatter  =   new SimpleDateFormat("yyyyMMddHHmmss");
//            String str = formatter.format(curDate);
//            return str;
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//    }
}
