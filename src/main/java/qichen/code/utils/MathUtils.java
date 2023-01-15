package qichen.code.utils;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;


public class MathUtils {

	public static final BigDecimal HUNDRED = new BigDecimal(100);

	private static final double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		// s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**
	 * 获取长度为length的随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		char[] chars = new char[length];

		for (int i = 0; i < length; i++) {
			chars[i] = getRandomChar();
		}

		return new String(chars);
	}

	private static Random random = new Random();
	private static char[] charArr = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static char getRandomChar() {
		return charArr[random.nextInt(charArr.length)];
	}
	
	
	/**
	 * 获取长度为length的数字型随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomInteger(int length) {
		
		Double maxDouble = Math.pow(10, length) - 1;
		
		String maxString = new DecimalFormat("#").format(maxDouble);
		
		Integer maxInt = Integer .parseInt(maxString);
		
		Integer verifyNo = random.nextInt(maxInt);
		
		StringBuilder ftStr = new StringBuilder("");
		for(Integer i = 0; i < length; i++){
			ftStr.append("0");
		}
		
		
		String verifyCode =  new DecimalFormat(ftStr.toString()).format(verifyNo);

		return verifyCode;
	}
	
	public static int nextInt(int bound) {
		return random.nextInt(bound);
	}
	
	public static Integer toInteger(String str) {
		return str == null ? null : new Integer(str);
	}

	public static Long toLong(String str) {
		return str == null ? null : new Long(str);
	}

	public static Double toDouble(String str) {
		return str == null ? null : new Double(str);
	}

	public static BigDecimal toBigDecimal(String str) {
		return str == null ? null : new BigDecimal(str);
	}
	
	public static void main(String[] args) {

	}


}
