package gisluq.lib.Util;

import android.util.Log;

/**
 * 
 * 本工具对 Log的打印进行封装。作者在开发中自定义以下格式，方便在程序中查看日志。默认为debug类型,即发布后不打印<br>
 * 1.调试log打印： activity - 用途 - 数据名称 - 数据<br>
 * 2.提示Log打印：比如：登陆-->进入首页-->获取数据--><br>
 * 3.打印出一个异常<br>
 */
public class LogUtils {
	private static int LOGO_MODE = Log.DEBUG;

	/**
	 * 打印数据， 用来观察数据是否正常。
	 * @param object 传递 this。
	 * @param what 数据用途
	 * @param dataName 数据名称
	 * @param data 数据
	 */
	public static void LOG(Object object, String what, String dataName, String data) {
		String msg =  "用途："+what+";数据名称:"+dataName+";数据:"+data;
		switch (LOGO_MODE) {
		case Log.DEBUG:
			Log.d(object.getClass().getSimpleName(),msg);
			break;
		case Log.INFO:
			Log.i(object.getClass().getSimpleName(),msg);
			break;
		case Log.WARN:
			Log.w(object.getClass().getSimpleName(),msg);
			break;
		case Log.ERROR:
			Log.e(object.getClass().getSimpleName(),msg);
			break;
		case Log.VERBOSE:
			Log.v(object.getClass().getSimpleName(),msg);
			break;

		}
	}

	/**
	 * 
	 * @param object 传递this即可
	 * @param msg 需要打印的提示信息
	 */
	public static void LOG_L(Object object, String msg){
		Log.d(object.getClass().getSimpleName(),msg);
	}
	
	/**
	 * 在异常中打印LOG
	 * @param object 传递this即可
	 * @param where 该异常发生地点
	 * @param e 异常
	 */
	public static void LOG_E(Object object, String where, Exception e){
		Log.wtf(object.getClass().getSimpleName(), where, e);
	}
}
