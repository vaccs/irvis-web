/**
 * 
 */
package org.vaccs.ir.value;

import org.vaccs.ir.util.ArchitectureType;
import org.vaccs.ir.util.BIT32Architecture;
import org.vaccs.ir.util.BIT64Architecture;
import com.jpro.ir.UIUtils;

/**
 * @author carr
 *
 */
public abstract class CValue {

	public static final String CHAR = "char";
	public static final String SHORT = "short";
	public static final String INT = "int";
	public static final String LONG = "long";
	public static final String UCHAR = "unsigned char";
	public static final String USHORT = "unsigned short";
	public static final String UINT = "unsigned int";
	public static final String ULONG = "unsigned long";

	protected boolean formatError = false;
	protected String errorMessage = "";

	protected static CValueFactory cvFactory = new CValueFactory();
	protected ArchitectureType aType = UIUtils.architecture;
	protected String endian = ArchitectureType.BIG_ENDIAN;

	public CValue addArchitecture(int arch) {
		if (arch == ArchitectureType.BIT32)
			aType = new BIT32Architecture();
		else
			aType = new BIT64Architecture();
		return this;
	}

	public CValue addEndian(String endianType) {
		endian = endianType;
		return this;
	}

	public ArchitectureType getArchitectureType() {
		return aType;
	}

	public String getEndianType() {
		return endian;
	}

	public abstract boolean isSigned();

	protected String extendHexString(String hexVal) {
		String value, extChar;
		if (isSigned())
			extChar = "f";
		else
			extChar = "0";
		int strLen = numRepresentationBits() >> 2; // 4 bits per hex digit
		value = new String(hexVal);
		while (value.length() < strLen) {
			value = extChar + value;
		}
		return value;
	}

	private String swapEndian(String val) {
		String newVal = "";
		for (int i = val.length() - 2; i >= 0; i -= 2) {
			newVal += val.substring(i, i + 2);
		}
		return newVal;
	}

	public abstract String getTypeSizeHexValue();

	public String getHexValue() {
		String sval = getTypeSizeHexValue();
		if (endian == ArchitectureType.BIG_ENDIAN)
			return sval;
		else
			return swapEndian(sval);
	}

	public String getHexValueWithError() {
		if (hasFormatError())
			return getErrorMessage();
		else
			return getHexValue();
	}

	public abstract CValue addValue(String value);

	public CValue addValue(CValue value, String type) {
		addValue(value.toString(), type);
		if (value.hasFormatError()) {
			setFormatError();
			setErrorMessage("", value.getErrorMessage());
		}
		return this;
	}

	public CValue addValue(String value, String type) {
		switch (type) {
			case CHAR:
				return ((CharValue) this).addValue(value);
			case INT:
				return ((IntValue) this).addValue(value);
			case LONG:
				return ((LongValue) this).addValue(value);
			case SHORT:
				return ((ShortValue) this).addValue(value);
			case UCHAR:
				return ((UnsignedCharValue) this).addValue(value);
			case UINT:
				return ((UnsignedIntValue) this).addValue(value);
			case ULONG:
				return ((UnsignedLongValue) this).addValue(value);
			case USHORT:
				return ((UnsignedShortValue) this).addValue(value);
		}
		return this;
	}

	public void clearFormatError() {
		formatError = false;
	}

	public void setFormatError() {
		formatError = true;
	}

	public void setErrorMessage(String prefix, String msg) {
		errorMessage = prefix + ": " + msg;
	}

	public boolean hasFormatError() {
		return formatError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public abstract int numRepresentationBits();

	public abstract String getType();

	public abstract Number getJavaValue();

	public abstract byte getByteValue();

	public abstract short getShortValue();

	public abstract int getIntValue();

	public abstract long getLongValue();

	public abstract int getUnsignedByteValue();

	public abstract int getUnsignedShortValue();

	public abstract long getUnsignedIntValue();

	public abstract long getUnsignedLongValue();

	public abstract void increment();

	public abstract void decrement();

	public String toStringWithError() {
		if (formatError)
			return errorMessage;
		else
			return toString();
	}
}
