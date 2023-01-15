package qichen.code.controller.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import qichen.code.controller.conf.FilePathConf;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.ResponseBean;
import qichen.code.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/api/v1/files")
public class FileController {

	@Autowired
	private FileUtils fileUtils;
	@Autowired
	private FilePathConf filePathConf;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Object upload(@RequestParam(value = "file", required = false) CommonsMultipartFile[] files,
			@RequestParam(value = "file1", required = false) CommonsMultipartFile file1,
			@RequestParam(value = "fileType", required = false) Integer fileType,
			@RequestParam(value = "files", required = false) MultipartFile[] file2) {
		List<String> list = new ArrayList<>();
		try {
			if (files != null) {
				for (CommonsMultipartFile file : files) {
					String path = FileUtils.saveMultipartFile(file, fileType);
					list.add(path);
				}
			}

			if (file1 != null) {
				String path = FileUtils.saveMultipartFile(file1, fileType);

				Map<String, String> data = new HashMap<>();
				data.put("fileType", fileType.toString());
				data.put("state", "SUCCESS");
				data.put("url", path);
				return data;
			}

			if (file2 != null) {
				for (MultipartFile file : file2) {
					String path = FileUtils.saveMultipartFile(file, fileType);
					list.add(path);
				}
			}

			ResponseBean responseBean = new ResponseBean();

			if (list.size() == 1) {
				responseBean.setData(list.get(0));
			} else {
				responseBean.setData(list);
			}

			return responseBean;
		}catch (Exception exception){
			exception.printStackTrace();
			log.error(exception.getMessage());
			return new ResponseBean(ResException.SYSTEM_ERR);
		}
	}

	@ResponseBody
	@PostMapping("/uploadToImg")
	public ResponseBean uploadToImg(@RequestParam(value = "files") MultipartFile[] files){

		try {
			List<String> filePaths = fileUtils.uploadToImg(files);
			return new ResponseBean(filePaths);
		} catch (BusinessException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return new ResponseBean(e);
		} catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return new ResponseBean(ResException.SYSTEM_ERR);
		}
	}


	@ResponseBody
	@PostMapping("/pdfToImg")
	public ResponseBean pdfToImg(@RequestParam(value = "waterMark",required = false) String waterMark,
								 @RequestParam(value = "file") MultipartFile file){
		try {
			Map<String,Object> res = FileUtils.pdfToImg(waterMark,file);
			return new ResponseBean(res);
		} catch (BusinessException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return new ResponseBean(e);
		} catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return new ResponseBean(ResException.SYSTEM_ERR);
		}
	}

	@ResponseBody
	@GetMapping("/getOutPutStream")
	public void getOutPutStream(HttpServletResponse response,@RequestParam(value = "path") String path){
		try {
			File file = new File(path.replace(filePathConf.getQuestion_net(),filePathConf.getQuestion_local()));
			BufferedImage bufferedImage = ImageIO.read(file);
			setResponseHeader(response,path);
			ServletOutputStream outputStream = response.getOutputStream();
			ImageIO.write(bufferedImage,"png",outputStream);
		} catch (Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}


	// 发送响应流方法
	public void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



}
