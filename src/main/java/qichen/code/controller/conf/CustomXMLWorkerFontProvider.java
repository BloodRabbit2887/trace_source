package qichen.code.controller.conf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * 解决XMLWorkerHelper中文不显示。
 * <p>使用iTextAsian.jar中自带的中文字体
 *
 */
@Slf4j
public class CustomXMLWorkerFontProvider extends XMLWorkerFontProvider {

    @Override
    public Font getFont(final String fontName, final String encoding, final boolean embedded, final float size, final int style,
                        final BaseColor color) {
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            Font font = new Font(bf, size, style, color);
            font.setColor(color);
            // log.info("PDF文档字体初始化完成!");
            return font;
        } catch (Exception e) {
            log.error("exception:", e);
        }
        return null;
    }
}

