package gisluq.lib.Util;

import java.util.regex.Pattern;

/**
 * 1.二进制转换为十六进制
 * 2.E-mail 检测
 * 3.URL检测
 * 4.text是否包含空字符串
 * 5.添加对象数组
 * @author dds
 *
 */
public class StringUtil {

	public static final String URL_REG_EXPRESSION = "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*";
	public static final String EMAIL_REG_EXPRESSION = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";

	/**
	 * 是否是URL
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isUrl(String s) {
		if (s == null) {
			return false;
		}
		return Pattern.matches(URL_REG_EXPRESSION, s);
	}

	/**
	 * 是否是E-MAIL
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmail(String s) {
		if (s == null) {
			return true;
		}
		return Pattern.matches(EMAIL_REG_EXPRESSION, s);
	}

	/**
	 * 是否含有空字符串
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isBlank(String s) {
		if (s == null) {
			return true;
		}
		return Pattern.matches("\\s*", s);
	}

	/**
	 * 添加对象数组
	 * 
	 * @param spliter
	 *            间隔符
	 * @param arr
	 *            数组
	 * @return
	 */
	public static String join(String spliter, Object[] arr) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		if (spliter == null) {
			spliter = "";
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i == arr.length - 1) {
				break;
			}
			if (arr[i] == null) {
				continue;
			}
			builder.append(arr[i].toString());
			builder.append(spliter);
		}
		return builder.toString();
	}



	public  static String b2h(String binary) {

		String hexStr[] = { "0", "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "A", "B", "C", "D", "E", "F" };
		int length = binary.length();
		int temp = length % 4;

		if (temp != 0) {
			for (int i = 0; i < 4 - temp; i++) {
				binary = "0" + binary;
			}
		}

		length = binary.length();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length / 4; i++) {
			int num = 0;

			for (int j = i * 4; j < i * 4 + 4; j++) {
				num <<= 1;// 左移
				num |= (binary.charAt(j) - '0');
			}
			sb.append(hexStr[num]);

		}
		return sb.toString();
	}

}
