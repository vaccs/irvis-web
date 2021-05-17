package org.vaccs.ir.value;

import java.math.BigInteger;

import org.vaccs.ir.util.CTypeTables;
import org.vaccs.ir.value.CValue;

/**
 * 
 */

/**
 * @author carr
 *
 */
public class UnsignedLongValue extends CValue {

	private long value;

	/**
	 * 
	 */
	public UnsignedLongValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return new BigInteger(Long.toUnsignedString(value));
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	public UnsignedLongValue addValue(long val) {
		value = val;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		this.value = value;
	}

	public String toString() {
		return Long.toUnsignedString(getValue());
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
		return CTypeTables.typeName[CTypeTables.CULONG];
	}

	@Override
	public boolean isSigned() {
		return false;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxUnsignedLongValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(BigInteger.ZERO) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type unsigned long.");
			return addValue(0);
		}
		return addValue(Long.parseUnsignedLong(value));
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
		return aType.longSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Long.toHexString(value));
		int size = aType.longSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}
}
