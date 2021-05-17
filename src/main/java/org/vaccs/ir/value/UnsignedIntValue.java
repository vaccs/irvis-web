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
public class UnsignedIntValue extends CValue {

	private long value;
	static final long bitmask = 0xffffffffL;

	/**
	 * 
	 */
	public UnsignedIntValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Long.valueOf(value & bitmask);
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value & bitmask;
	}

	public UnsignedIntValue addValue(long val) {
		value = val & bitmask;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
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
		return (int) value;
	}

	@Override
	public long getLongValue() {
		return value;
	}

	@Override
	public int getUnsignedByteValue() {
		return (int) (value & UnsignedIntValue.bitmask);
	}

	@Override
	public int getUnsignedShortValue() {
		return (int) (value & UnsignedShortValue.bitmask);
	}

	@Override
	public long getUnsignedIntValue() {
		return value;
	}

	@Override
	public long getUnsignedLongValue() {
		return value;
	}

	@Override
	public String getType() {
		return CTypeTables.typeName[CTypeTables.CUINT];
	}

	@Override
	public boolean isSigned() {
		return false;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxUnsignedIntValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(BigInteger.ZERO) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type unsigned int.");
			return addValue(0);
		}
		return addValue(Long.parseLong(value));
	}

	@Override
	public String toString() {
		return Long.toUnsignedString(getValue());
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
		return aType.intSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Long.toHexString(value));
		int size = aType.intSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}

}
