/**
 * ObjectCompareUtils.java
 * author: yujiakui
 * 2017年12月20日
 * 下午3:33:01
 */
package com.ctfin.framework.ats;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.beust.jcommander.internal.Lists;

/**
 * @author yujiakui
 *
 *         下午3:33:01
 *
 *         对象比较工具
 */
public class ObjectCompareUtils {

	/**
	 * 排除某系属性的对象对比
	 *
	 * @param expectedObj
	 * @param actualObj
	 * @param excludeFieldName
	 */
	public static void assertEqualsWithExcludeFields(Object expectedObj, Object actualObj,
			String... excludeFieldName) {
		assertEqualsWithFieldsAndFlag(expectedObj, actualObj, true,
				expectedObj.getClass().getSimpleName(), excludeFieldName);
	}

	/**
	 * 仅仅对比对象的某些属性
	 *
	 * @param expectedObj
	 * @param actualObj
	 * @param excludeFieldName
	 */
	public static void assertEqualsWithIncludeFields(Object expectedObj, Object actualObj,
			String... includeFieldName) {
		assertEqualsWithFieldsAndFlag(expectedObj, actualObj, false,
				expectedObj.getClass().getSimpleName(), includeFieldName);
	}

	/**
	 * @param expectedObj
	 * @param actualObj
	 * @param excludeFlag
	 * @param FieldName
	 */
	private static void assertEqualsWithFieldsAndFlag(Object expectedObj, Object actualObj,
			Boolean excludeFlag, String fieldPath, String... fieldNames) {

		if (fieldNames == null || fieldNames.length == 0) {
			assertEquals(expectedObj, actualObj, "字段路径:" + fieldPath);
			return;
		}
		List<String> fieldNameLists = Arrays.asList(fieldNames);

		Field[] fields = expectedObj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			List<String> fieldNameSuffixs = Lists.newArrayList();
			boolean fieldNameExistFlag = false;
			String fieldNamePoint = field.getName() + ".";
			for (String fieldName : fieldNameLists) {
				if (fieldName.equals(field.getName())) {
					fieldNameExistFlag = true;
				} else if (fieldName.startsWith(fieldNamePoint)) {
					// 所有以属性名. 前缀开头的
					String fieldNameSuffix = fieldName.substring(fieldNamePoint.length());
					fieldNameSuffixs.add(fieldNameSuffix);
				}
			}

			// 获取field对应的对象
			Object fieldExpectObj = getFieldObj(field, expectedObj);
			Object fieldActualObj = getFieldObj(field, actualObj);
			String tmpFieldPath = fieldPath + "." + field.getName();
			// 排除对应的属性存在则继续下一个属性
			if (excludeFlag && fieldNameExistFlag) {
				continue;
			} else if (!excludeFlag && !fieldNameExistFlag
					&& CollectionUtils.isEmpty(fieldNameSuffixs)) {
				// 包含对应的属性不存在，且对应的属性后缀列表为空，则继续下一个属性
				continue;
			} else if (!excludeFlag && fieldNameExistFlag
					&& CollectionUtils.isEmpty(fieldNameSuffixs)) {
				// 包含 ， 属性对应的存在，且对应的属性后缀为空，则直接进行对象比较
				assertEquals(fieldExpectObj, fieldActualObj, "字段路径：" + tmpFieldPath);
			} else {
				// 递归进行比较
				assertEqualsWithFieldsAndFlag(fieldExpectObj, fieldActualObj, excludeFlag,
						tmpFieldPath, fieldNameSuffixs.toArray(new String[0]));
			}

		}
	}

	/**
	 * 字段field进行对比
	 *
	 * @param field
	 * @param expectedObj
	 * @param actualObj
	 */
	private static Object getFieldObj(Field field, Object obj) {
		Object fieldObj = null;
		try {
			fieldObj = field.get(obj);

		} catch (IllegalArgumentException e) {
			System.out.println("字段name=" + field.getName() + "异常" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("字段name=" + field.getName() + "异常" + e.getMessage());
			e.printStackTrace();
		}

		return fieldObj;
	}
}
