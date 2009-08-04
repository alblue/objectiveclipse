package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IProblem;

/**
 * Interface between the lexer and the preprocessor for picking up warnings and
 * comments.
 * 
 * @since 5.0
 */
public interface ILexerLog {
    ILexerLog NULL = new ILexerLog() {
        public void handleComment(boolean isBlockComment, int offset, int endOffset) {
        }

        public void handleProblem(int problemID, char[] info, int offset, int endOffset) {
        }
    };

    /**
     * A comment has been detected
     * 
     * @param isBlockComment
     *            <code>true</code> for block-comments, <code>false</code> for
     *            line-comments.
     * @param source
     *            the input of the lexer.
     * @param offset
     *            the offset where the comment starts
     * @param endOffset
     *            the offset where the comment ends
     */
    void handleComment(boolean isBlockComment, int offset, int endOffset);

    /**
     * A problem has been detected
     * 
     * @param problemID
     *            id as defined in {@link IProblem}
     * @param info
     *            additional info as required for {@link IProblem}.
     * @param offset
     *            The offset of the problem in the source of the lexer.
     * @param endOffset
     *            end offset of the problem in the source of the lexer.
     */
    void handleProblem(int problemID, char[] info, int offset, int endOffset);
}
