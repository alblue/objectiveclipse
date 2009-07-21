package org.eclipse.cdt.objc.core.internal.parser.scanner;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer.LexerOptions;

final class DateMacro extends DynamicMacro {
    DateMacro(char[] name) {
        super(name);
    }

    private char[] createDate() {
        char[] charArray;
        StringBuilder buffer = new StringBuilder("\""); //$NON-NLS-1$
        Calendar cal = Calendar.getInstance();
        DateFormatSymbols dfs = new DateFormatSymbols();
        buffer.append(dfs.getShortMonths()[cal.get(Calendar.MONTH)]);
        buffer.append(" "); //$NON-NLS-1$
        append(buffer, cal.get(Calendar.DAY_OF_MONTH));
        buffer.append(" "); //$NON-NLS-1$
        buffer.append(cal.get(Calendar.YEAR));
        buffer.append("\""); //$NON-NLS-1$
        charArray = buffer.toString().toCharArray();
        return charArray;
    }

    @Override
    public Token execute(MacroExpander expander) {
        return new TokenWithImage(IToken.tSTRING, null, 0, 0, createDate());
    }

    public char[] getExpansionImage() {
        return createDate();
    }
}

abstract class DynamicMacro extends PreprocessorMacro {

    public DynamicMacro(char[] name) {
        super(name);
    }

    final protected void append(StringBuilder buffer, int value) {
        if (value < 10) {
            buffer.append("0"); //$NON-NLS-1$
        }
        buffer.append(value);
    }

    public abstract Token execute(MacroExpander expander);

    public final char[] getExpansion() {
        return getExpansionImage();
    }

    @Override
    public TokenList getTokens(MacroDefinitionParser mdp, LexerOptions lexOptions, MacroExpander expander) {
        TokenList result = new TokenList();
        result.append(execute(expander));
        return result;
    }

    public final boolean isDynamic() {
        return true;
    }
}

final class FileMacro extends DynamicMacro {
    FileMacro(char[] name) {
        super(name);
    }

    @Override
    public Token execute(MacroExpander expander) {
        StringBuffer buffer = new StringBuffer("\""); //$NON-NLS-1$
        buffer.append(expander.getCurrentFilename());
        buffer.append('\"');
        return new TokenWithImage(IToken.tSTRING, null, 0, 0, buffer.toString().toCharArray());
    }

    public char[] getExpansionImage() {
        return "\"file\"".toCharArray(); //$NON-NLS-1$
    }
}

class FunctionStyleMacro extends ObjectStyleMacro {
    public static final int NAMED_VAARGS = 2; // M(a...)
    public static final int NO_VAARGS = 0; // M(a)
    public static final int VAARGS = 1; // M(...)

    final private int fHasVarArgs;
    final private char[][] fParamList;
    private char[] fSignature;

    public FunctionStyleMacro(char[] name, char[][] paramList, int hasVarArgs, char[] expansion) {
        super(name, expansion);
        fParamList = paramList;
        fHasVarArgs = hasVarArgs;
    }

    public FunctionStyleMacro(char[] name, char[][] paramList, int hasVarArgs, int expansionFileOffset,
            int endFileOffset, TokenList expansion, char[] source) {
        super(name, expansionFileOffset, endFileOffset, expansion, source);
        fParamList = paramList;
        fHasVarArgs = hasVarArgs;
    }

    @Override
    public char[][] getParameterList() {
        final int length = fParamList.length;
        if (fHasVarArgs == NO_VAARGS || length == 0) {
            return fParamList;
        }
        char[][] result = new char[length][];
        System.arraycopy(fParamList, 0, result, 0, length - 1);
        if (fHasVarArgs == VAARGS) {
            result[length - 1] = Keywords.cpELLIPSIS;
        } else {
            final char[] param = fParamList[length - 1];
            final int plen = param.length;
            final int elen = Keywords.cpELLIPSIS.length;
            final char[] rp = new char[plen + elen];
            System.arraycopy(param, 0, rp, 0, plen);
            System.arraycopy(Keywords.cpELLIPSIS, 0, rp, plen, elen);
            result[length - 1] = rp;
        }
        return result;
    }

    @Override
    public char[][] getParameterPlaceholderList() {
        return fParamList;
    }

    public char[] getSignature() {
        if (fSignature != null) {
            return fSignature;
        }

        StringBuffer result = new StringBuffer();
        result.append(getName());
        result.append('(');

        final int lastIdx = fParamList.length - 1;
        if (lastIdx >= 0) {
            for (int i = 0; i < lastIdx; i++) {
                result.append(fParamList[i]);
                result.append(',');
            }
            switch (fHasVarArgs) {
                case VAARGS:
                    result.append(Keywords.cpELLIPSIS);
                    break;
                case NAMED_VAARGS:
                    result.append(fParamList[lastIdx]);
                    result.append(Keywords.cpELLIPSIS);
                    break;
                default:
                    result.append(fParamList[lastIdx]);
                    break;
            }
        }
        result.append(')');
        final int len = result.length();
        final char[] sig = new char[len];
        result.getChars(0, len, sig, 0);
        fSignature = sig;
        return sig;
    }

