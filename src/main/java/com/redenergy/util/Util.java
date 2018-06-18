// Copyright Red Energy Limited 2017

package com.redenergy.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Locale;

public class Util {

	/**
	 * Parsing the date
	 *
	 * @param date
	 * 
	 * @return - LocalDate object
	 * 
	 */
	public static LocalDate parseDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		formatter = formatter.withLocale(Locale.ENGLISH);
		return LocalDate.parse(date, formatter);
	}

}
