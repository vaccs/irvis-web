package org.vaccs.ir.value;

import java.math.BigInteger;

import util.CTypeTables;
import value.CValue;

/**
 * 
 */

/**
 * @author carr
 *
 */
public class LongValue extends CValue {

	private long value;

	/**
	 * 
	 */
	public LongValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Long.valueOf(value);
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	public LongValue addValue(long val) {
		value = val;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public byte getByteValue() {
		return (byte) value;
	}

	@Override
	public short getShortValue() {
		return (short) value;
	}

	@Override
	public int getIntValue() {
		return (int) value;
	}

	@Override
	public long getLongValue() {
		return value;
	}

	@Override
	public int getUnsignedByteValue() {
		return (int) value & UnsignedCharValue.bitmask;
	}

	@Override
	public int getUnsignedShortValue() {
		return (int) value & UnsignedShortValue.bitmask;
	}

	@Override
	public long getUnsignedIntValue() {
		return value & UnsignedIntValue.bitmask;
	}

	@Override
	public long getUnsignedLongValue() {
		return value;
	}

	@Override
	public String getType() {
		return CTypeTables.typeName[CTypeTables.CLONG];
	}

	@Override
	public boolean isSigned() {
		return value < 0;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxLongValue());
		BigInteger minVal = new BigInteger(aType.getMinLongValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(minVal) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type long.");
			return addValue(0);
		} else
			return addValue(Long.parseLong(value));
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public void increment() {
		value++;
	}

	@Override
	public void decrement() {
		value--;
	}

	@Override
	public int numRepresentationBits() {
		return aType.longSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Long.toHexString(value));
		int size = aType.longSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}
}
