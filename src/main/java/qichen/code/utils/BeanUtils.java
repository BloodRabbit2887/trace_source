package qichen.code.utils;

import org.springframework.util.Assert;
import qichen.code.exception.BeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;


public class BeanUtils {

	public static void copyProperties(Object source, Object target, String... ignoreProperties) {
		org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
	}

	/**
	 * 复制属性（忽略空值）
	 * 
	 * @param source
	 * @param target
	 * @param ignoreProperties
	 */
	public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null
					&& (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);

						// 不复制空属性
						if (value == null) {
							continue;
						}

						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, value);
					} catch (Throwable ex) {
						throw new BeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	public static <T> T copyAs(Object source, Class<T> clazz, String... ignoreProperties) {
		if (source == null) {
			return null;
		}

		T t;
		try {
			t = clazz.newInstance();
		} catch (Exception e) {
			throw new BeanException("创建对象失败", e);
		}

		org.springframework.beans.BeanUtils.copyProperties(source, t, ignoreProperties);

		return t;
	}

	public static <T> List<T> copyAs(List<?> source, Class<T> clazz, String... ignoreProperties) {
		if (source == null) {
			return null;
		}

		List<T> list = new ArrayList<>();
		for (Object o : source) {
			T t = copyAs(o, clazz, ignoreProperties);

			list.add(t);
		}

		return list;
	}

}
