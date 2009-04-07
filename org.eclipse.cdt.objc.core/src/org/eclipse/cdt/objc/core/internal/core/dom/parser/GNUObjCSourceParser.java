package org.eclipse.cdt.objc.core.internal.core.dom.parser;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
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

public class GNUObjCSourceParser extends GNUCSourceParser {
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
    protected IASTDeclaration declaration(final DeclarationOptions declOption) throws EndOfFileException,
            BacktrackException {
        switch (LT(1)) {
            // ObjC static/instance method calls
            case IToken.tPLUS:
            case IToken.tMINUS:
                System.err.println("Woo! Found a plus/minus");
                return null;
        }
        return super.declaration(declOption);
    }

    // FIXME this is fugly stuff, but it is a starting point
    @Override
    protected IASTExpression primaryExpression() throws EndOfFileException, BacktrackException {
        switch (LT(1)) {
            case IToken.tIDENTIFIER:
                IToken t = LA(1);
                if ("@".equals(t.getImage()) && LT(2) == IToken.tSTRING) { //$NON-NLS-1$
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
                IASTExpression e = expression();
                IASTName name = null;
                // FIXME this admits [foo bar wibble] which isn't correct
                while (LT(1) != IToken.tRBRACKET && LT(1) != IToken.tCOLON) {
                    name = identifier();
                    if (LT(1) == IToken.tCOLON) {
                        consume();
                        e = expression();
                    }
                }
                consume(IToken.tRBRACKET);
                // FIXME This is a bogus type - an objc method call type
                return nodeFactory.newFieldReference(name, null);
            default:
                return super.primaryExpression();
        }
    }

}
