package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;

/**
 * Thrown when content assist is used within the parameter list of a macro
 * expansion. It transports the token list of the current parameter for further
 * use in attempting a completion.
 * 
 * @since 5.0
 */
public class CompletionInMacroExpansionException extends OffsetLimitReachedException {

    private final TokenList fParameterTokens;

    public CompletionInMacroExpansionException(int origin, IToken lastToken, TokenList paramTokens) {
        super(origin, lastToken);
        fParameterTokens = paramTokens;
    }

    public TokenList getParameterTokens() {
        return fParameterTokens;
    }
}
