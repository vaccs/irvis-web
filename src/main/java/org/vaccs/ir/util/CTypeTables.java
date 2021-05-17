/**
 *
 */
package org.vaccs.ir.util;

/**
 * @author carr
 *
 */
public final class CTypeTables {

	// constants for the C types, boths signed and unsigned

	public final static int CBYTE = 0; // C char type
	public final static int CUBYTE = 1; // C unsigned char type
	public final static int CSHORT = 2; // C short type
	public final static int CUSHORT = 3; // C unsigned short type
	public final static int CINT = 4; // C int type
	public final static int CUINT = 5; // C unsigned int type
	public final static int CLONG = 6; // C long type
	public final static int CULONG = 7; // C unsigned long type
	public final static int ANYTYPE = 8; // Any type
	public final static int NOTYPE = 9; // No type
	public final static int NUMTYPES = 10; // The # of types

	public final static short NO_RULE = -1;
	public final static short PROMOTION = 0;
	public final static short BOTH_SIGNEDNESS_SAME = 1;
	public final static short RANK_UNSIGNED_GREATER = 2;
	public final static short SIGNED_ALL = 3;
	public final static short UNSIGNED_ALL = 4;

	public final static int rank[] = { 0, 0, 1, 1, 2, 2, 3, 3 };

	public final static String typeName[] = { "char", "unsigned char", "short", "unsigned short", "int", "unsigned int",
			"long", "unsigned long", "any", "none" };

	// The C type coercion table for expressions
	private final static int[][] expressionTable = {
			{ CBYTE, CUBYTE, CSHORT, CUSHORT, CINT, CUINT, CLONG, CULONG, CBYTE, NOTYPE }, // CBYTE
			{ CUBYTE, CUBYTE, CSHORT, CUSHORT, CINT, CUINT, CLONG, CULONG, CUBYTE, NOTYPE }, // CUBYTE
			{ CSHORT, CSHORT, CSHORT, CUSHORT, CINT, CUINT, CLONG, CULONG, CSHORT, NOTYPE }, // CSHORT
			{ CUSHORT, CUSHORT, CUSHORT, CUSHORT, CINT, CUINT, CLONG, CULONG, CUSHORT, NOTYPE }, // CUSHORT
			{ CINT, CINT, CINT, CINT, CINT, CUINT, CLONG, CULONG, CINT, NOTYPE }, // CINT
			{ CUINT, CUINT, CUINT, CUINT, CUINT, CUINT, CLONG, CULONG, CUINT, NOTYPE }, // CUINT
			{ CLONG, CLONG, CLONG, CLONG, CLONG, CLONG, CLONG, CULONG, CLONG, NOTYPE }, // CLONG
			{ CULONG, CULONG, CULONG, CULONG, CULONG, CULONG, CULONG, CULONG, CULONG, NOTYPE }, // CULONG
			{ CBYTE, CUBYTE, CSHORT, CUSHORT, CINT, CUINT, CLONG, CULONG, ANYTYPE, NOTYPE }, // ANYTYPE
			{ NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE, NOTYPE } }; // NOTYPE

	public static int getResultingType(int leftType, int rightType) {
		return expressionTable[leftType][rightType];
	}

	public static int convertToUnsignedType(int signedType) {
		switch (signedType) {
			case CBYTE:
			case CUBYTE:
				return CUBYTE;
			case CSHORT:
			case CUSHORT:
				return CUSHORT;
			case CINT:
			case CUINT:
				return CUINT;
			case CLONG:
			case CULONG:
				return CULONG;
			case ANYTYPE:
				return ANYTYPE;
			default:
				return NOTYPE;
		}
	}

	public static int convertToSignedType(int unsignedType) {
		switch (unsignedType) {
			case CBYTE:
			case CUBYTE:
				return CBYTE;
			case CSHORT:
			case CUSHORT:
				return CSHORT;
			case CINT:
			case CUINT:
				return CINT;
			case CLONG:
			case CULONG:
				return CLONG;
			case ANYTYPE:
				return ANYTYPE;
			default:
				return NOTYPE;
		}
	}

	public static boolean isSigned(int type) {
		return (type == CBYTE || type == CSHORT || type == CINT || type == CLONG);
	}

	public static boolean isUnsigned(int type) {
		return (type == CUBYTE || type == CUSHORT || type == CUINT || type == CULONG || type == ANYTYPE);
	}

	public static int getPromotedType(int type) {
		if (rank[type] < rank[CINT])
			return CINT;
		else
			return type;
	}

	public static int size(ArchitectureType arch, int type) {
		switch (type) {
			case CBYTE:
			case CUBYTE:
				return arch.byteSize();
			case CSHORT:
			case CUSHORT:
				return arch.shortSize();
			case CINT:
			case CUINT:
				return arch.intSize();
			case CLONG:
			case CULONG:
			case ANYTYPE:
				return arch.longSize();
			default:
				return 0;
		}
	}

	public static int getConvertedType(ArchitectureType arch, int lType, int rType) {
		if (lType == rType)
			return lType;
		else if ((isSigned(lType) && isSigned(rType)) || (isUnsigned(lType) && isUnsigned(rType)))
			return rank[lType] > rank[rType] ? lType : rType;
		else if (isUnsigned(lType) && rank[lType] >= rank[rType])
			return lType;
		else if (isUnsigned(rType) && rank[rType] >= rank[lType])
			return rType;
		else if (isSigned(lType) && size(arch, lType) > size(arch, rType))
			return lType;
		else if (isSigned(rType) && size(arch, rType) > size(arch, lType))
			return rType;
		else if (isSigned(lType))
			return convertToUnsignedType(lType);
		else
			return convertToUnsignedType(rType);

	}

	public static short getConversionRule(ArchitectureType arch, int lType, int rType) {
		if (lType == rType)
			return NO_RULE;
		else if ((isSigned(lType) && isSigned(rType)) || (isUnsigned(lType) && isUnsigned(rType)))
			return BOTH_SIGNEDNESS_SAME;
		else if (isUnsigned(lType) && rank[lType] >= rank[rType])
			return RANK_UNSIGNED_GREATER;
		else if (isUnsigned(rType) && rank[rType] >= rank[lType])
			return RANK_UNSIGNED_GREATER;
		else if (isSigned(lType) && size(arch, lType) > size(arch, rType))
			return SIGNED_ALL;
		else if (isSigned(rType) && size(arch, rType) > size(arch, lType))
			return SIGNED_ALL;
		else if (isSigned(lType))
			return UNSIGNED_ALL;
		else
			return UNSIGNED_ALL;

	}
}
