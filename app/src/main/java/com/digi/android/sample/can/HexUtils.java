/**
 * Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.can;

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
					+ Character.digit(value.charAt(i + 1), 16));
		}
		return data;
	}
}
