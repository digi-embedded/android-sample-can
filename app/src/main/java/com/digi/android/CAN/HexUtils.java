/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */

package com.digi.android.CAN;

/**
 * Utility class containing methods to work with hexadecimal values and
 * several data type conversions.
 */
public class HexUtils {

	// Constants.
	static final String HEXES = "0123456789ABCDEF";

	/**
	 * Converts the given byte array into an hexadecimal string.
	 * 
	 * @param value Byte array to convert to hexadecimal string.
	 *
	 * @return Converted byte array to hexadecimal string. If {@code value} is
	 *         {@code null}, the returned string is {@code null}.
	 */
	public static String byteArrayToHexString(byte[] value) {
		if (value == null )
			return null;

		StringBuilder hex = new StringBuilder();
		for (byte aValue : value) {
			hex.append(HEXES.charAt((aValue & 0xF0) >> 4))
					.append(HEXES.charAt((aValue & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * Converts the given hexadecimal string into a byte array.
	 * 
	 * @param value Hexadecimal string to convert to.
	 *
	 * @return Byte array of the given hexadecimal string.
	 */
	public static byte[] hexStringToByteArray(String value) {
		int len = value.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
					+ Character.digit(value.charAt(i+1), 16));
		}
		return data;
	}
}
