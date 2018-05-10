package com.gisluq.runtimeviewer.Utils;

import java.util.regex.Pattern;

/**
 * 字符串处理工具
 */
public class StringUtils {

    /**
     * 字符串转浮点型数据
     * @param alpha
     * @return
     */
    private static float getFloat(String alpha)
    {
        float value = 1;
        if(alpha == null || alpha.equals("")) alpha = "1";
        try
        {
            value = Float.parseFloat(alpha);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 字符串转boolean型数据
     * @param visible
     * @return
     */
    private static boolean getBoolean(String visible)
    {
        boolean value = false;
        if(visible == null || visible.equals("")) visible = "false";
        try
        {
            value = Boolean.parseBoolean(visible);
        }
        catch(Exception e)
        {
            value = false;
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 判断是否为数字
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
