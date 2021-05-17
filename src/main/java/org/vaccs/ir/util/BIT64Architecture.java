/**
 * 
 */
package org.vaccs.ir.util;

/**
 * @author carr
 *
 */
public class BIT64Architecture extends ArchitectureType {

	public static String BIT64_LONG_MIN_VALUE = "-9223372036854775808";
	public static String BIT64_LONG_MAX_VALUE = "9223372036854775807";
	public static String BIT64_ULONG_MIN_VALUE = "0";
	public static String BIT64_ULONG_MAX_VALUE = "18446744073709551615";
	public static String BIT64_LONG_MIN_LABEL = "0x8000000000000000=-2^(64-1)";
	public static String BIT64_LONG_MAX_LABEL = "0x7FFFFFFFFFFFFFFF=2^(64-1)-1";
	public static String BIT64_ULONG_MIN_LABEL = "0x0000000000000000=0";
	public static String BIT64_ULONG_MAX_LABEL = "0xFFFFFFFFFFFFFFFF=2^64";

	public BIT64Architecture() {
		super(BIT64_LONG_MIN_VALUE, BIT64_LONG_MAX_VALUE, BIT64_ULONG_MIN_VALUE, BIT64_ULONG_MAX_VALUE,
				BIT64_LONG_MIN_LABEL, BIT64_LONG_MAX_LABEL, BIT64_ULONG_MIN_LABEL, BIT64_ULONG_MAX_LABEL);
	}

	@Override
	public int longSize() {
		return 8;
	}

}
