/**
 * 
 */
package org.vaccs.ir.value;

/**
 * @author carr
 *
 */
public class CValueFactory {

	/**
	 * 
	 */
	public CValueFactory() {
	}

	public CValue makeCValue(String type) {
		switch (type) {
			case CValue.CHAR:
				return new CharValue();
			case CValue.INT:
				return new IntValue();
			case CValue.LONG:
				return new LongValue();
			case CValue.SHORT:
				return new ShortValue();
			case CValue.UCHAR:
				return new UnsignedCharValue();
			case CValue.UINT:
				return new UnsignedIntValue();
			case CValue.ULONG:
				return new UnsignedLongValue();
			case CValue.USHORT:
				return new UnsignedShortValue();
			default:
				return null;
		}
	}

}
