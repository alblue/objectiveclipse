package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IGCCToken;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer.LexerOptions;

/**
 * In short this class converts line endings (to '\n') and trigraphs (to their
 * corresponding character), removes line-splices, comments and whitespace other
 * than newline. Returns preprocessor tokens.
 * <p>
 * In addition to the preprocessor tokens the following tokens may also be
 * returned: {@link #tBEFORE_INPUT}, {@link IToken#tEND_OF_INPUT},
 * {@link IToken#tCOMPLETION}.
 * <p>
 * Number literals are split up into {@link IToken#tINTEGER} and
 * {@link IToken#tFLOATINGPT}. No checks are done on the number literals.
 * <p>
 * UNCs are accepted, however characters from outside of the basic source
 * character set are not converted to UNCs. Rather than that they are tested
 * with {@link Character#isUnicodeIdentifierPart(char)} and may be accepted as
 * part of an identifier.
 * <p>
 * The characters in string literals and char-literals are left as they are
 * found, no conversion to an execution character-set is performed.
 */
final public class Lexer implements ITokenSequence {

    private static final int END_OF_INPUT = -1;
    private static final int ORIGIN_LEXER = OffsetLimitReachedException.ORIGIN_LEXER;
    public static final int tBEFORE_INPUT = IToken.FIRST_RESERVED_SCANNER;
    public static final int tNEWLINE = IToken.FIRST_RESERVED_SCANNER + 1;

    public static final int tOTHER_CHARACTER = IToken.FIRST_RESERVED_SCANNER + 4;
    public static final int tQUOTE_HEADER_NAME = IToken.FIRST_RESERVED_SCANNER + 2;

    public static final int tSYSTEM_HEADER_NAME = IToken.FIRST_RESERVED_SCANNER + 3;

    private int fCharPhase3;
    private int fEndOffset;
    // the input to the lexer
    private final char[] fInput;
    private boolean fInsideIncludeDirective = false;

    private Token fLastToken;
    private int fLimit;
    private final ILexerLog fLog;

    private int fMarkEndOffset;
    // for the few cases where we have to lookahead more than one character
    private int fMarkOffset;
    private int fMarkPrefetchedChar;

    // after phase 3 (newline, trigraph, line-splice)
    private int fOffset;
    // configuration
    private final LexerOptions fOptions;
    private final Object fSource;

    private final int fStart;
    private boolean fSupportContentAssist = false;
    private Token fToken;

    public Lexer(char[] input, int start, int end, LexerOptions options, ILexerLog log, Object source) {
        fInput = input;
        fStart = fOffset = fEndOffset = start;
        fLimit = end;
        fOptions = options;
        fLog = log;
        fSource = source;
        fLastToken = fToken = new Token(tBEFORE_INPUT, source, start, start);
        nextCharPhase3();
    }

    public Lexer(char[] input, LexerOptions options, ILexerLog log, Object source) {
        this(input, 0, input.length, options, log, source);
    }

    private void blockComment(final int start, final char trigger) {
        // we can ignore line-splices, trigraphs and windows newlines when
        // searching for the '*'
        int pos = fEndOffset;
        while (pos < fLimit) {
            if (fInput[pos++] == trigger) {
                fEndOffset = pos;
                if (nextCharPhase3() == '/') {
                    nextCharPhase3();
                    fLog.handleComment(true, start, fOffset);
                    return;
                }
            }
        }
        fCharPhase3 = END_OF_INPUT;
        fOffset = fEndOffset = pos;
        fLog.handleComment(true, start, pos);
    }

    @SuppressWarnings("fallthrough")
    private Token charLiteral(final int start, final int tokenType) throws OffsetLimitReachedException {
        boolean escaped = false;
        boolean done = false;
        int length = tokenType == IToken.tCHAR ? 1 : 2;
        int c = fCharPhase3;

        loop: while (!done) {
            switch (c) {
                case END_OF_INPUT:
                    if (fSupportContentAssist) {
                        throw new OffsetLimitReachedException(ORIGIN_LEXER,
                                newToken(tokenType, start, length));
                    }
                    // no break;
                case '\n':
                    handleProblem(IProblem.SCANNER_BAD_CHARACTER, getInputChars(start, fOffset), start);
                    break loop;
                case '\\':
                    escaped = !escaped;
                    break;
                case '\'':
                    if (!escaped) {
                        done = true;
                    }
                    escaped = false;
                    break;
                default:
                    escaped = false;
                    break;
            }
            length++;
            c = nextCharPhase3();
        }
        return newToken(tokenType, start, length);
    }

