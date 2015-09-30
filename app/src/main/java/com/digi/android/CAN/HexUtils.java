package com.example.android.CAN;

/**
 * Utility class containing methods to work with hexadecimal values
 * and several data type conversions.
 */
public class HexUtils {

	static final String HEXES = "0123456789ABCDEF";

	/**
	 * Converts the given integer into a byte array.
	 * 
	 * @param value Integer to convert to.
	 * @return Byte array of the given integer (4 bytes length).
	 */
	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)((value >>> 24) & 0xFF),
				(byte)((value >>> 16) & 0xFF),
				(byte)((value >>> 8) & 0xFF),
				(byte)(value & 0xFF)
		};
	}

	/**
	 * Converts the given byte array (4 bytes length max) into an
	 * integer.
	 * 
	 * @param value Byte array to convert to integer (4 bytes length max).
	 * @return Converted integer.
	 */
	public static int byteArrayToInt(byte[] b) {
		byte[] values = b;
		if (b.length < 4) {
			values = new byte[4];
			int diff = 4 - b.length;
			for (int i = 0; i < 4; i++) {
				if (i < diff)
					values[i] = 0;
				else
					values[i] = b[i - diff];
			}
		}
		return (values[0] << 24)
				+ ((values[1] & 0xFF) << 16)
				+ ((values[2] & 0xFF) << 8)
				+ (values[3] & 0xFF);
	}

	/**
	 * Converts any byte array in an integer.
	 * 
	 * @param b Byte array to convert to integer.
	 * @param length Length of the bytes to convert in the byte array.
	 * @return Converted integer.
	 */
	public static int byteArrayToInt(byte[] b, int length) {
		return byteArrayToInt(b, length, 0);
	}

	/**
	 * Converts any byte array in an integer.
	 * 
	 * @param b Byte array to convert to integer.
	 * @param length Length of the bytes to convert in the byte array.
	 * @param offset Offset to start reading bytes in the byte array.
	 * @return Converted integer.
	 */
	public static int byteArrayToInt(byte[] b, int length, int offset) {
		int value = 0;
		for (int i = 0; i < length; i++) {
			int shift = (length - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * Converts the given short into a byte array.
	 * 
	 * @param value Short to convert to.
	 * @return Byte array of the given short (2 bytes length).
	 */
	public static byte[] shortToByteArray(short value) {
		byte[] b = new byte[2];
		b[0] = (byte)((value >> 8) & 0xFF);
		b[1] = (byte)(value & 0xFF);
		return b;
	}

	/**
	 * Converts a long value to a 4-byte-array-length array .
	 * 
	 * @param value Long value to convert.
	 * @return Byte array containing the long value.
	 */
	public static byte[] longTo4ByteArray(long value) {
		return new byte[] {
				(byte) (value >>> 24),
				(byte) (value >>> 16),
				(byte) (value >>> 8),
				(byte) value
		};
	}
	
	/**
	 * Converts the given byte array into a hex string.
	 * 
	 * @param Byte array to convert to hex string.
	 * @return Converted byte array to hex string.
	 */
	public static String byteArrayToHexString(byte[] value) {
		if (value == null )
			return null;
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			hex.append(HEXES.charAt((value[i] & 0xF0) >> 4))
			.append(HEXES.charAt((value[i] & 0x0F)));
		}
		return hex.toString();
	}

	/**
	 * Converts the given hex string into a byte array.
	 * 
	 * @param value Hex string to convert to.
	 * @return Byte array of the given hex string.
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

	/**
	 * Converts the given string into a byte array.
	 * 
	 * @param value String to convert to.
	 * @return Byte array of the given string.
	 */
	public static byte[] stringToByteArray(String value) {
		return value.getBytes();
	}

	/**
	 * Converts the given string byte array into a string.
	 * 
	 * @param Byte array to convert to string.
	 * @return Converted String.
	 */
	public static String byteArrayToString(byte[] value) {
		return new String(value);
	}
}
