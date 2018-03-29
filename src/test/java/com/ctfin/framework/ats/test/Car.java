/**
 * Car.java
 * author: yujiakui
 * 2018年1月10日
 * 下午2:16:54
 */
package com.ctfin.framework.ats.test;

/**
 * @author yujiakui
 *
 *         下午2:16:54
 *
 */
public class Car {

	private String name = "carName";

	private CarSeat carSeat;

	private String color = "carColor";

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the carSeat
	 */
	public CarSeat getCarSeat() {
		return carSeat;
	}

	/**
	 * @param carSeat
	 *            the carSeat to set
	 */
	public void setCarSeat(CarSeat carSeat) {
		this.carSeat = carSeat;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

}
