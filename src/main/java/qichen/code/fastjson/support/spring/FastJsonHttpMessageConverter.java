package qichen.code.fastjson.support.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import qichen.code.utils.ContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class FastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	public final static Charset UTF8 = StandardCharsets.UTF_8;

	private Charset charset = UTF8;

	private SerializerFeature[] features = new SerializerFeature[0];

	public FastJsonHttpMessageConverter() {
		super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	public Charset getCharset() {
		return this.charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public SerializerFeature[] getFeatures() {
		return features;
	}

	public void setFeatures(SerializerFeature... features) {
		this.features = features;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		InputStream in = inputMessage.getBody();

		byte[] buf = new byte[1024];
		for (;;) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}
			if (len > 0) {
				baos.write(buf, 0, len);
			}
		}

		byte[] bytes = baos.toByteArray();
		return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(), clazz);
	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		PropertyPreFilter filter = ContextUtils.getPropertyPreFilter();
		OutputStream out = outputMessage.getBody();
		setFeatures(SerializerFeature.WriteDateUseDateFormat);
		String text = JSON.toJSONString(obj, filter, features);
		byte[] bytes = text.getBytes(charset);
		out.write(bytes);
	}



}
