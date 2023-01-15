package qichen.code.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.*;

/**
 * fastjson过滤多个class的属性
 * 
 * @author zpl
 *
 */
public class MultiPropertyPreFilter implements PropertyPreFilter {

	private final List<Class<?>> clazzs = new ArrayList<>();

	private final Map<Class<?>, Set<String>> includes = new HashMap<>();
	private final Map<Class<?>, Set<String>> excludes = new HashMap<>();

	public MultiPropertyPreFilter(Class<?>[] clazzs, String[][] properties) {
		for (int i = 0; i < clazzs.length; i++) {
			this.clazzs.add(clazzs[i]);

			Set<String> includeSet = new HashSet<>();
			includes.put(clazzs[i], includeSet);

			for (String item : properties[i]) {
				if (item != null) {
					includeSet.add(item);
				}
			}
		}
	}

	public List<Class<?>> getClazzs() {
		return clazzs;
	}

	public Map<Class<?>, Set<String>> getIncludes() {
		return includes;
	}

	public Map<Class<?>, Set<String>> getExcludes() {
		return excludes;
	}

	public boolean apply(JSONSerializer serializer, Object source, String name) {
		if (source == null) {
			return true;
		}

		for (Class<?> clazz : clazzs) {
			if (!clazz.isInstance(source)) {
				continue;
			}

			Set<String> excludeSet = excludes.get(clazz);
			if (excludeSet != null) {
				if (excludeSet.contains(name)) {
					return false;
				}
			}

			Set<String> includeSet = includes.get(clazz);
			if (includeSet.size() == 0 || includeSet.contains(name)) {
				return true;
			}

			return false;
		}

		return true;
	}

}
