package gisluq.lib.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import static com.esri.arcgisruntime.internal.jni.av.dm;
import static com.esri.arcgisruntime.internal.jni.av.el;

/**
 * 系统硬件参数获取类
 * Created by lq on 2015/3/13.
 */
public class SysUtils {

    /**
     * 判断网络（3G、GPRS）是否联通
     * @param context
     */
    public static boolean isConnected(Context context)
    {
        final ConnectivityManager connMgr =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected())
            return true;
        else
            return false;
    }

    /**
     * 获取当前系统时间--标准格式yyyyMMddHHmmss
     * @return 当前时间点
     */
    public static  String getTimeNow() {
        //获取当前系统时间并用系统时间作为文件名保存照片文件
        SimpleDateFormat formatter  =   new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate= new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {

        Configuration configuration = context.getResources().getConfiguration();
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


//    /**
//     * 更改当前系统时间--自定自定义格式yyyy-MM-dd HH$mm$ss
//     * @return 当前时间点
//     */
//    public static  String updateTimeFormat(String time) {
//        Date curDate;
//        try {
//            curDate = getDateFromString(time);
//            //获取当前系统时间并用系统时间作为文件名保存照片文件
//            SimpleDateFormat  formatter  =   new  SimpleDateFormat("yyyy-MM-dd HH$mm$ss");
//            String str = formatter.format(curDate);
//            return str;
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 字符串转日期
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getDateFromString(String str) throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= formatter.parse(str);//获取当前时间
        return date;
    }

    /**
     * 日期转字符串
     * @param curDate
     * @return
     */
    public static String getDateString(Date curDate){
        SimpleDateFormat  formatter  =   new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取GUID
     * @return GUID
     */
    public static String getGUID(){
        String str = java.util.UUID.randomUUID().toString();//GUID;
        return str;
    }


    /**
     * 这是使用adb shell命令来获取mac地址的方式
     * @return
     */
    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        if (macSerial==null){
            macSerial = getLocalMacAddressFromIp();
        }

        return macSerial;
    }


    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toLowerCase();
        } catch (Exception e) {

        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }



    /**
     * 获取SD卡路径信息
     * @return sd卡路径
     */
    public static String getSDPath(){
        return SDCardUtils.getSDCardPath();
    }

//    /**
//     * 获取扩展SD卡路径信息
//     * @return 扩展sd卡路径，没有返回空
//     */
//    public static String getExtSDPath() {
//        return SDCardUtils.getExternalSdCardPath();
//    }


    /**
     * 获取图片资源信息
     * @param context
     * @param name
     * @return
     */
    public static int getResourceID(Context context,String name) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

    /**
     * 文字转换BitMap
     * @param text
     * @return
     */
    public static Drawable createMapBitMap(String text) {

        //默认字体大小个高度
        int size = 30;
        int height = 30;

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);//位置

        float textLength = paint.measureText(text);

        int width = (int) textLength;

        Bitmap newb = Bitmap.createBitmap(width*2, height*2, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.drawColor(Color.parseColor("#00000000"));

        cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));

        cv.drawText(text, width, size, paint);
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储

        return new BitmapDrawable(newb);

    }


}
