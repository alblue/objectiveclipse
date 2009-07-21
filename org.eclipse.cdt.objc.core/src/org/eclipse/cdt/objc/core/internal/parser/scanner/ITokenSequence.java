package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.OffsetLimitReachedException;

/**
 * A token sequence serves as input to the macro expansion.
 */
interface ITokenSequence {
    /**
     * Returns the current token
     */
    Token currentToken();

    /**
     * Returns the offset of the last token consumed.
     */
    int getLastEndOffset();

    /**
     * Consumes the current token and returns the next one.
     */
    Token nextToken() throws OffsetLimitReachedException;
}