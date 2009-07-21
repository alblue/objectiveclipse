package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IInactiveCodeToken;

/**
 * Special token to separate active from inactive code
 */
public class InactiveCodeToken extends Token implements IInactiveCodeToken {
    private final int fNewNesting;
    private final int fOldNesting;

    InactiveCodeToken(int kind, int oldNesting, int newNesting, int offset) {
        super(kind, null, offset, offset);
        fOldNesting = oldNesting;
        fNewNesting = newNesting;
    }

    public int getNewNesting() {
        return fNewNesting;
    }

    public int getOldNesting() {
        return fOldNesting;
    }
}
