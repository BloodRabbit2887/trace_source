package qichen.code.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5签名验签工具类
 * @author huangqiang
 *
 */
public class MD5Utils {

    /**
     * MD5签名
     * 
     * @param paramSrc
     *		签名内容
     *   	签名key
     * @return
     * @throws Exception
     */
    public static String sign(String paramSrc) {
        String sign = md5(paramSrc);
        return sign;
    }

    /**
     * MD5验签
     * 
     * @param source
     *    	签名内容
     * @param sign
     *   	签名值
     * @param key
     *   	签名key
     * @return
     */
    public static boolean verify(String source, String sign,String key) {
        String signature = md5(source + "&key=" + key);
        return sign.equals(signature);
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMd5(File file) {
        InputStream fis;
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, numRead);
            }
            fis.close();
            return byteArrayToHexString(messagedigest.digest());
        } catch (NoSuchAlgorithmException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final static String md5(String paramSrc) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = paramSrc.getBytes("UTF-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
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
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String charset = "utf-8";

    public static String MD5Encode(String origin) {
        return MD5Encode(origin,charset);
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };



    public static void main(String[] args) {
        String randomString = MathUtils.getRandomString(32);
        System.out.println("randomString==="+randomString.toLowerCase());

    }
}
