package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.c.ICNodeFactory;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.EndOfFileException;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.BacktrackException;
import org.eclipse.cdt.internal.core.dom.parser.DeclarationOptions;
import org.eclipse.cdt.internal.core.dom.parser.c.CNodeFactory;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;

@SuppressWarnings("restriction")
public class GNUObjCSourceParser extends GNUCSourceParser {
    private static final String AT = "@"; //$NON-NLS-1$
    private final ICNodeFactory nodeFactory;

    public GNUObjCSourceParser(IScanner scanner, ParserMode parserMode, IParserLogService logService,
            ICParserExtensionConfiguration config) {
        super(scanner, parserMode, logService, config);
        nodeFactory = CNodeFactory.getDefault();

    }

    public GNUObjCSourceParser(IScanner scanner, ParserMode parserMode, IParserLogService logService,
            ICParserExtensionConfiguration config, IIndex index) {
        super(scanner, parserMode, logService, config, index);
        nodeFactory = CNodeFactory.getDefault();
    }

    @Override
    protected IASTDeclSpecifier declSpecifierSeq(final DeclarationOptions declOption)
            throws BacktrackException, EndOfFileException, FoundDeclaratorException,
            FoundAggregateInitializer {
        switch (LT(1)) {
            case IToken.tIDENTIFIER:
                IToken t = LA(1);
                String i = t.getImage();
                if (i.startsWith(AT)) {
                    boolean isInterface = "@interface".equals(i); //$NON-NLS-1$
                    boolean isImplementation = "@implementation".equals(i); //$NON-NLS-1$
                    boolean isProtocol = "@protocol".equals(i); //$NON-NLS-1$
                    if (isInterface || isImplementation || isProtocol) {
                        // System.err.println("We're in an interface!");
                        while (LT(1) != IToken.tIDENTIFIER && !LA(1).getImage().equals("@end")) { //$NON-NLS-1$
                            consume();
                        }
                        return nodeFactory.newSimpleDeclSpecifierGCC(null /* expression */);
                        // return typeDeclaration(declOption, isInterface,
                        // isImplementation, isProtocol);
                    }
                }
                break;
            // ObjC static/instance method calls
            // case IToken.tPLUS:
            // case IToken.tMINUS:
            // System.err.println("Woo! Found a plus/minus");
            // return null;
        }
        return super.declSpecifierSeq(declOption);
    }

    @Override
    public IASTTranslationUnit parse() {
        // Does nothing, but needed so test doesn't complain about discouraged
        // access
        return super.parse();
    }

    // FIXME this is fugly stuff, but it is a starting point
    @Override
    protected IASTExpression primaryExpression() throws EndOfFileException, BacktrackException {
        switch (LT(1)) {
            case IToken.tIDENTIFIER:
                IToken t = LA(1);
                if (AT.equals(t.getImage()) && LT(2) == IToken.tSTRING) {
                    consume();
                    t = consume();
                    IASTLiteralExpression literalExpression = nodeFactory.newLiteralExpression(
                            IASTLiteralExpression.lk_string_literal, t.getImage());
                    ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getEndOffset()
                            - t.getOffset());
                    return literalExpression;
                }
                return super.primaryExpression();
            case IToken.tLBRACKET:
                consume();
                expression();
                IASTName name = null;
                boolean first = true;
                while (LT(1) != IToken.tRBRACKET) { // optional argument
                    name = identifier(); // the method call
                    if (LT(1) != IToken.tCOLON && first) {
                        break;
                    }
                    first = false;
                    consume(); // :
                    expression();
                }
                consume(IToken.tRBRACKET);
                // FIXME This is a bogus type - an objc method call type
                return nodeFactory.newFieldReference(name, null);
            default:
                return super.primaryExpression();
        }
    }

}
