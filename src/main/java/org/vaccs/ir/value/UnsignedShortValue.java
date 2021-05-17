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
public class UnsignedShortValue extends CValue {

	private int value;
	static final int bitmask = 0xffff;

	/**
	 * 
	 */
	public UnsignedShortValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Integer.valueOf(value & bitmask);
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value & bitmask;
	}

	public UnsignedShortValue addValue(int val) {
		value = val & bitmask;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value & bitmask;
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
		return value;
	}

	@Override
	public long getLongValue() {
		return value;
	}

	@Override
	public int getUnsignedByteValue() {
		return value & UnsignedCharValue.bitmask;
	}

	@Override
	public int getUnsignedShortValue() {
		return value & UnsignedShortValue.bitmask;
	}

	@Override
	public long getUnsignedIntValue() {
		return (long) value & UnsignedIntValue.bitmask;
	}

	@Override
	public long getUnsignedLongValue() {
		return value;
	}

	@Override
	public String getType() {
		return CTypeTables.typeName[CTypeTables.CUSHORT];
	}

	@Override
	public boolean isSigned() {
		return false;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxUnsignedShortValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(BigInteger.ZERO) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type unsigned short.");
			return addValue(0);
		}
		return addValue(Integer.parseInt(value));
	}

	@Override
	public String toString() {
		return Integer.toUnsignedString(getValue());
	}

	@Override
	public void increment() {
		setValue(getValue() + 1);
	}

	@Override
	public void decrement() {
		setValue(getValue() - 1);
	}

	@Override
	public int numRepresentationBits() {
		return aType.shortSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Integer.toHexString(value));
		int size = aType.shortSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}
}