    /**
     * Returns one of {@link FunctionStyleMacro#NO_VAARGS}, {@link #VAARGS} or
     * {@link #NAMED_VAARGS}.
     */
    @Override
    int hasVarArgs() {
        return fHasVarArgs;
    }

    @Override
    public boolean isFunctionStyle() {
        return true;
    }
}

final class LineMacro extends DynamicMacro {
    LineMacro(char[] name) {
        super(name);
    }

    @Override
    public Token execute(MacroExpander expander) {
        int lineNumber = expander.getCurrentLineNumber();
        return new TokenWithImage(IToken.tINTEGER, null, 0, 0, Long.toString(lineNumber).toCharArray());
    }

    public char[] getExpansionImage() {
        return new char[] { '1' };
    }
}

class ObjectStyleMacro extends PreprocessorMacro {
    final int fEndOffset;
    private final char[] fExpansion;
    final int fExpansionOffset;
    private TokenList fExpansionTokens;

    public ObjectStyleMacro(char[] name, char[] expansion) {
        this(name, 0, expansion.length, null, expansion);
    }

    public ObjectStyleMacro(char[] name, int expansionOffset, int endOffset, TokenList expansion,
            char[] source) {
        super(name);
        fExpansionOffset = expansionOffset;
        fEndOffset = endOffset;
        fExpansion = source;
        fExpansionTokens = expansion;
        if (expansion != null) {
            setSource(expansion.first());
        }
    }

    public char[] getExpansion() {
        return MacroDefinitionParser.getExpansion(fExpansion, fExpansionOffset, fEndOffset);
    }

    public int getExpansionEndOffset() {
        return fEndOffset;
    }

    public char[] getExpansionImage() {
        final int length = fEndOffset - fExpansionOffset;
        if (length == fExpansion.length) {
            return fExpansion;
        }
        char[] result = new char[length];
        System.arraycopy(fExpansion, fExpansionOffset, result, 0, length);
        return result;
    }

    public int getExpansionOffset() {
        return fExpansionOffset;
    }

    @Override
    public TokenList getTokens(MacroDefinitionParser mdp, LexerOptions lexOptions, MacroExpander expander) {
        if (fExpansionTokens == null) {
            fExpansionTokens = new TokenList();
            Lexer lex = new Lexer(fExpansion, fExpansionOffset, fEndOffset, lexOptions, ILexerLog.NULL, this);
            try {
                lex.nextToken(); // consume the start token
                mdp.parseExpansion(lex, ILexerLog.NULL, getNameCharArray(), getParameterPlaceholderList(),
                        fExpansionTokens);
            } catch (OffsetLimitReachedException e) {
            }
        }
        return fExpansionTokens;
    }

    public final boolean isDynamic() {
        return false;
    }

    private void setSource(Token t) {
        final int shift = -fExpansionOffset;
        while (t != null) {
            t.fSource = this;
            t.shiftOffset(shift);
            t = (Token) t.getNext();
        }
    }
}

/**
 * Models macros used by the preprocessor
 * 
 * @since 5.0
 */
abstract class PreprocessorMacro implements IMacroBinding {
    final private char[] fName;

    public PreprocessorMacro(char[] name) {
        fName = name;
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class clazz) {
        return null;
    }

    final public ILinkage getLinkage() {
        return Linkage.NO_LINKAGE;
    }

    final public String getName() {
        return new String(fName);
    }

    final public char[] getNameCharArray() {
        return fName;
    }

    public IBinding getOwner() {
        return null;
    }

    public char[][] getParameterList() {
        return null;
    }

    public char[][] getParameterPlaceholderList() {
        return null;
    }

    public IScope getScope() {
        return null;
    }

    public abstract TokenList getTokens(MacroDefinitionParser parser, LexerOptions lexOptions,
            MacroExpander expander);

    /**
     * Returns {@link FunctionStyleMacro#NO_VAARGS}
     */
    int hasVarArgs() {
        return FunctionStyleMacro.NO_VAARGS;
    }

    public boolean isFunctionStyle() {
        return false;
    }

    @Override
    public String toString() {
        char[][] p = getParameterList();
        if (p == null) {
            return getName();
        }
        StringBuffer buf = new StringBuffer();
        buf.append(getNameCharArray());
        buf.append('(');
        for (int i = 0; i < p.length; i++) {
            if (i > 0) {
                buf.append(',');
            }
            buf.append(p[i]);
        }
        buf.append(')');
        return buf.toString();
    }
}

final class TimeMacro extends DynamicMacro {
    TimeMacro(char[] name) {
        super(name);
    }

    private char[] createDate() {
        StringBuilder buffer = new StringBuilder("\""); //$NON-NLS-1$
        Calendar cal = Calendar.getInstance();
        append(buffer, cal.get(Calendar.HOUR_OF_DAY));
        buffer.append(":"); //$NON-NLS-1$
        append(buffer, cal.get(Calendar.MINUTE));
        buffer.append(":"); //$NON-NLS-1$
        append(buffer, cal.get(Calendar.SECOND));
        buffer.append("\""); //$NON-NLS-1$
        return buffer.toString().toCharArray();
    }

    @Override
    public Token execute(MacroExpander expander) {
        return new TokenWithImage(IToken.tSTRING, null, 0, 0, createDate());
    }

    public char[] getExpansionImage() {
        return createDate();
    }
}