    /**
     * Maps a trigraph to the character it encodes.
     * 
     * @param c
     *            trigraph without leading question marks.
     * @return the character encoded or 0.
     */
    private char checkTrigraph(char c) {
        switch (c) {
            case '=':
                return '#';
            case '\'':
                return '^';
            case '(':
                return '[';
            case ')':
                return ']';
            case '!':
                return '|';
            case '<':
                return '{';
            case '>':
                return '}';
            case '-':
                return '~';
            case '/':
                return '\\';
        }
        return 0;
    }

    /**
     * Advances to the next newline or the end of input. The newline will not be
     * consumed. If the current token is a newline no action is performed.
     * Returns the end offset of the last token before the newline.
     * 
     * @param origin
     *            parameter for the {@link OffsetLimitReachedException} when it
     *            has to be thrown.
     * @since 5.0
     */
    @SuppressWarnings("fallthrough")
    public final int consumeLine(int origin) throws OffsetLimitReachedException {
        Token t = fToken;
        Token lt = null;
        while (true) {
            switch (t.getType()) {
                case IToken.tCOMPLETION:
                    if (lt != null) {
                        fLastToken = lt;
                    }
                    fToken = t;
                    throw new OffsetLimitReachedException(origin, t);
                case IToken.tEND_OF_INPUT:
                    if (fSupportContentAssist) {
                        t.setType(IToken.tCOMPLETION);
                        throw new OffsetLimitReachedException(origin, t);
                    }
                    // no break;
                case Lexer.tNEWLINE:
                    fToken = t;
                    if (lt != null) {
                        fLastToken = lt;
                    }
                    return getLastEndOffset();
            }
            lt = t;
            t = fetchToken();
        }
    }

    /**
     * Returns the current preprocessor token, does not advance.
     */
    public Token currentToken() {
        return fToken;
    }

    public boolean currentTokenIsFirstOnLine() {
        final int type = fLastToken.getType();
        return type == tNEWLINE || type == tBEFORE_INPUT;
    }

