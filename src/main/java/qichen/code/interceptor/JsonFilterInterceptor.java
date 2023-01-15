package qichen.code.interceptor;

import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import qichen.code.annotation.JsonPropertyFilter;
import qichen.code.annotation.JsonPropertyFilters;
import qichen.code.serializer.MultiPropertyPreFilter;
import qichen.code.utils.ContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class JsonFilterInterceptor extends HandlerInterceptorAdapter {

	private Map<Object, PropertyPreFilter> filterMap = new HashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			Method method = ((HandlerMethod) handler).getMethod();

			PropertyPreFilter filter = filterMap.get(method);
			if (filter != null) {
				ContextUtils.setPropertyPreFilter(filter);
				return true;
			}

			JsonPropertyFilter filter_a = method.getAnnotation(JsonPropertyFilter.class);
			if (filter_a != null) {
				filter = new SimplePropertyPreFilter(filter_a.clazz(), filter_a.value());
				filterMap.put(method, filter);
				ContextUtils.setPropertyPreFilter(filter);

				return true;
			} else {
				JsonPropertyFilters filters_a = method.getAnnotation(JsonPropertyFilters.class);

				if (filters_a != null) {
					JsonPropertyFilter[] filter_a_s = filters_a.value();

					Class<?>[] clazzs = new Class<?>[filter_a_s.length];
					String[][] properties = new String[filter_a_s.length][];

					for (int i = 0; i < filter_a_s.length; i++) {
						JsonPropertyFilter item = filter_a_s[i];
						clazzs[i] = item.clazz();
						properties[i] = item.value();
					}

					filter = new MultiPropertyPreFilter(clazzs, properties);
					filterMap.put(method, filter);
					ContextUtils.setPropertyPreFilter(filter);
					return true;
				}
			}
		}

		ContextUtils.setPropertyPreFilter(null);

		return true;
	}

}
