package org.eclipse.cdt.objc.core.internal.parser.scanner;

/**
 * Tokens for digraphs simply have a different image.
 * 
 * @since 5.0
 */
public class TokenForDigraph extends Token {
    public TokenForDigraph(int kind, Object source, int offset, int endOffset) {
        super(kind, source, offset, endOffset);
    }

    @Override
    public char[] getCharImage() {
        return TokenUtil.getDigraphImage(getType());
    }
}
