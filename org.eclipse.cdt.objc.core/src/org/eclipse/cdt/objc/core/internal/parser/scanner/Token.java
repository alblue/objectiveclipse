package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IToken;

/**
 * Represents tokens found by the lexer. The preprocessor reuses the tokens and
 * passes them on to the parsers.
 * 
 * @since 5.0
 */
public class Token implements IToken, Cloneable {
    private int fEndOffset;
    private int fKind;
    private IToken fNextToken;
    private int fOffset;
    Object fSource;

    Token(int kind, Object source, int offset, int endOffset) {
        fKind = kind;
        fOffset = offset;
        fEndOffset = endOffset;
        fSource = source;
    }

    @Override
    final public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public char[] getCharImage() {
        return TokenUtil.getImage(getType());
    }

    final public int getEndOffset() {
        return fEndOffset;
    }

    public String getImage() {
        return new String(getCharImage());
    }

    final public int getLength() {
        return fEndOffset - fOffset;
    }

    final public IToken getNext() {
        return fNextToken;
    }

    final public int getOffset() {
        return fOffset;
    }

    final public int getType() {
        return fKind;
    }

    final public boolean isOperator() {
        return TokenUtil.isOperator(fKind);
    }

    final public void setNext(IToken t) {
        fNextToken = t;
    }

    public void setOffset(int offset, int endOffset) {
        fOffset = offset;
        fEndOffset = endOffset;
    }

    final public void setType(int kind) {
        fKind = kind;
    }

    public void shiftOffset(int shift) {
        fOffset += shift;
        fEndOffset += shift;
    }

    @Override
    public String toString() {
        return getImage();
    }
}
