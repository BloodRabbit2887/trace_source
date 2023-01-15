package qichen.code.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.controller.conf.CustomXMLWorkerFontProvider;
import qichen.code.controller.conf.FilePathConf;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/**
 * pdf工具类
 *
 * @author wuhuc
 * @data 2022/7/12 - 9:12
 */
@Slf4j
@Component
public class PdfUtil {

    @Autowired
    private static FilePathConf filePathConf;

    public static final String WATER_PATH = "C:\\tomcat_static\\spot_check\\upload\\ac15.png";

    public static String createFile(String fileName,float pxh,float pxw,String img,List<String> details,float marginRight,String localUrl,String netUrl,String watermark,Color color,String waterImgPath) throws Exception {
        try {

            Rectangle rectangle = new Rectangle(/*weight/300*72*/pxw/*+2*marginRight*/, /*height/300*72*/pxh/*+2*marginRight*/);

/*            rectangle.setBackgroundColor(new BaseColor(255,255,255,0));*/

            rectangle.setBorderColor(new BaseColor(255,255,255,0));

            Document document = new Document(rectangle,0.0F,0.0F,0.0F,0.0F);

            PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(localUrl+fileName));
            writer.setInitialLeading(20);

            document.open();

            if (!CollectionUtils.isEmpty(details) && details.size()>0){
                BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                Font font = new Font(baseFont);
                font.setSize(16);
                font.setColor(BaseColor.BLACK);
                font.setStyle(Font.NORMAL);
                for (String detail : details) {
                    if (!StringUtils.isEmpty(detail) && detail.length()>0){

                        if (detail.contains("</")){
                            List<Element> elements = HTMLWorker.parseToList(new StringReader(detail), new StyleSheet());
                            Paragraph par = new Paragraph();
                            par.addAll(elements);
                            document.add(par);
                        }else {
                            System.out.println("detail==="+detail);
                            Paragraph paragraph = new Paragraph(detail, font);
                            document.add(paragraph);
                        }

                    }else {
                        Paragraph paragraph = new Paragraph(detail);
                        document.add(paragraph);
                    }
                }
            }

/*            if (!CollectionUtils.isEmpty(imgs) && imgs.size()>0){
                for (String img : imgs) {
                    Image image = Image.getInstance(img);
                    image.setAbsolutePosition(weight*0.75f,height*0.75f);
                    document.add(image);
                }
            }*/

            if (!StringUtils.isEmpty(img) && img.length()>0){
                Image image = Image.getInstance(img);
                document.add(image);
            }

            document.addCreationDate();
            document.addAuthor("测试");
            document.close();
            writer.close();
            pdf2png(localUrl,fileName.replace(".pdf",""),"png",watermark,color);

            return netUrl+fileName.replace(".pdf",".png");
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 通过html生成文件
     * @param htmlContent  html格式内容
     */
    public static void createdPdfByItextHtml(String htmlContent,String localUrl,String filePath,Color color,float pxh,float pxw){
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        PdfWriter writer = null;

        try {
            htmlContent = formatHtml(htmlContent);
            // 1. 获取生成pdf的html内容
            inputStream= new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
            outputStream = new FileOutputStream(localUrl+filePath);


            Rectangle rectangle = new Rectangle(/*weight/300*72*/pxw/*+2*marginRight*/, /*height/300*72*/pxh/*+2*marginRight*/);

            /*            rectangle.setBackgroundColor(new BaseColor(255,255,255,0));*/

            rectangle.setBorderColor(new BaseColor(255,255,255,0));

            Document document = new Document(rectangle,0.0F,0.0F,0.0F,0.0F);

            writer = PdfWriter.getInstance(document, outputStream);
            writer.setInitialLeading(20);

/*            Document document = new Document();*/

            document.open();
            // 2. 添加字体
//            XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//            fontImp.register(getFontPath());
            // 3. 设置编码
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, inputStream, StandardCharsets.UTF_8,new CustomXMLWorkerFontProvider());
            // 4. 关闭,(不关闭则会生成无效pdf)
            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            try {
                if(writer!=null){
                    writer.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                pdf2png(localUrl,filePath.replace(".pdf",""),"png","点检水印",color);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }


    private static String formatHtml(String html) {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);

        // jsoup标准化标签，生成闭合标签
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return doc.html();
    }


    public static List<String> pdfToPng(String filename,String type,String watermark,Color color) {
        // 将pdf装图片 并且自定义图片得格式大小

        List<String> fileNames = new ArrayList<>();

        try {
            File file = new File(filename);
            PDDocument doc = PDDocument.load(file);
            doc.setAllSecurityToBeRemoved(true);

/*            PDImageXObject pdImage = PDImageXObject.createFromFile(WATER_PATH, doc);*/

            // 设置透明度
            PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
            pdExtGfxState.setNonStrokingAlphaConstant(0.2f);
            pdExtGfxState.setAlphaSourceFlag(true);
            pdExtGfxState.getCOSObject().setItem(COSName.BM, COSName.MULTIPLY);

            for (PDPage page : doc.getPages()) {
                PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

                // 加载水印字体
                PDFont font = PDType0Font.load(doc, new FileInputStream("C:\\Windows\\Fonts\\simhei.ttf"), true);

                PDExtendedGraphicsState r = new PDExtendedGraphicsState();

                // 设置透明度
                r.setNonStrokingAlphaConstant(0.2f);
                r.setAlphaSourceFlag(true);
                stream.setGraphicsStateParameters(r);
                stream.setNonStrokingColor(color);

                stream.beginText();
                stream.setFont(font, 20);
                stream.newLineAtOffset(0, -15);

                // 获取PDF页面大小
                float pageHeight = page.getMediaBox().getHeight();
                float pageWidth = page.getMediaBox().getWidth();

                // 根据纸张大小添加水印，30度倾斜
                for (int h = 10; h < pageHeight; h = h + 100) {
                    for (int w = - 10; w < pageWidth; w = w + 100) {
                        stream.setTextMatrix(Matrix.getRotateInstance(0.3, w, h));
                        stream.showText(watermark);
                        stream.setGraphicsStateParameters(pdExtGfxState);
/*                        stream.drawImage(pdImage, w, h);*/
                    }
                }

                // 结束渲染，关闭流
                stream.endText();
                stream.restoreGraphicsState();
                stream.close();
            }

            doc.save(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();

            for (int k = 0; k < pageCount; k++) {
                BufferedImage image = renderer.renderImageWithDPI(k, 296/*,ImageType.ARGB*/); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                String replaceName = filename.replace(".pdf", "_" + k + "." + type);
                ImageIO.write(image, type, new File(replaceName));
                fileNames.add(replaceName);
            }

            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    public static void pdf2png(String fileAddress,String filename,String type,String watermark,Color color) {
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress+"\\"+filename+".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            doc.setAllSecurityToBeRemoved(true);

            PDImageXObject pdImage = PDImageXObject.createFromFile(WATER_PATH, doc);

            // 设置透明度
            PDExtendedGraphicsState pdExtGfxState = new PDExtendedGraphicsState();
            pdExtGfxState.setNonStrokingAlphaConstant(0.2f);
            pdExtGfxState.setAlphaSourceFlag(true);
            pdExtGfxState.getCOSObject().setItem(COSName.BM, COSName.MULTIPLY);



            for (PDPage page : doc.getPages()) {
                PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

                // 加载水印字体
                PDFont font = PDType0Font.load(doc, new FileInputStream("C:\\Windows\\Fonts\\simhei.ttf"), true);

                PDExtendedGraphicsState r = new PDExtendedGraphicsState();

                // 设置透明度
                r.setNonStrokingAlphaConstant(0.2f);
                r.setAlphaSourceFlag(true);
                stream.setGraphicsStateParameters(r);
                stream.setNonStrokingColor(color);

                stream.beginText();
                stream.setFont(font, 30);
                stream.newLineAtOffset(0, -15);

                // 获取PDF页面大小
                float pageHeight = page.getMediaBox().getHeight();
                float pageWidth = page.getMediaBox().getWidth();


                // 根据纸张大小添加水印，30度倾斜
                for (int h = 10; h < pageHeight; h = h + 100) {
                    for (int w = - 10; w < pageWidth; w = w + 100) {
                        stream.setTextMatrix(Matrix.getRotateInstance(0.3, w, h));
                        stream.showText(watermark);
                        stream.setGraphicsStateParameters(pdExtGfxState);
                        stream.drawImage(pdImage, w, h);
                    }
                }


                // 结束渲染，关闭流
                stream.endText();
                stream.restoreGraphicsState();
                stream.close();
            }

            doc.save(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int k = 0; k < pageCount; k++) {
                BufferedImage image = renderer.renderImageWithDPI(k, 296/*,ImageType.ARGB*/); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, type, new File(fileAddress+"\\"+filename+"."+type));
            }
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createPDF(Map<String, Object> data, Map<String,String> imageMap, String outfile, String templateFile,String localUrl,String watermark,Color color) {
        PdfReader reader = null;
        AcroFields s;
        PdfStamper ps;
        ByteArrayOutputStream bos = null;
        //输出文档路径及名称
        File outFile = new File(outfile);
        try {
            reader = new PdfReader(templateFile);
            bos = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, bos);
            s = ps.getAcroFields();
            // 处理文字
            if (data!=null){
                for (String key : data.keySet()) {
                    if (data.get(key) != null) {
                       s.setField(key, data.get(key).toString());
                    }
                }
            }

            // 处理图片
            if (imageMap!=null) {
                for (String key : imageMap.keySet()) {
                    try {
                        String value = imageMap.get(key);
                        int pageNo = s.getFieldPositions(key).get(0).page;
                        Rectangle signRect = s.getFieldPositions(key).get(0).position;
                        float x = signRect.getLeft();
                        float y = signRect.getBottom();
                        // 根据路径读取图片
                        Image image = Image.getInstance(value);
                        // 获取图片页面
                        PdfContentByte under = ps.getOverContent(pageNo);
                        // 图片大小自适应
                        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                        // 设置图片位置，以为我们以左下角为起始点，所以这里x、y加上偏移量，偏移量为计算的居中量
                        image.setAbsolutePosition(x + (signRect.getWidth() - image.getScaledWidth()) / 2, y + (signRect.getHeight() - image.getScaledHeight()) / 2);
                        // 添加图片
                        under.addImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            ps.setFormFlattening(true);
            ps.close();

            //生成pdf路径存放的路径
            OutputStream fos = new FileOutputStream(outfile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            bos.close();

            pdf2png(localUrl,outfile.replace(".pdf","").replace(localUrl,""),"png",watermark,color);
        } catch (IOException | DocumentException e) {
            log.error("读取文件异常");
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                reader.close();
            } catch (IOException e) {
                log.error("关闭流异常");
                e.printStackTrace();
            }
        }
        return outFile;
    }


}

