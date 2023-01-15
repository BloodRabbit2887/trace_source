package qichen.code.utils;

import qichen.code.exception.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashUtils {

	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	public static String getMd5(byte[] data) {
		// 获得MD5摘要算法的 MessageDigest 对象
		MessageDigest mdInst;
		try {
			mdInst = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new FrameworkException(e);
		}

		// 使用指定的字节更新摘要
		mdInst.update(data);

		// 获得密文
		byte[] md = mdInst.digest();

		// 把密文转换成十六进制的字符串形式
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static String getMd5(InputStream is) {
		byte[] data = new byte[4096];
		int n;

		// 获得MD5摘要算法的 MessageDigest 对象
		MessageDigest mdInst;
		try {
			mdInst = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new FrameworkException(e);
		}

		// 使用指定的字节更新摘要
		try {
			while ((n = is.read(data)) != -1) {
				mdInst.update(data, 0, n);
			}
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		// 获得密文
		byte[] md = mdInst.digest();

		// 把密文转换成十六进制的字符串形式
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static String getSha1(byte[] data) {
		// 获得MD5摘要算法的 MessageDigest 对象
		MessageDigest mdInst;
		try {
			mdInst = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new FrameworkException(e);
		}

		// 使用指定的字节更新摘要
		mdInst.update(data);

		// 获得密文
		byte[] md = mdInst.digest();

		// 把密文转换成十六进制的字符串形式
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static void main(String[] args) {
		String saltValue = MathUtils.getRandomString(20);
		String pass = HashUtils.getMd5(("123456"+saltValue).getBytes());
		System.out.println("saltValue==="+saltValue);
		System.out.println("pass==="+pass);
	}
	
}