    /**
     * Computes the next token.
     */
    private Token fetchToken() throws OffsetLimitReachedException {
        while (true) {
            final int start = fOffset;
            final int c = fCharPhase3;
            final int d = nextCharPhase3();
            switch (c) {
                case END_OF_INPUT:
                    return newToken(IToken.tEND_OF_INPUT, start);
                case '\n':
                    fInsideIncludeDirective = false;
                    return newToken(Lexer.tNEWLINE, start);
                case ' ':
                case '\t':
                case 0xb: // vertical tab
                case '\f':
                case '\r':
                    continue;

                case 'L':
                    switch (d) {
                        case '"':
                            nextCharPhase3();
                            return stringLiteral(start, IToken.tLSTRING);
                        case '\'':
                            nextCharPhase3();
                            return charLiteral(start, IToken.tLCHAR);
                    }
                    return identifier(start, 1);

                case 'u':
                case 'U':
                    if (fOptions.fSupportUTFLiterals) {
                        if (d == '"') {
                            nextCharPhase3();
                            return stringLiteral(start, c == 'u' ? IToken.tUTF16STRING : IToken.tUTF32STRING);
                        }
                        if (d == '\'') {
                            nextCharPhase3();
                            return charLiteral(start, c == 'u' ? IToken.tUTF16CHAR : IToken.tUTF32CHAR);
                        }
                    }
                    return identifier(start, 1);

                case '"':
                    if (fInsideIncludeDirective) {
                        return headerName(start, true);
                    }
                    return stringLiteral(start, IToken.tSTRING);

                case '\'':
                    return charLiteral(start, IToken.tCHAR);

                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                    return identifier(start, 1);

                case '$':
                    if (fOptions.fSupportDollarInIdentifiers) {
                        return identifier(start, 1);
                    }
                    break;
                case '@':
                    if (fOptions.fSupportAtSignInIdentifiers) {
                        return identifier(start, 1);
                    }
                    break;

                case '\\':
                    switch (d) {
                        case 'u':
                        case 'U':
                            nextCharPhase3();
                            return identifier(start, 2);
                    }
                    return newToken(tOTHER_CHARACTER, start, 1);

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return number(start, 1, false);

                case '.':
                    switch (d) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            nextCharPhase3();
                            return number(start, 2, true);

                        case '.':
                            markPhase3();
                            if (nextCharPhase3() == '.') {
                                nextCharPhase3();
                                return newToken(IToken.tELLIPSIS, start);
                            }
                            restorePhase3();
                            break;

                        case '*':
                            nextCharPhase3();
                            return newToken(IToken.tDOTSTAR, start);
                    }
                    return newToken(IToken.tDOT, start);

                case '#':
                    if (d == '#') {
                        nextCharPhase3();
                        return newToken(IToken.tPOUNDPOUND, start);
                    }
                    return newToken(IToken.tPOUND, start);

                case '{':
                    return newToken(IToken.tLBRACE, start);
                case '}':
                    return newToken(IToken.tRBRACE, start);
                case '[':
                    return newToken(IToken.tLBRACKET, start);
                case ']':
                    return newToken(IToken.tRBRACKET, start);
                case '(':
                    return newToken(IToken.tLPAREN, start);
                case ')':
                    return newToken(IToken.tRPAREN, start);
                case ';':
                    return newToken(IToken.tSEMI, start);

                case ':':
                    switch (d) {
                        case ':':
                            nextCharPhase3();
                            return newToken(IToken.tCOLONCOLON, start);
                        case '>':
                            nextCharPhase3();
                            return newDigraphToken(IToken.tRBRACKET, start);
                    }
                    return newToken(IToken.tCOLON, start);

                case '?':
                    return newToken(IToken.tQUESTION, start);

                case '+':
                    switch (d) {
                        case '+':
                            nextCharPhase3();
                            return newToken(IToken.tINCR, start);
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tPLUSASSIGN, start);
                    }
                    return newToken(IToken.tPLUS, start);

                case '-':
                    switch (d) {
                        case '>':
                            int e = nextCharPhase3();
                            if (e == '*') {
                                nextCharPhase3();
                                return newToken(IToken.tARROWSTAR, start);
                            }
                            return newToken(IToken.tARROW, start);

                        case '-':
                            nextCharPhase3();
                            return newToken(IToken.tDECR, start);
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tMINUSASSIGN, start);
                    }
                    return newToken(IToken.tMINUS, start);

                case '*':
                    if (d == '=') {
                        nextCharPhase3();
                        return newToken(IToken.tSTARASSIGN, start);
                    }
                    return newToken(IToken.tSTAR, start);

