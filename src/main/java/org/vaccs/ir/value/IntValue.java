/**
 * 
 */
package org.vaccs.ir.value;

import java.math.BigInteger;

import org.vaccs.ir.util.CTypeTables;

/**
 * @author carr
 *
 */
public class IntValue extends CValue {

	private int value;

	/**
	 * 
	 */
	public IntValue() {
	}

	public IntValue addValue(int value) {
		this.value = value;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.CValue#getCValue()
	 */
	@Override
	public Number getJavaValue() {
		return Integer.valueOf(value);
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
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
		return CTypeTables.typeName[CTypeTables.CINT];

	}

	@Override
	public boolean isSigned() {
		return value < 0;
	}

	@Override
	public CValue addValue(String value) {
		BigInteger bVal = new BigInteger(value);
		BigInteger maxVal = new BigInteger(aType.getMaxIntValue());
		BigInteger minVal = new BigInteger(aType.getMinIntValue());
		if (bVal.compareTo(maxVal) == 1 || bVal.compareTo(minVal) == -1) {
			setFormatError();
			setErrorMessage("IRVis:", "Value " + value + " is not in the valid range of type int.");
			return addValue(0);
		}
		return addValue(Integer.parseInt(value));
	}

	@Override
	public String toString() {
		return Integer.toString(value);
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
		return aType.intSize() << 3;
	}

	@Override
	public String getTypeSizeHexValue() {
		String hexVal = extendHexString(Integer.toHexString(value));
		int size = aType.intSize() << 1;
		return hexVal.substring(hexVal.length() - size);
	}

}
