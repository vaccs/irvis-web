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
public class CharValue extends CValue {

	private byte value;

	/**
	 * 
	 */
	public CharValue() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Byte.valueOf(value);
	}

	/**
	 * @return the value
	 */
	public byte getValue() {
		return value;
	}

	public CharValue addValue(byte val) {
		value = val;
		return this;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(byte value) {
		this.value = value;
	}

	@Override
	public byte getByteValue() {
		return value;
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
		return CTypeTables.typeName[CTypeTables.CBYTE];
	}

	@Override
	public boolean isSigned() {
		return value < 0;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxCharValue());
		BigInteger minVal = new BigInteger(aType.getMinCharValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(minVal) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type char.");
			return addValue((byte) 0);
		}
		return addValue(Byte.parseByte(value));
	}

	@Override
	public String toString() {
		return Byte.toString(value);
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
		return aType.byteSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Integer.toHexString(value));
		int size = aType.byteSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}
}
