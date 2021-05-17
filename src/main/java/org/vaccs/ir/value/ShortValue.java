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
public class ShortValue extends CValue {

	private short value;

	/**
	 * 
	 */
	public ShortValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Short.valueOf(value);
	}

	/**
	 * @return the value
	 */
	public short getValue() {
		return value;
	}

	public ShortValue addValue(short val) {
		value = val;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(short value) {
		this.value = value;
	}

	@Override
	public byte getByteValue() {
		return (byte) value;
	}

	@Override
	public short getShortValue() {
		return value;
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
		return (int) value & UnsignedCharValue.bitmask;
	}

	@Override
	public int getUnsignedShortValue() {
		return (int) value & UnsignedShortValue.bitmask;
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
		return CTypeTables.typeName[CTypeTables.CSHORT];
	}

	@Override
	public boolean isSigned() {
		return value < 0;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxShortValue());
		BigInteger minVal = new BigInteger(aType.getMinShortValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(minVal) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type short.");
			return addValue((short) 0);
		}
		return addValue(Short.parseShort(value));
	}

	@Override
	public String toString() {
		return Short.toString(value);
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
		return aType.shortSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Integer.toHexString(value));
		int size = aType.shortSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}

}
