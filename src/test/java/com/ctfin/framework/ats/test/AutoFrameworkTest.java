/**
 * TestDBUtils.java
 * author: yujiakui
 * 2017年9月5日
 * 上午9:00:00
 */
package com.ctfin.framework.ats.test;

import static org.testng.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.testng.annotations.Test;

import com.ctfin.framework.ats.AbstractAutoTestFramework;
import com.ctfin.framework.ats.DBUtils;
import com.ctfin.framework.ats.UseCaseInfo;
import com.ctfin.framework.ats.XTest;

/**
 * @author yujiakui
 *
 *         上午9:00:00
 *
 *         注意目前这个
 */

// @XTest(relatePath = "usecase", desc = "数据库工具测试")
public class AutoFrameworkTest extends AbstractAutoTestFramework {

	/*@BeforeMethod
	public void prepare(Method method, Object[] params) {
		// 测试前删除影响的数据
		DBUtils.deleteDataInDB("ct_user", "id in (2234578,2234578) ");
		DBUtils.deleteDataInDB("ct_user", "phone in ('1234567890') ");
	}*/

	/*@Test(dataProvider = "TestDataProvider")
	@XTest(testOnly = "dbutil_001", relatePath = "usecase//method1")*/
	public void test(UseCaseInfo useCaseInfo, Map<String, Object> paramMap,
			Map<String, Object> preparedData, Map<String, Object> expectedData, Object extParam1,
			Object extParam2) {
		System.out.println(useCaseInfo);
		System.out.println(paramMap);
		System.out.println(preparedData);
		System.out.println(expectedData);
		// 数据库准备
		DBUtils.prepare(preparedData);

		// 数据库结果和期望结果进行校验
		assertEquals(DBUtils.dbCheck(expectedData, null, useCaseInfo), true);

	}

	// @Test
	@Test(dataProvider = "TestDataProvider")
	@XTest(testOnly = "dbutil*", relatePath = "usecase")
	public void test1(UseCaseInfo useCaseInfo, Map<String, Object> paramMap,
			Map<String, Object> preparedData, Map<String, Object> expectedData, Object extParam1,
			Object extParam2) {
		System.out.println(useCaseInfo);
		System.out.println(paramMap);
		System.out.println(preparedData);
		System.out.println(expectedData);
		// 数据库准备
		DBUtils.prepare(preparedData);

		// 数据库结果和期望结果进行校验
		assertEquals(DBUtils.dbCheck(expectedData, null, useCaseInfo), true);

	}

	public static void main(String[] args) {
		Method[] methods = AutoFrameworkTest.class.getDeclaredMethods();
		for (Method method : methods) {
			if (!"test1".equals(method.getName())) {
				continue;
			}
			System.out.println("-------------------" + method.getName());

			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				System.out.println(annotation.toString());
				Annotation[] annotations2 = annotation.annotationType().getAnnotations();
				System.out.println(annotations2);
			}
			System.out.println("---------declared--------");
			annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				System.out.println(annotation.toString());
			}
			Test[] tests = method.getDeclaredAnnotationsByType(Test.class);
			System.out.println(tests);

		}
	}

	/*@AfterMethod
	public void clear() {
		// 测试后删除产生的数据
		DBUtils.deleteDataInDB("ct_user", "id in (2234578,22345738) ");
	}*/
}
