package qichen.code.utils;

import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import qichen.code.exception.BusinessException;
import qichen.code.exception.FrameworkException;
import qichen.code.exception.ResException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class FileUtils {

	public static final String IMG_TYPE_WXMEDIAID = "2"; // 文件流
	public static final String IMG_TYPE_FILE = "1"; // 微信图片MEDIAID

	private static String localUrl;
	private static String netUrl;


	@SuppressWarnings("static-access")
	@Value("${local.url}")
	private void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	@SuppressWarnings("static-access")
	@Value("${net.url}")
	private void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}

	@Autowired
	public DocumentConverter converter;


	public static String saveMultipartFile(CommonsMultipartFile multipartFile, Integer fileType) {

		String departUrl = null;
		if (null == fileType) {
			departUrl = "";
		} else if (fileType.equals(1)) { // 商品图片
			departUrl = "product/";
		} else if (fileType.equals(2)) { // 店铺收款二维码
			departUrl = "storeCode/";
		} else if (fileType.equals(3)) { // 用户推荐二维码
			departUrl = "referCode/";
		} else if (fileType.equals(4)) { // 用户头像
			departUrl = "avatar/";
		} else if (fileType.equals(5)) { // 店铺图片
			departUrl = "store/";
		} else if (fileType.equals(6)) { // 实名认证图片
			departUrl = "front/";
		} else if (fileType.equals(7)) { // 实名认证图片
			departUrl = "back/";
		} else if (fileType.equals(8)) { // 实名认证图片
			departUrl = "banner/";
		}

		String fullUrl = localUrl.concat(departUrl);
		return saveMultipartFile2Path(multipartFile, fullUrl, departUrl);
	}

	public static String saveMultipartFile2Path(CommonsMultipartFile multipartFile, String rootPath, String departUrl) {
		// rootPath : E\:/tomcat_static/qiman/upload/storeCode/
		System.out.println("rootPath=========" + rootPath);
		InputStream is;
		try {
			is = multipartFile.getInputStream();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		String originalFilename = multipartFile.getOriginalFilename();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);

		String md5Str = HashUtils.getMd5(is);

		String realPath = rootPath;

		String filePath = "/" + departUrl + md5Str + extension;

		System.out.println("realPath=========" + realPath + "/" + md5Str + extension);
		File dest = new File(realPath + "/" + md5Str + extension);
		try {
			multipartFile.transferTo(dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}

		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		System.out.println("netPath========" + netUrl + filePath);
		return netUrl + filePath;
	}

	public static String saveMultipartFile2ContextPath(CommonsMultipartFile multipartFile, String rootPath) {
		InputStream is;
		try {
			is = multipartFile.getInputStream();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		String originalFilename = multipartFile.getOriginalFilename();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);

		String md5Str = HashUtils.getMd5(is);

		String realPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(rootPath);

		String filePath = rootPath + "/" + md5Str + extension;

		File dest = new File("/" + realPath + md5Str + extension);
		try {
			multipartFile.transferTo(dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}

		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		return filePath;
	}

	public static String saveFile2ContextPath(File src, String rootPath) {
		InputStream is;
		try {
			is = new FileInputStream(src);
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		String originalFilename = src.getName();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);

		String md5Str = HashUtils.getMd5(is);

		String realPath = rootPath;

		String filePath = "/" + md5Str + extension;

		File dest = new File(realPath + filePath);
		System.out.println("======physicalUrl======" + realPath + filePath);
		try {
			FileCopyUtils.copy(src, dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}

		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		return netUrl + filePath;
	}

	public static String saveFile2ContextPath(CommonsMultipartFile multipartFile, String rootPath) {
		InputStream is;
		try {
			is = multipartFile.getInputStream();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		String originalFilename = multipartFile.getOriginalFilename();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);

		String md5Str = HashUtils.getMd5(is);

		String realPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(rootPath);

		String filePath = rootPath + "/" + md5Str + extension;

		File dest = new File(realPath + "/" + md5Str + extension);
		try {
			multipartFile.transferTo(dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}

		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		return filePath;
	}

	public static File getFileByUrl(String fileUrl, String suffix) {
		if (!StringUtils.hasText(suffix)) {
			suffix = fileUrl.substring(fileUrl.lastIndexOf(".") + 1);
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		BufferedOutputStream stream = null;
		InputStream inputStream = null;
		File file = null;
		try {
			URL imageUrl = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			inputStream = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			file = File.createTempFile("pattern", "." + suffix);
//		 logger.info("临时文件创建成功={}", file.getCanonicalPath());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fileOutputStream);
			stream.write(outStream.toByteArray());
		} catch (Exception e) {
//		 logger.error("创建人脸获取服务器图片异常", e);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (stream != null)
					stream.close();
				outStream.close();
			} catch (Exception e) {
//				logger.error("关闭流异常", e);
			}
		}
		return file;
	}

	public static String saveMultipartFile(MultipartFile file, Integer fileType) {
		//  Auto-generated method stub
		System.out.println("rootPath=========" + localUrl);
		InputStream is;
		try {
			is = file.getInputStream();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		String originalFilename = file.getOriginalFilename();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);

		String md5Str = HashUtils.getMd5(is);

		String realPath = localUrl;

		String filePath = "/" + md5Str + extension;
		System.out.println("realPath=========" + realPath + "/" + md5Str + extension);
		File dest = new File(realPath + "/" + md5Str + extension);
		try {
			file.transferTo(dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}

		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		System.out.println("netPath========" + netUrl + filePath);
		return netUrl + filePath;
	}

	public static File InputStreamToFile(File file, InputStream is) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);

			int index;
			byte[] bytes = new byte[1024];
			try {
				while ((index = is.read(bytes)) != -1) {
					fos.write(bytes, 0, index);
					fos.flush();
				}
			} catch (IOException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
			is.close();
			fos.close();
		} catch (FileNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
				fos.close();
			} catch (IOException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}

		}
		return file;
	}


	public static Map<String, Object> pdfToImg(String waterMark, MultipartFile file) {

		Map<String,Object> res = new HashMap<>();

		InputStream is;
		try {
			is = file.getInputStream();
		} catch (Exception e) {
			throw new BusinessException(ResException.MAKE_ERR.getCode(),"文件上传失败");
		}
		String originalFilename = file.getOriginalFilename();
		int index = originalFilename.lastIndexOf(".");
		String extension = originalFilename.substring(index);
		String md5Str = HashUtils.getMd5(is);
/*		String realPath = localUrl;*/

		File dest = new File(localUrl + "/" + md5Str + extension);
		try {
			file.transferTo(dest);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		try {
			is.close();
		} catch (IOException e) {
			throw new FrameworkException(e);
		}

		List<String> strings = PdfUtil.pdfToPng(localUrl + "/" + md5Str+".pdf", "png", waterMark, Color.GRAY);

		res.put("pdfUrl",netUrl+"/" + md5Str+".pdf");
		List<String> filePaths = new ArrayList<>();
		for (String string : strings) {
			filePaths.add(string.replace(localUrl,netUrl).replace(".pdf",".png"));
		}
		res.put("imgUrls",filePaths);
		return res;
	}



	public List<String> uploadToImg(MultipartFile[] files) {
		List<String> filePaths = new ArrayList<>();
		for (MultipartFile file : files) {
			InputStream is;
			try {
				is = file.getInputStream();
			} catch (Exception e) {
				throw new BusinessException(ResException.MAKE_ERR.getCode(),"文件上传失败");
			}
			String originalFilename = file.getOriginalFilename();
			int index = originalFilename.lastIndexOf(".");
			String extension = originalFilename.substring(index);
			String md5Str = HashUtils.getMd5(is);
			String realPath = localUrl;

			File dest = new File(realPath + "/" + md5Str + extension);
			try {
				file.transferTo(dest);
			} catch (Exception e) {
				throw new FrameworkException(e);
			}
			try {
				is.close();
			} catch (IOException e) {
				throw new FrameworkException(e);
			}

			if (extension.equals(".jpg") || extension.equals(".png")){
				markImageByText("震裕溯源",localUrl+"/" + md5Str + extension,localUrl+"/" + md5Str + extension,1);
				filePaths.add(netUrl+"/" + md5Str + extension);
			}else {
				if (!extension.equals(".pdf")){
					try {
						converter.convert(new File(realPath + "/" + md5Str + extension)).to(new File(realPath+"/"+md5Str+".pdf")).as(DefaultDocumentFormatRegistry.PDF).execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				List<String> strings = PdfUtil.pdfToPng(localUrl + "/" + md5Str+".pdf", "png", "点检水印", Color.GRAY);
				for (String string : strings) {
					filePaths.add(string.replace(localUrl,netUrl).replace(".pdf",".png"));
				}
			}

		}
		return filePaths;
	}


	// 水印透明度
	private static final float alpha = 0.5f;
	// 水印横向位置
	private static final int positionWidth = 70;
	// 水印纵向位置
	private static final int positionHeight = 100;
	// 水印文字字体
	private static final Font font = new Font("黑体", Font.BOLD, 32);
	// 水印文字颜色
	private static final Color color = Color.GRAY;

	/**
	 * 给图片添加水印文字、可设置水印文字的旋转角度
	 *
	 * @param logoText
	 * @param srcImgPath
	 * @param targerPath
	 * @param degree
	 */
	public static void markImageByText(String logoText, String srcImgPath, String targerPath, Integer degree) {

		InputStream is = null;
		OutputStream os = null;
		try {
//			targerPath = srcImgPath.substring(0, srcImgPath.lastIndexOf(".")) + NEW_IMAGE_NAME_PRE_STR + "f"
//					+ srcImgPath.substring(srcImgPath.lastIndexOf("."));
			// 1、源图片
			Image srcImg = ImageIO.read(new File(srcImgPath));
			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
					BufferedImage.TYPE_INT_RGB);

			// 2、得到画笔对象
			Graphics2D g = buffImg.createGraphics();
			// 3、设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
					0, null);
			// 4、设置水印旋转
			if (null != degree) {
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}
			// 5、设置水印文字颜色
			g.setColor(color);
			// 6、设置水印文字Font
			g.setFont(font);
			// 7、设置水印文字透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
			g.drawString(logoText, positionWidth, positionHeight);
			// 9、释放资源
			g.dispose();
			// 10、生成图片
			os = new FileOutputStream(targerPath);
			ImageIO.write(buffImg, "JPG", os);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String textToImg(List<String> details,String localPath,String netPath) {

		float height = 12.00f;
		for (String detail : details) {
			height = height+(detail.length()/32)*16+40;
		}
		try {
			return PdfUtil.createFile(MathUtils.getRandomString(10) + ".pdf",height, /*3200*/530, null, details, 20,localPath, netPath,"点检水印", Color.GRAY,"E:\\work_tool_blood_rabbit\\点检\\ac15.png");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new BusinessException(ResException.SYSTEM_ERR);
		}
	}
}