                case '/':
                    switch (d) {
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tDIVASSIGN, start);
                        case '/':
                            nextCharPhase3();
                            lineComment(start);
                            continue;
                        case '*':
                            blockComment(start, '*');
                            continue;
                        case '%':
                            if (fOptions.fSupportSlashPercentComments) {
                                blockComment(start, '%');
                                continue;
                            }
                            break;
                    }
                    return newToken(IToken.tDIV, start);

                case '%':
                    switch (d) {
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tMODASSIGN, start);
                        case '>':
                            nextCharPhase3();
                            return newDigraphToken(IToken.tRBRACE, start);
                        case ':':
                            final int e = nextCharPhase3();
                            if (e == '%') {
                                markPhase3();
                                if (nextCharPhase3() == ':') {
                                    nextCharPhase3();
                                    return newDigraphToken(IToken.tPOUNDPOUND, start);
                                }
                                restorePhase3();
                            }
                            return newDigraphToken(IToken.tPOUND, start);
                    }
                    return newToken(IToken.tMOD, start);

                case '^':
                    if (d == '=') {
                        nextCharPhase3();
                        return newToken(IToken.tXORASSIGN, start);
                    }
                    return newToken(IToken.tXOR, start);

                case '&':
                    switch (d) {
                        case '&':
                            nextCharPhase3();
                            return newToken(IToken.tAND, start);
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tAMPERASSIGN, start);
                    }
                    return newToken(IToken.tAMPER, start);

                case '|':
                    switch (d) {
                        case '|':
                            nextCharPhase3();
                            return newToken(IToken.tOR, start);
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tBITORASSIGN, start);
                    }
                    return newToken(IToken.tBITOR, start);

                case '~':
                    return newToken(IToken.tBITCOMPLEMENT, start);

                case '!':
                    if (d == '=') {
                        nextCharPhase3();
                        return newToken(IToken.tNOTEQUAL, start);
                    }
                    return newToken(IToken.tNOT, start);

                case '=':
                    if (d == '=') {
                        nextCharPhase3();
                        return newToken(IToken.tEQUAL, start);
                    }
                    return newToken(IToken.tASSIGN, start);

                case '<':
                    if (fInsideIncludeDirective) {
                        return headerName(start, false);
                    }

                    switch (d) {
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tLTEQUAL, start);
                        case '<':
                            final int e = nextCharPhase3();
                            if (e == '=') {
                                nextCharPhase3();
                                return newToken(IToken.tSHIFTLASSIGN, start);
                            }
                            return newToken(IToken.tSHIFTL, start);
                        case '?':
                            if (fOptions.fSupportMinAndMax) {
                                nextCharPhase3();
                                return newToken(IGCCToken.tMIN, start);
                            }
                            break;
                        case ':':
                            nextCharPhase3();
                            return newDigraphToken(IToken.tLBRACKET, start);
                        case '%':
                            nextCharPhase3();
                            return newDigraphToken(IToken.tLBRACE, start);
                    }
                    return newToken(IToken.tLT, start);

                case '>':
                    switch (d) {
                        case '=':
                            nextCharPhase3();
                            return newToken(IToken.tGTEQUAL, start);
                        case '>':
                            final int e = nextCharPhase3();
                            if (e == '=') {
                                nextCharPhase3();
                                return newToken(IToken.tSHIFTRASSIGN, start);
                            }
                            return newToken(IToken.tSHIFTR, start);
                        case '?':
                            if (fOptions.fSupportMinAndMax) {
                                nextCharPhase3();
                                return newToken(IGCCToken.tMAX, start);
                            }
                            break;
                    }
                    return newToken(IToken.tGT, start);

                case ',':
                    return newToken(IToken.tCOMMA, start);

                default:
                    // in case we have some other letter to start an identifier
                    if (Character.isUnicodeIdentifierStart((char) c)) {
                        return identifier(start, 1);
                    }
                    break;
            }
            // handles for instance @
            return newToken(tOTHER_CHARACTER, start, 1);
        }
    }

    /**
     * Returns the endoffset for a line-splice sequence, or -1 if there is none.
     */
    @SuppressWarnings("fallthrough")
    private int findEndOfLineSpliceSequence(int pos) {
        boolean haveBackslash = true;
        int result = -1;
        loop: while (pos < fLimit) {
            switch (fInput[pos++]) {
                case '\n':
                    if (haveBackslash) {
                        result = pos;
                        haveBackslash = false;
                        continue loop;
                    }
                    return result;

                case '\r':
                case ' ':
                case '\f':
                case '\t':
                case 0xb: // vertical tab
                    if (haveBackslash) {
                        continue loop;
                    }
                    return result;

                case '?':
                    if (pos + 1 >= fLimit || fInput[pos] != '?' || fInput[++pos] != '/') {
                        return result;
                    }
                    // fall through to backslash handling

                case '\\':
                    if (!haveBackslash) {
                        haveBackslash = true;
                        continue loop;
                    }
                    return result;

                default:
                    return result;
            }
        }
        return result;
    }

    /**
     * Returns the image with trigraphs replaced and line-splices removed.
     */
    private char[] getCharImage(int offset, int endOffset, int imageLength) {
        final char[] result = new char[imageLength];
        markPhase3();
        fEndOffset = offset;
        for (int idx = 0; idx < imageLength; idx++) {
            result[idx] = (char) nextCharPhase3();
        }
        restorePhase3();
        return result;
    }

    char[] getInput() {
        return fInput;
    }

    /**
     * Returns the image from the input without any modification.
     */
    public char[] getInputChars(int offset, int endOffset) {
        final int length = endOffset - offset;
        final char[] result = new char[length];
        System.arraycopy(fInput, offset, result, 0, length);
        return result;
    }

    /**
     * Returns the endoffset of the token before the current one.
     */
    public int getLastEndOffset() {
        return fLastToken.getEndOffset();
    }

    /**
     * Returns the source that is attached to the tokens generated by this lexer
     */
    public Object getSource() {
        return fSource;
    }

    private void handleProblem(int problemID, char[] arg, int offset) {
        fLog.handleProblem(problemID, arg, offset, fOffset);
    }

    @SuppressWarnings("fallthrough")
    private Token headerName(final int start, final boolean expectQuotes) throws OffsetLimitReachedException {
        int length = 1;
        boolean done = false;
        int c = fCharPhase3;
        loop: while (!done) {
            switch (c) {
                case END_OF_INPUT:
                    if (fSupportContentAssist) {
                        throw new OffsetLimitReachedException(ORIGIN_LEXER, newToken(
                                (expectQuotes ? tQUOTE_HEADER_NAME : tSYSTEM_HEADER_NAME), start, length));
                    }
                    // no break;
                case '\n':
                    handleProblem(IProblem.SCANNER_UNBOUNDED_STRING, getInputChars(start, fOffset), start);
                    break loop;

                case '"':
                    done = expectQuotes;
                    break;
                case '>':
                    done = !expectQuotes;
                    break;
            }
            length++;
            c = nextCharPhase3();
        }
        return newToken((expectQuotes ? tQUOTE_HEADER_NAME : tSYSTEM_HEADER_NAME), start, length);
    }

    private Token identifier(int start, int length) {
        int tokenKind = IToken.tIDENTIFIER;
        boolean isPartOfIdentifier = true;
        int c = fCharPhase3;
        while (true) {
            switch (c) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;

                case '\\': // universal character name
                    markPhase3();
                    switch (nextCharPhase3()) {
                        case 'u':
                        case 'U':
                            length++;
                            break;
                        default:
                            restorePhase3();
                            isPartOfIdentifier = false;
                            break;
                    }
                    break;

                case END_OF_INPUT:
                    if (fSupportContentAssist) {
                        tokenKind = IToken.tCOMPLETION;
                    }
                    isPartOfIdentifier = false;
                    break;
                case ' ':
                case '\t':
                case 0xb:
                case '\f':
                case '\r':
                case '\n':
                    isPartOfIdentifier = false;
                    break;

                case '$':
                    isPartOfIdentifier = fOptions.fSupportDollarInIdentifiers;
                    break;
                case '@':
                    isPartOfIdentifier = fOptions.fSupportAtSignInIdentifiers;
                    break;

                case '{':
                case '}':
                case '[':
                case ']':
                case '#':
                case '(':
                case ')':
                case '<':
                case '>':
                case '%':
                case ':':
                case ';':
                case '.':
                case '?':
                case '*':
                case '+':
                case '-':
                case '/':
                case '^':
                case '&':
                case '|':
                case '~':
                case '!':
                case '=':
                case ',':
                case '"':
                case '\'':
                    isPartOfIdentifier = false;
                    break;

                default:
                    isPartOfIdentifier = Character.isUnicodeIdentifierPart((char) c);
                    break;
            }

            if (!isPartOfIdentifier) {
                break;
            }

            length++;
            c = nextCharPhase3();
        }

        return newToken(tokenKind, start, length);
    }

    private void lineComment(final int start) {
        int c = fCharPhase3;
        while (true) {
            switch (c) {
                case END_OF_INPUT:
                case '\n':
                    fLog.handleComment(false, start, fOffset);
                    return;
            }
            c = nextCharPhase3();
        }
    }

    /**
     * Saves the current state of phase3, necessary for '...', '%:%:' and UNCs.
     */
    private void markPhase3() {
        fMarkOffset = fOffset;
        fMarkEndOffset = fEndOffset;
        fMarkPrefetchedChar = fCharPhase3;
    }

    private Token newDigraphToken(int kind, int offset) {
        return new TokenForDigraph(kind, fSource, offset, fOffset);
    }

    private Token newToken(int kind, int offset) {
        return new Token(kind, fSource, offset, fOffset);
    }

    private Token newToken(final int kind, final int offset, final int imageLength) {
        final int endOffset = fOffset;
        final int sourceLen = endOffset - offset;
        char[] image;
        if (sourceLen != imageLength) {
            image = getCharImage(offset, endOffset, imageLength);
        } else {
            image = new char[imageLength];
            System.arraycopy(fInput, offset, image, 0, imageLength);
        }
        return new TokenWithImage(kind, fSource, offset, endOffset, image);
    }

    /**
     * Perform phase 1-3: Replace \r\n with \n, handle trigraphs, detect
     * line-splicing. Changes fOffset, fEndOffset and fCharPhase3, stateless
     * otherwise.
     */
    @SuppressWarnings("fallthrough")
    private int nextCharPhase3() {
        int pos = fEndOffset;
        do {
            if (pos + 1 >= fLimit) {
                if (pos >= fLimit) {
                    fOffset = fLimit;
                    fEndOffset = fLimit;
                    fCharPhase3 = END_OF_INPUT;
                    return END_OF_INPUT;
                }
                fOffset = pos;
                fEndOffset = pos + 1;
                fCharPhase3 = fInput[pos];
                return fCharPhase3;
            }

            final char c = fInput[pos];
            fOffset = pos;
            fEndOffset = ++pos;
            fCharPhase3 = c;
            switch (c) {
                // windows line-ending
                case '\r':
                    if (fInput[pos] == '\n') {
                        fEndOffset = pos + 1;
                        fCharPhase3 = '\n';
                        return '\n';
                    }
                    return c;

                    // trigraph sequences
                case '?':
                    if (fInput[pos] != '?' || pos + 1 >= fLimit) {
                        return c;
                    }
                    final char trigraph = checkTrigraph(fInput[pos + 1]);
                    if (trigraph == 0) {
                        return c;
                    }
                    if (trigraph != '\\') {
                        fEndOffset = pos + 2;
                        fCharPhase3 = trigraph;
                        return trigraph;
                    }
                    pos += 2;
                    // no break, handle backslash

                case '\\':
                    final int lsPos = findEndOfLineSpliceSequence(pos);
                    if (lsPos > pos) {
                        pos = lsPos;
                        continue;
                    }
                    fEndOffset = pos;
                    fCharPhase3 = '\\';
                    return '\\'; // don't return c, it may be a '?'

                default:
                    return c;
            }
        } while (true);
    }

    /**
     * Advances to the next pound token that starts a preprocessor directive.
     * 
     * @return pound token of the directive or end-of-input.
     * @throws OffsetLimitReachedException
     *             when completion is requested in a literal or an header-name.
     */
    public Token nextDirective() throws OffsetLimitReachedException {
        fInsideIncludeDirective = false;
        final Token t = fToken;
        boolean haveNL = t == null || t.getType() == tNEWLINE;
        while (true) {
            final boolean hadNL = haveNL;
            haveNL = false;
            final int start = fOffset;
            final int c = fCharPhase3;

            // optimization avoids calling nextCharPhase3
            int d;
            final int pos = fEndOffset;
            if (pos + 1 >= fLimit) {
                d = nextCharPhase3();
            } else {
                d = fInput[pos];
                switch (d) {
                    case '\\':
                        d = nextCharPhase3();
                        break;
                    case '?':
                        if (fInput[pos + 1] == '?') {
                            d = nextCharPhase3();
                            break;
                        }
                        fOffset = pos;
                        fCharPhase3 = d;
                        fEndOffset = pos + 1;
                        break;
                    default:
                        fOffset = pos;
                        fCharPhase3 = d;
                        fEndOffset = pos + 1;
                        break;
                }
            }

            switch (c) {
                case END_OF_INPUT:
                    fLastToken = fToken = newToken(IToken.tEND_OF_INPUT, start);
                    return fToken;
                case '\n':
                    haveNL = true;
                    continue;
                case ' ':
                case '\t':
                case 0xb: // vertical tab
                case '\f':
                case '\r':
                    haveNL = hadNL;
                    continue;

                case '"':
                    stringLiteral(start, IToken.tSTRING);
                    continue;

                case '\'':
                    charLiteral(start, IToken.tCHAR);
                    continue;

                case '/':
                    switch (d) {
                        case '/':
                            nextCharPhase3();
                            lineComment(start);
                            continue;
                        case '*':
                            blockComment(start, '*');
                            haveNL = hadNL;
                            continue;
                        case '%':
                            if (fOptions.fSupportSlashPercentComments) {
                                blockComment(start, '%');
                            }
                            continue;
                    }
                    continue;

                case '%':
                    if (hadNL) {
                        if (d == ':') {
                            // found at least '#'
                            final int e = nextCharPhase3();
                            if (e == '%') {
                                markPhase3();
                                if (nextCharPhase3() == ':') {
                                    // found '##'
                                    nextCharPhase3();
                                    continue;
                                }
                                restorePhase3();
                            }
                            fLastToken = new Token(tNEWLINE, fSource, 0, start); // offset
                            // not
                            // significant
                            fToken = newDigraphToken(IToken.tPOUND, start);
                            return fToken;
                        }
                    }
                    continue;

                case '#':
                    if (hadNL && d != '#') {
                        fLastToken = new Token(tNEWLINE, fSource, 0, start); // offset
                        // not
                        // significant
                        fToken = newToken(IToken.tPOUND, start);
                        return fToken;
                    }
                    continue;

                default:
                    continue;
            }
        }
    }

    /**
     * Advances to the next token, skipping whitespace other than newline.
     * 
     * @throws OffsetLimitReachedException
     *             when completion is requested in a literal or a header-name.
     */
    public Token nextToken() throws OffsetLimitReachedException {
        fLastToken = fToken;
        return fToken = fetchToken();
    }

    private Token number(final int start, int length, boolean isFloat) throws OffsetLimitReachedException {
        boolean isPartOfNumber = true;
        boolean isHex = false;
        int c = fCharPhase3;
        while (true) {
            switch (c) {
                // non-digit
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'Y':
                case 'Z':
                case '_':

                    // digit
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;

                case 'x':
                case 'X':
                    isHex = !isFloat;
                    break;

                // period
                case '.':
                    isFloat = true;
                    break;

                // exponents
                case 'e':
                case 'E':
                    if (isHex) {
                        break;
                    }
                    //$FALL-THROUGH$
                case 'p':
                case 'P':
                    length++;
                    c = nextCharPhase3();
                    switch (c) {
                        case '+':
                        case '-':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            isFloat = true;
                            isHex = false;
                            length++;
                            c = nextCharPhase3();
                            break;
                    }
                    continue;

                    // universal character name (non-digit)
                case '\\':
                    markPhase3();
                    switch (nextCharPhase3()) {
                        case 'u':
                        case 'U':
                            length++;
                            break;
                        default:
                            restorePhase3();
                            isPartOfNumber = false;
                            break;
                    }
                    break;

                case END_OF_INPUT:
                    if (fSupportContentAssist) {
                        throw new OffsetLimitReachedException(ORIGIN_LEXER, newToken(
                                (isFloat ? IToken.tFLOATINGPT : IToken.tINTEGER), start, length));
                    }
                    isPartOfNumber = false;
                    break;

                default:
                    isPartOfNumber = false;
                    break;
            }
            if (!isPartOfNumber) {
                break;
            }

            c = nextCharPhase3();
            length++;
        }

        return newToken((isFloat ? IToken.tFLOATINGPT : IToken.tINTEGER), start, length);
    }

    /**
     * Restores a previously saved state of phase3.
     */
    private void restorePhase3() {
        fOffset = fMarkOffset;
        fEndOffset = fMarkEndOffset;
        fCharPhase3 = fMarkPrefetchedChar;
    }

    /**
     * Resets the lexer to the first char and prepares for content-assist mode.
     */
    public void setContentAssistMode(int offset) {
        fSupportContentAssist = true;
        fLimit = Math.min(offset, fInput.length);
        // re-initialize
        fOffset = fEndOffset = fStart;
        nextCharPhase3();
    }

    /**
     * Call this before consuming the name-token in the include directive. It
     * causes the header-file tokens to be created.
     */
    public void setInsideIncludeDirective(boolean val) {
        fInsideIncludeDirective = val;
    }

    @SuppressWarnings("fallthrough")
    private Token stringLiteral(final int start, final int tokenType) throws OffsetLimitReachedException {
        boolean escaped = false;
        boolean done = false;

        int length = tokenType == IToken.tSTRING ? 1 : 2;
        int c = fCharPhase3;

        loop: while (!done) {
            switch (c) {
                case END_OF_INPUT:
                    if (fSupportContentAssist) {
                        throw new OffsetLimitReachedException(ORIGIN_LEXER,
                                newToken(tokenType, start, length));
                    }
                    // no break;
                case '\n':
                    handleProblem(IProblem.SCANNER_UNBOUNDED_STRING, getInputChars(start, fOffset), start);
                    break loop;

                case '\\':
                    escaped = !escaped;
                    break;
                case '"':
                    if (!escaped) {
                        done = true;
                    }
                    escaped = false;
                    break;
                default:
                    escaped = false;
                    break;
            }
            length++;
            c = nextCharPhase3();
        }
        return newToken(tokenType, start, length);
    }
}
