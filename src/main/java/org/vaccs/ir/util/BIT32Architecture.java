/**
 * 
 */
package org.vaccs.ir.util;

/**
 * @author carr
 *
 */
public class BIT32Architecture extends ArchitectureType {

	public static String BIT32_LONG_MIN_VALUE = "-2147483648";
	public static String BIT32_LONG_MAX_VALUE = "2147483647";
	public static String BIT32_ULONG_MIN_VALUE = "0";
	public static String BIT32_ULONG_MAX_VALUE = "4294967295";
	public static String BIT32_LONG_MIN_LABEL = "0x80000000=-2^(32-1)";
	public static String BIT32_LONG_MAX_LABEL = "0x7FFFFFFF=2^(32-1)-1";
	public static String BIT32_ULONG_MIN_LABEL = "0x00000000=0";
	public static String BIT32_ULONG_MAX_LABEL = "0xFFFFFFFF=2^32";

	public BIT32Architecture() {
		super(BIT32_LONG_MIN_VALUE, BIT32_LONG_MAX_VALUE, BIT32_ULONG_MIN_VALUE, BIT32_ULONG_MAX_VALUE,
				BIT32_LONG_MIN_LABEL, BIT32_LONG_MAX_LABEL, BIT32_ULONG_MIN_LABEL, BIT32_ULONG_MAX_LABEL);
	}

	@Override
	public int longSize() {
		return 4;
	}

}
