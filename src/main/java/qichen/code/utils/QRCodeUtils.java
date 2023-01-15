package qichen.code.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import java.util.Random;

/**
 * 二维码工具类
 * 
 */

@Component
public class QRCodeUtils {

	private static Logger logger = LoggerFactory.getLogger(QRCodeUtils.class);
	private static final String CHARSET = "utf-8";
	private static final String FORMAT = "PNG";
	// 二维码尺寸
	private static final int QRCODE_SIZE = 300;
	// LOGO宽度
	private static final int LOGO_WIDTH = 60;
	// LOGO高度
	private static final int LOGO_HEIGHT = 60;

	private static final String WxaCodeunLimit_url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";

	private static BufferedImage createImage(String content, String logoPath, boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (logoPath == null || "".equals(logoPath)) {
			return image;
		}
		// 插入图片
		insertImage(image, logoPath, needCompress);
		return image;
	}

	/**
	 * 插入LOGO
	 * 
	 * @param source       二维码图片
	 * @param logoPath     LOGO图片地址
	 * @param needCompress 是否压缩
	 * @throws Exception
	 */
	public static void insertImage(BufferedImage source, String logoPath, boolean needCompress) throws Exception {
		File file = new File(logoPath);
		if (!file.exists()) {
			throw new Exception("logo file not found.");
		}
		Image src = ImageIO.read(file);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > LOGO_WIDTH) {
				width = LOGO_WIDTH;
			}
			if (height > LOGO_HEIGHT) {
				height = LOGO_HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	/**
	 * 生成二维码(内嵌LOGO) 调用者指定二维码文件名
	 * 
	 * @param content      内容
	 * @param logoPath     LOGO地址
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static String getEncodeUrl(String content, String logoPath, boolean needCompress) throws Exception {
		BufferedImage image = QRCodeUtils.createImage(content, logoPath, needCompress);

		// 生成本地保存的路径及文件名
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, FORMAT, os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		String md5Str = HashUtils.getMd5(is);
		String fileName = md5Str + "." + FORMAT.toLowerCase();
		String virtualUrl = ContextUtils.getParameter(null, "qr.virtual.address", true).getParamValue();
		String physicalPath = ContextUtils.getParameter(null, "qr.physical.address", true).getParamValue();
		String filePath = fileName;

		// 写入文件
		ImageIO.write(image, FORMAT, new File(physicalPath + fileName));

		// 返回网络地址
		return virtualUrl + filePath;
	}

	/**
	 * 生成二维码(内嵌LOGO) 调用者指定二维码文件名
	 * 
	 * @param content      内容
	 * @param logoPath     LOGO地址
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static void getEncodeOS(String content, String logoPath, OutputStream os, boolean needCompress)
			throws Exception {

		BufferedImage image = createImage(content, logoPath, needCompress);

		// 生成本地保存的路径及文件名
		ImageIO.write(image, FORMAT, os);
	}

	/**
	 * 生成二维码(内嵌LOGO) 二维码文件名随机，文件名可能会有重复
	 * 
	 * @param content      内容
	 * @param logoPath     LOGO地址
	 * @param destPath     存放目录
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static String encode(String content, String logoPath, String destPath, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		String fileName = new Random().nextInt(99999999) + "." + FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
		return fileName;

	}

	/**
	 * 生成二维码(内嵌LOGO) 调用者指定二维码文件名
	 * 
	 * @param content      内容
	 * @param logoPath     LOGO地址
	 * @param destPath     存放目录
	 * @param fileName     二维码文件名
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static String encode(String content, String logoPath, String destPath, String fileName, boolean needCompress)
			throws Exception {
		BufferedImage image = QRCodeUtils.createImage(content, logoPath, needCompress);
		mkdirs(destPath);
		fileName = fileName.substring(0, fileName.indexOf(".") > 0 ? fileName.indexOf(".") : fileName.length()) + "."
				+ FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
		return fileName;
	}

	/**
	 * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir． (mkdir如果父目录不存在则会抛出异常)
	 * 
	 * @param destPath 存放目录
	 */
	public static void mkdirs(String destPath) {
		File file = new File(destPath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content  内容
	 * @param logoPath LOGO地址
	 * @param destPath 存储地址
	 * @throws Exception
	 */
	public static String encode(String content, String logoPath, String destPath) throws Exception {
		return encode(content, logoPath, destPath, false);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content      内容
	 * @param destPath     存储地址
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static String encode(String content, String destPath, boolean needCompress) throws Exception {
		return encode(content, null, destPath, needCompress);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content  内容
	 * @param destPath 存储地址
	 * @throws Exception
	 */
	public static String encode(String content, String destPath) throws Exception {
		return encode(content, null, destPath, false);
	}

	/**
	 * 获取小程序码
	 *
	 * @param scene        最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用
	 *                     urlencode 处理，请使用其他编码方式
	 * @return 返回路径
	 */
	public static BufferedImage getQrCode(String scene, String path,String accessToken) throws IOException {
		RestTemplate rest = new RestTemplate();
		InputStream inputStream = null;
		JSONObject param = new JSONObject();
		param.put("page", path);
		param.put("width", QRCODE_SIZE);
		param.put("scene", scene);
		param.put("auto_color", Boolean.TRUE);
		param.put("is_hyaline", false);
		// logger.info("调用生成微信URL接口传参:" + param);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		HttpEntity requestEntity = new HttpEntity(JSONObject.toJSONString(param), headers);
		String url = WxaCodeunLimit_url +accessToken;
		ResponseEntity<byte[]> entity = rest.exchange(url, HttpMethod.POST, requestEntity, byte[].class, new Object[0]);
		byte[] result = entity.getBody();
		inputStream = new ByteArrayInputStream(result);
		return ImageIO.read(inputStream);
	}

	/**
	 * 生成二维码(内嵌LOGO) 调用者指定二维码文件名
	 *
	 * @param path         存放文件夹(特定目录下的文件夹)
	 * @throws Exception
	 */
	public static String getWxEncodeUrl(String scene, String path,String localUrl,String accessToken) throws Exception {
		BufferedImage image = QRCodeUtils.getQrCode(scene, path,accessToken);
		// 生成本地保存的路径及文件名
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, FORMAT, os);

		InputStream is = new ByteArrayInputStream(os.toByteArray());
		String md5Str = HashUtils.getMd5(is);
		String fileName = md5Str + "." + FORMAT.toLowerCase();
		// 写入文件
		ImageIO.write(image, FORMAT, new File(localUrl + fileName));
		// 返回网络地址
		return fileName;
	}




	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content      内容
	 * @param logoPath     LOGO地址
	 * @param output       输出流
	 * @param needCompress 是否压缩LOGO
	 * @throws Exception
	 */
	public static void encode(String content, String logoPath, OutputStream output, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, logoPath, needCompress);
		ImageIO.write(image, FORMAT, output);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content 内容
	 * @param output  输出流
	 * @throws Exception
	 */
	public static void encode(String content, OutputStream output) throws Exception {
		encode(content, null, output, false);
	}

	/**
	 * 解析二维码
	 * 
	 * @param file 二维码图片
	 * @return
	 * @throws Exception
	 */
	public static String decode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		result = new MultiFormatReader().decode(bitmap, hints);
		return result.getText();
	}

	/**
	 * 解析二维码
	 * 
	 * @param path 二维码图片地址
	 * @return
	 * @throws Exception
	 */
	public static String decode(String path) throws Exception {
		return decode(new File(path));
	}



}