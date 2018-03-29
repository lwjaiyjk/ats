/**
 * ObjectCompareUtilsTest.java
 * author: yujiakui
 * 2018年1月10日
 * 下午2:15:55
 */
package com.ctfin.framework.ats.test;

import com.ctfin.framework.ats.ObjectCompareUtils;

/**
 * @author yujiakui
 *
 *         下午2:15:55
 *
 */
public class ObjectCompareUtilsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Car car1 = new Car();
		Car car2 = new Car();

		CarSeat carSeat1 = new CarSeat();
		carSeat1.setColor("greenCarSeat1");
		carSeat1.setName("carSeatName1");
		car1.setCarSeat(carSeat1);

		CarSeat carSeat2 = new CarSeat();
		carSeat2.setName("carSeatName2");
		car2.setCarSeat(carSeat2);

		car1.setColor("carColor1");
		car2.setColor("carColor2");
		ObjectCompareUtils.assertEqualsWithExcludeFields(car1, car2, "carSeat.name",
				"carSeat.color");
	}

}
