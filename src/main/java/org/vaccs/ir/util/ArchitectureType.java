/**
 * 
 */
package org.vaccs.ir.util;

/**
 * @author carr
 *
 */
public abstract class ArchitectureType {
	public static final int BIT32 = 0;
	public static final int BIT64 = 1;

	public static final String BIG_ENDIAN = "Big Endian";
	public static final String LITTLE_ENDIAN = "Little Endian";

	protected static final String CHAR_MIN_VALUE = "-128";
	protected static final String CHAR_MAX_VALUE = "127";
	protected static final String UCHAR_MIN_VALUE = "0";
	protected static final String UCHAR_MAX_VALUE = "255";

	protected static final String SHORT_MIN_VALUE = "-32768";
	protected static final String SHORT_MAX_VALUE = "32767";
	protected static final String USHORT_MIN_VALUE = "0";
	protected static final String USHORT_MAX_VALUE = "65535";

	protected static final String INT_MIN_VALUE = "-2147483648";
	protected static final String INT_MAX_VALUE = "2147483647";
	protected static final String UINT_MIN_VALUE = "0";
	protected static final String UINT_MAX_VALUE = "4294967295";

	protected static final String CHAR_MIN_LABEL = "0x80=2^(8-1)";
	protected static final String CHAR_MAX_LABEL = "0x7F=2^8";
	protected static final String UCHAR_MIN_LABEL = "0x00=0";
	protected static final String UCHAR_MAX_LABEL = "0xFF=2^8";

	protected static final String SHORT_MIN_LABEL = "0x8000=-2^(16-1)";
	protected static final String SHORT_MAX_LABEL = "0x7FFF=2^(16-1)-1";
	protected static final String USHORT_MIN_LABEL = "0x0000=0";
	protected static final String USHORT_MAX_LABEL = "0xFFFF=2^16";

	protected static final String INT_MIN_LABEL = "0x80000000=-2^(32-1)";
	protected static final String INT_MAX_LABEL = "0x7FFFFFFF=2^(32-1)-1";
	protected static final String UINT_MIN_LABEL = "0x00000000=0";
	protected static final String UINT_MAX_LABEL = "0xFFFFFFFF=2^32";

	protected static String LONG_MIN_VALUE;
	protected static String LONG_MAX_VALUE;
	protected static String ULONG_MIN_VALUE;
	protected static String ULONG_MAX_VALUE;

	protected static String LONG_MIN_LABEL;
	protected static String LONG_MAX_LABEL;
	protected static String ULONG_MIN_LABEL;
	protected static String ULONG_MAX_LABEL;

	public ArchitectureType(String longMinValue, String longMaxValue, String ulongMinValue, String ulongMaxValue,
			String longMinLabel, String longMaxLabel, String ulongMinLabel, String ulongMaxLabel) {
		LONG_MIN_VALUE = longMinValue;
		LONG_MAX_VALUE = longMaxValue;
		ULONG_MIN_VALUE = ulongMinValue;
		ULONG_MAX_VALUE = ulongMaxValue;

		LONG_MIN_LABEL = longMinLabel;
		LONG_MAX_LABEL = longMaxLabel;
		ULONG_MIN_LABEL = ulongMinLabel;
		ULONG_MAX_LABEL = ulongMaxLabel;
	}

	public int byteSize() {
		return 1;
	}

	public int shortSize() {
		return 2;
	}

	public int intSize() {
		return 4;
	}

	public abstract int longSize();

	public String getMinCharValue() {
		return CHAR_MIN_VALUE;
	}

	public String getMaxCharValue() {
		return CHAR_MAX_VALUE;
	}

	public String getMinUnsignedCharValue() {
		return UCHAR_MIN_VALUE;
	}

	public String getMaxUnsignedCharValue() {
		return UCHAR_MAX_VALUE;
	}

	public String getMinShortValue() {
		return SHORT_MIN_VALUE;
	}

	public String getMaxShortValue() {
		return SHORT_MAX_VALUE;
	}

	public String getMinUnsignedShortValue() {
		return USHORT_MIN_VALUE;
	}

	public String getMaxUnsignedShortValue() {
		return USHORT_MAX_VALUE;
	}

	public String getMinIntValue() {
		return INT_MIN_VALUE;
	}

	public String getMaxIntValue() {
		return INT_MAX_VALUE;
	}

	public String getMinUnsignedIntValue() {
		return UINT_MIN_VALUE;
	}

	public String getMaxUnsignedIntValue() {
		return UINT_MAX_VALUE;
	}

	public String getMinLongValue() {
		return LONG_MIN_VALUE;
	}

	public String getMaxLongValue() {
		return LONG_MAX_VALUE;
	}

	public String getMinUnsignedLongValue() {
		return ULONG_MIN_VALUE;
	}

	public String getMaxUnsignedLongValue() {
		return ULONG_MAX_VALUE;
	}

	public String getMinCharLabel() {
		return CHAR_MIN_LABEL;
	}

	public String getMaxCharLabel() {
		return CHAR_MAX_LABEL;
	}

	public String getMinUnsignedCharLabel() {
		return UCHAR_MIN_LABEL;
	}

	public String getMaxUnsignedCharLabel() {
		return UCHAR_MAX_LABEL;
	}

	public String getMinShortLabel() {
		return SHORT_MIN_LABEL;
	}

	public String getMaxShortLabel() {
		return SHORT_MAX_LABEL;
	}

	public String getMinUnsignedShortLabel() {
		return USHORT_MIN_LABEL;
	}

	public String getMaxUnsignedShortLabel() {
		return USHORT_MAX_LABEL;
	}

	public String getMinIntLabel() {
		return INT_MIN_LABEL;
	}

	public String getMaxIntLabel() {
		return INT_MAX_LABEL;
	}

	public String getMinUnsignedIntLabel() {
		return UINT_MIN_LABEL;
	}

	public String getMaxUnsignedIntLabel() {
		return UINT_MAX_LABEL;
	}

	public String getMinLongLabel() {
		return LONG_MIN_LABEL;
	}

	public String getMaxLongLabel() {
		return LONG_MAX_LABEL;
	}

	public String getMinUnsignedLongLabel() {
		return ULONG_MIN_LABEL;
	}

	public String getMaxUnsignedLongLabel() {
		return ULONG_MAX_LABEL;
	}
}
