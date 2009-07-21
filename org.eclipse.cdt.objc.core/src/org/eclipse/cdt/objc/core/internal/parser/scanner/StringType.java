package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IToken;

/**
 * Utility class that provides some simple operations for string literals.
 */
@SuppressWarnings("nls")
public enum StringType {

    // listed in order of "wideness"
    NARROW("", IToken.tSTRING), UTF16("u", IToken.tUTF16STRING), UTF32("U", IToken.tUTF32STRING), WIDE("L",
            IToken.tLSTRING);

    /**
     * Returns the StringType value for the given string literal type.
     * 
     * @see IToken#tSTRING
     * @see IToken#tLSTRING
     * @see IToken#tUTF16STRING
     * @see IToken#tUTF32STRING
     * 
     * @throws IllegalArgumentException
     *             if the tokenVal does not represent a string literal
     */
    public static StringType fromToken(int tokenVal) {
        switch (tokenVal) {
            case IToken.tSTRING:
                return NARROW;
            case IToken.tLSTRING:
                return WIDE;
            case IToken.tUTF16STRING:
                return UTF16;
            case IToken.tUTF32STRING:
                return UTF32;
            default:
                throw new IllegalArgumentException(tokenVal + " is not a string token");
        }
    }

    /**
     * Returns the StringType value that represesnts the 'wider' of the two
     * given StringTypes.
     * 
     * @thows NullPointerException if an argument is null
     */
    public static StringType max(StringType st1, StringType st2) {
        return values()[Math.max(st1.ordinal(), st2.ordinal())];
    }

    private char[] prefix;

    private int tokenVal;

    private StringType(String prefix, int tokenVal) {
        this.prefix = prefix.toCharArray();
        this.tokenVal = tokenVal;
    }

    public char[] getPrefix() {
        return prefix;
    }

    public int getTokenValue() {
        return tokenVal;
    }
}
