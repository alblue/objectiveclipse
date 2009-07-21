package org.eclipse.cdt.objc.core.internal.parser.scanner;

public class TokenWithImage extends Token {
    private final char[] fImage;

    public TokenWithImage(int kind, Object source, int offset, int endOffset, char[] image) {
        super(kind, source, offset, endOffset);
        fImage = image;
    }

    @Override
    public char[] getCharImage() {
        return fImage;
    }
}
