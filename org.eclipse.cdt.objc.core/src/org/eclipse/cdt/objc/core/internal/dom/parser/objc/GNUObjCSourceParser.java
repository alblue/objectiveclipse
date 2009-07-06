/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems) Ed Swartz (Nokia) Mike
 * Kucera (IBM) - bug #206952
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTGenericVisitor;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationListOwner;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTUnaryExpression;
import org.eclipse.cdt.core.dom.parser.IExtensionToken;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.EndOfFileException;
import org.eclipse.cdt.core.parser.IGCCToken;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.ASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.AbstractGNUSourceCodeParser;
import org.eclipse.cdt.internal.core.dom.parser.BacktrackException;
import org.eclipse.cdt.internal.core.dom.parser.DeclarationOptions;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousExpression;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousStatement;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayModifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCatchHandler;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDesignatedInitializer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFieldDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodDeclarator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTOptionalityLabel;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPointer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTProtocolIdExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSelectorIdExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTryBlockStatement;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypeIdExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypedefNameSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTUnaryExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisibilityLabel;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCNodeFactory;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;
import org.eclipse.cdt.objc.core.dom.parser.IObjCToken;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTArrayRangeDesignator;

/**
 * Source parser for gnu-c syntax.
 */
@SuppressWarnings("restriction")
public class GNUObjCSourceParser extends AbstractGNUSourceCodeParser {
    private static final String AT = "@"; //$NON-NLS-1$
    private static final int DEFAULT_CATCH_HANDLER_LIST_SIZE = 4;

    private static final int DEFAULT_PARAMETERS_LIST_SIZE = 4;
    private static final int DEFAULT_POINTEROPS_LIST_SIZE = 4;

    private final static int INLINE = 0x1, CONST = 0x2, RESTRICT = 0x4, VOLATILE = 0x8, SHORT = 0x10,
            UNSIGNED = 0x20, SIGNED = 0x40, COMPLEX = 0x80, IMAGINARY = 0x100;
    private static final ASTVisitor MARK_INACTIVE = new ASTGenericVisitor(true) {
        {
            shouldVisitAmbiguousNodes = true;
        }

        @Override
        protected int genericVisit(IASTNode node) {
            ((ASTNode) node).setInactive();
            return PROCESS_CONTINUE;
        }

        @Override
        public int visit(ASTAmbiguousNode node) {
            node.setInactive();
            IASTNode[] alternatives = node.getNodes();
            for (IASTNode alt : alternatives) {
                if (!alt.accept(this)) {
                    return PROCESS_ABORT;
                }
            }
            return PROCESS_CONTINUE;
        }
    };
    private IToken currentClassElaboratedToken;

    private boolean expectClassDeclaration;
    private int fPreventKnrCheck = 0;
    private final IIndex index;

    private final IObjCNodeFactory nodeFactory;

    private boolean parsingClassDeclarationList;

    private boolean parsingProtocolDeclSpecList;
    private final boolean supportGCCStyleDesignators;
    protected IASTTranslationUnit translationUnit;

    public GNUObjCSourceParser(IScanner scanner, ParserMode parserMode, IParserLogService logService,
            ICParserExtensionConfiguration config) {
        this(scanner, parserMode, logService, config, null);
    }

    public GNUObjCSourceParser(IScanner scanner, ParserMode parserMode, IParserLogService logService,
            ICParserExtensionConfiguration config, IIndex index) {
        super(scanner, logService, parserMode, ObjCNodeFactory.getDefault(), config
                .supportStatementsInExpressions(), config.supportTypeofUnaryExpressions(), config
                .supportAlignOfUnaryExpression(), config.supportKnRC(), config.supportAttributeSpecifiers(),
                config.supportDeclspecSpecifiers(), config.getBuiltinBindingsProvider());
        supportGCCStyleDesignators = config.supportGCCStyleDesignators();
        supportParameterInfoBlock = config.supportParameterInfoBlock();
        supportExtendedSizeofOperator = config.supportExtendedSizeofOperator();
        supportFunctionStyleAsm = config.supportFunctionStyleAssembler();
        this.index = index;
        nodeFactory = ObjCNodeFactory.getDefault();
    }

    private void addDeclaration(final IASTDeclarationListOwner parent, IASTDeclaration declaration,
            final boolean active) {
        if (!active) {
            declaration.accept(MARK_INACTIVE);
        }
        parent.addDeclaration(declaration);
    }

    @Override
    protected IASTDeclarator addInitializer(FoundAggregateInitializer e, DeclarationOptions options)
            throws EndOfFileException {
        final IASTDeclarator d = e.fDeclarator;
        try {
            IASTInitializer i = optionalCInitializer();
            if (i != null) {
                d.setInitializer(i);
                ((ASTNode) d).setLength(calculateEndOffset(i) - ((ASTNode) d).getOffset());
            }
        } catch (BacktrackException e1) {
            // mstodo add problem node
        }
        return d;
    }

    /**
     * Parse an array declarator starting at the square bracket.
     */
    private IASTArrayDeclarator arrayDeclarator() throws EndOfFileException, BacktrackException {
        ArrayList<IASTArrayModifier> arrayMods = new ArrayList<IASTArrayModifier>(
                DEFAULT_POINTEROPS_LIST_SIZE);
        int start = LA(1).getOffset();
        consumeArrayModifiers(arrayMods);
        if (arrayMods.isEmpty()) {
            throwBacktrack(LA(1));
        }

        final int endOffset = calculateEndOffset(arrayMods.get(arrayMods.size() - 1));
        final IASTArrayDeclarator d = nodeFactory.newArrayDeclarator(null);
        for (IASTArrayModifier m : arrayMods) {
            d.addArrayModifier(m);
        }

        ((ASTNode) d).setOffsetAndLength(start, endOffset - start);
        return d;
    }

    @Override
    protected IASTExpression assignmentExpression() throws EndOfFileException, BacktrackException {
        IASTExpression conditionalExpression = conditionalExpression();
        // if the condition not taken, try assignment operators
        if (conditionalExpression instanceof IASTConditionalExpression) {
            return conditionalExpression;
        }
        switch (LT(1)) {
            case IToken.tASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_assign, conditionalExpression);
            case IToken.tSTARASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_multiplyAssign,
                        conditionalExpression);
            case IToken.tDIVASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_divideAssign,
                        conditionalExpression);
            case IToken.tMODASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_moduloAssign,
                        conditionalExpression);
            case IToken.tPLUSASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_plusAssign, conditionalExpression);
            case IToken.tMINUSASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_minusAssign,
                        conditionalExpression);
            case IToken.tSHIFTRASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_shiftRightAssign,
                        conditionalExpression);
            case IToken.tSHIFTLASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_shiftLeftAssign,
                        conditionalExpression);
            case IToken.tAMPERASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_binaryAndAssign,
                        conditionalExpression);
            case IToken.tXORASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_binaryXorAssign,
                        conditionalExpression);
            case IToken.tBITORASSIGN:
                return assignmentOperatorExpression(IASTBinaryExpression.op_binaryOrAssign,
                        conditionalExpression);
        }
        return conditionalExpression;
    }

    /**
     * Parses for a bit field declarator starting with the colon
     */
    private IASTFieldDeclarator bitFieldDeclarator() throws EndOfFileException, BacktrackException {
        int start = consume(IToken.tCOLON).getOffset();

        final IASTExpression bitField = constantExpression();
        final int endOffset = calculateEndOffset(bitField);

        IASTFieldDeclarator d = nodeFactory.newFieldDeclarator(null, bitField);
        d.setBitFieldSize(bitField);

        ((ASTNode) d).setOffsetAndLength(start, endOffset - start);
        return d;
    }

    private IASTExpression buildEncodeExpression() throws EndOfFileException, BacktrackException {
        int offset = consume().getOffset();
        IASTTypeId typeid;
        int endOffset = -1;
        try {
            consume(IToken.tLPAREN);
            typeid = typeId(DeclarationOptions.TYPEID);
            switch (LT(1)) {
                case IToken.tRPAREN:
                case IToken.tEOC:
                    endOffset = consume().getEndOffset();
                    break;
                default:
                    typeid = null;
                    break;
            }
        } catch (BacktrackException e) {
            typeid = null;
        }
        IASTTypeIdExpression typeIdExpression = nodeFactory.newTypeIdExpression(
                IObjCASTTypeIdExpression.op_AtEncode, typeid);
        setRange(typeIdExpression, offset, endOffset);
        return typeIdExpression;
    }

    private IASTExpression buildMessageExpression() throws EndOfFileException, BacktrackException {
        IToken lb = consume(IToken.tLBRACKET);
        IASTExpression receiver = expression();
        IASTExpression selector = buildMessageSelectorExpression();
        IASTExpression f = nodeFactory.newMessageExpression(receiver, selector);
        IToken rb = consume(IToken.tRBRACKET);
        ((ASTNode) f).setOffsetAndLength(lb.getOffset(), rb.getEndOffset() - lb.getOffset());
        return f;
    }

    private IASTExpression buildMessageSelectorExpression() throws EndOfFileException, BacktrackException {
        boolean expectSelector = true;
        boolean valid = false;
        final int startOffset = LA(1).getOffset();
        int lastOffset = startOffset;
        int i = 0;
        final int length = LA(1).getLength();

        IASTExpressionList expressionList = nodeFactory.newExpressionList();
        ((ASTNode) expressionList).setOffset(startOffset);

        StringBuilder builder = new StringBuilder();
        IASTName pName;

        paramLoop: while (true) {
            switch (LT(1)) {
                case IToken.tEOC:
                case IToken.tRBRACKET:
                    if (!valid) {
                        throwBacktrack(LA(1));
                    }
                    break paramLoop;
                case IToken.tCOLON:
                    consume();
                    builder.append(":"); //$NON-NLS-1$
                    expectSelector = false;
                    valid = false;
                    break;
                default:
                    if (expectSelector) {
                        pName = identifier();
                        builder.append(pName.getSimpleID());
                        valid = (i > 0) ? false : true;
                    } else {
                        IASTExpression expr = expression();
                        expressionList.addExpression(expr);
                        lastOffset = calculateEndOffset(expr);
                        expectSelector = true;
                        valid = true;
                    }
                    break;
            }
            ++i;
        }

        ((ASTNode) expressionList).setLength(lastOffset - startOffset);
        String n = builder.toString();
        IASTName name = nodeFactory.newName(n.toCharArray());
        ((ASTNode) name).setOffsetAndLength(startOffset, length);

        IASTExpression nameExpr = nodeFactory.newIdExpression(name);
        ((ASTNode) nameExpr).setOffsetAndLength((ASTNode) name);

        IASTExpression s = nodeFactory.newMessageSelectorExpression(nameExpr, expressionList);
        int exprListEndOffset = ((ASTNode) expressionList).getOffset()
                + ((ASTNode) expressionList).getLength();
        ((ASTNode) s).setOffsetAndLength(startOffset, exprListEndOffset - startOffset);

        return s;
    }

    private IASTDeclSpecifier buildMethodSimpleDeclSpec(int storageClass, int offset, int endOffset)
            throws EndOfFileException, BacktrackException {
        if (LT(1) == IToken.tLPAREN) {
            IToken lp = consume(IToken.tLPAREN);
            try {
                IASTDeclSpecifier declSpec = declSpecifierSeq(DeclarationOptions.GLOBAL);
                declSpec.setStorageClass(storageClass);
                return declSpec;
            } catch (FoundDeclaratorException e) {
                throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, lp.getEndOffset(), 0), e.declarator);
            } catch (FoundAggregateInitializer e) {
                throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, lp.getEndOffset(), 0), e.fDeclarator);
            }
        } else {
            IASTSimpleDeclSpecifier declSpec = nodeFactory.newSimpleDeclSpecifier();
            declSpec.setType(IASTSimpleDeclSpecifier.t_void);
            declSpec.setStorageClass(storageClass);
            ((ASTNode) declSpec).setOffsetAndLength(offset, endOffset - offset);
            return declSpec;
        }
        return null;
    }

    private IObjCASTTypedefNameSpecifier buildNamedTypeSpecifier(IASTName name, int storageClass,
            int options, int offset, int endOffset) {
        IObjCASTTypedefNameSpecifier declSpec = nodeFactory.newTypedefNameSpecifier(name);
        configureDeclSpec(declSpec, storageClass, options);
        declSpec.setRestrict((options & RESTRICT) != 0);
        ((ASTNode) declSpec).setOffsetAndLength(offset, endOffset - offset);
        return declSpec;
    }

    private IASTExpression buildProtocolExpression() throws EndOfFileException, BacktrackException {
        int offset = consume().getOffset();
        IASTName proto;
        int endOffset = -1;
        try {
            consume(IToken.tLPAREN);
            proto = identifier();
            switch (LT(1)) {
                case IToken.tRPAREN:
                case IToken.tEOC:
                    endOffset = consume().getEndOffset();
                    break;
                default:
                    proto = null;
                    break;
            }
        } catch (BacktrackException e) {
            proto = null;
        }
        IObjCASTProtocolIdExpression protocolExpression = nodeFactory.newProtocolExpression(proto);
        setRange(protocolExpression, offset, endOffset);
        return protocolExpression;
    }

    private IASTExpression buildSelectorExpression() throws EndOfFileException, BacktrackException {
        int offset = consume().getOffset();
        IASTName selid;
        int endOffset = -1;
        try {
            consume(IToken.tLPAREN);
            selid = selectorId();
            switch (LT(1)) {
                case IToken.tRPAREN:
                case IToken.tEOC:
                    endOffset = consume().getEndOffset();
                    break;
                default:
                    selid = null;
                    break;
            }
        } catch (BacktrackException e) {
            selid = null;
        }

        IObjCASTSelectorIdExpression selectorExpression = nodeFactory.newSelectorExpression(selid);
        setRange(selectorExpression, offset, endOffset);
        IASTExpression result = selectorExpression;
        return result;
    }

    private IObjCASTSimpleDeclSpecifier buildSimpleDeclSpec(int storageClass, int simpleType, int options,
            int isLong, IASTExpression typeofExpression, int offset, int endOffset) {
        IObjCASTSimpleDeclSpecifier declSpec;
        if (typeofExpression != null) {
            declSpec = nodeFactory.newSimpleDeclSpecifierGCC(typeofExpression);
        } else {
            declSpec = nodeFactory.newSimpleDeclSpecifier();
        }

        configureDeclSpec(declSpec, storageClass, options);
        declSpec.setType(simpleType);
        declSpec.setLong(isLong == 1);
        declSpec.setLongLong(isLong > 1);
        declSpec.setRestrict((options & RESTRICT) != 0);
        declSpec.setUnsigned((options & UNSIGNED) != 0);
        declSpec.setSigned((options & SIGNED) != 0);
        declSpec.setShort((options & SHORT) != 0);
        declSpec.setComplex((options & COMPLEX) != 0);
        declSpec.setImaginary((options & IMAGINARY) != 0);

        ((ASTNode) declSpec).setOffsetAndLength(offset, endOffset - offset);
        return declSpec;
    }

    protected IASTStatement catchBlockCompoundStatement() throws BacktrackException, EndOfFileException {
        if (mode == ParserMode.QUICK_PARSE || mode == ParserMode.STRUCTURAL_PARSE || !isActiveCode()) {
            int offset = LA(1).getOffset();
            IToken last = skipOverCompoundStatement();
            IASTCompoundStatement cs = nodeFactory.newCompoundStatement();
            setRange(cs, offset, last.getEndOffset());
            return cs;
        } else if (mode == ParserMode.COMPLETION_PARSE || mode == ParserMode.SELECTION_PARSE) {
            if (scanner.isOnTopContext()) {
                return compoundStatement();
            }
            int offset = LA(1).getOffset();
            IToken last = skipOverCompoundStatement();
            IASTCompoundStatement cs = nodeFactory.newCompoundStatement();
            setRange(cs, offset, last.getEndOffset());
            return cs;
        }
        return compoundStatement();
    }

    private void catchHandlerSequence(List<IObjCASTCatchHandler> catchHandlers) throws EndOfFileException,
            BacktrackException {
        if (LT(1) == IToken.tEOC) {
            return;
        }

        if (LT(1) != IObjCToken.t_AtCatch) {
            throwBacktrack(LA(1)); // error, need at least one
        }

        int lt1 = LT(1);
        while (lt1 == IObjCToken.t_AtCatch) {
            int startOffset = consume().getOffset();
            lt1 = LT(1);
            IASTDeclaration decl = null;
            boolean isDefault = false;
            if (lt1 == IToken.tLPAREN) {
                consume(IToken.tLPAREN);
                try {
                    decl = simpleSingleDeclaration(DeclarationOptions.EXCEPTION);
                    if (LT(1) != IToken.tEOC) {
                        consume(IToken.tRPAREN);
                    }
                } catch (BacktrackException bte) {
                    failParse();
                    IASTProblem p = createProblem(bte);
                    IASTProblemDeclaration pd = nodeFactory.newProblemDeclaration(p);
                    ((ASTNode) pd).setOffsetAndLength(((ASTNode) p));
                    decl = pd;
                }
            } else {
                isDefault = true;
            }

            IObjCASTCatchHandler handler = nodeFactory.newCatchHandler(decl, null);

            if (LT(1) != IToken.tEOC) {
                IASTStatement compoundStatement = catchBlockCompoundStatement();
                ((ASTNode) handler).setOffsetAndLength(startOffset, calculateEndOffset(compoundStatement)
                        - startOffset);
                handler.setIsDefaultCatch(isDefault);
                if (compoundStatement != null) {
                    handler.setCatchBody(compoundStatement);
                }
            }
            catchHandlers.add(handler);
            lt1 = LTcatchEOF(1);
        }
    }

    private IObjCASTCompositeTypeSpecifier categoryImplementation() throws EndOfFileException,
            BacktrackException {
        IASTName name = identifier();
        if (name.getSimpleID()[0] == '@') {
            throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, ((ASTNode) name).getOffset(),
                    ((ASTNode) name).getLength()));
        }
        consume(IToken.tLPAREN);
        identifier();
        consume(IToken.tRPAREN);
        IObjCASTCompositeTypeSpecifier astSpecifier = nodeFactory.newCompositeTypeSpecifier(
                IObjCASTCompositeTypeSpecifier.k_category, name);
        implementationDefinitionList(astSpecifier);
        return astSpecifier;
    }

    public IObjCASTCompositeTypeSpecifier categoryInterface() throws EndOfFileException, BacktrackException {
        IASTName name = identifier();
        if (name.getSimpleID()[0] == '@') {
            throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, ((ASTNode) name).getOffset(),
                    ((ASTNode) name).getLength()));
        }
        consume(IToken.tLPAREN);
        identifier();
        consume(IToken.tRPAREN);
        IObjCASTCompositeTypeSpecifier astSpecifier = nodeFactory.newCompositeTypeSpecifier(
                IObjCASTCompositeTypeSpecifier.k_category, name);
        int lt1 = LT(1);
        if (lt1 == IToken.tLT) {
            protocolReferenceList(astSpecifier);
            lt1 = LT(1);
        }
        interfaceDeclarationList(astSpecifier, ObjCDeclarationOptions.INTERFACE_LIST);
        return astSpecifier;
    }

    private IASTSimpleDeclaration checkKnrParameterDeclaration(IASTDeclaration decl,
            final IASTName[] parmNames) {
        if (decl instanceof IASTSimpleDeclaration == false) {
            return null;
        }

        IASTSimpleDeclaration declaration = ((IASTSimpleDeclaration) decl);
        IASTDeclarator[] decltors = declaration.getDeclarators();
        for (IASTDeclarator decltor : decltors) {
            boolean decltorOk = false;
            final char[] nchars = decltor.getName().toCharArray();
            for (IASTName parmName : parmNames) {
                if (CharArrayUtils.equals(nchars, parmName.toCharArray())) {
                    decltorOk = true;
                    break;
                }
            }
            if (!decltorOk) {
                return null;
            }
        }
        return declaration;
    }

    protected IASTInitializer cInitializerClause(boolean inAggregate) throws EndOfFileException,
            BacktrackException {
        final int offset = LA(1).getOffset();
        if (LT(1) != IToken.tLBRACE) {
            IASTExpression assignmentExpression = assignmentExpression();
            if (inAggregate && skipTrivialExpressionsInAggregateInitializers) {
                if (!ASTQueries.canContainName(assignmentExpression)) {
                    return null;
                }
            }
            IASTInitializerExpression result = nodeFactory.newInitializerExpression(assignmentExpression);
            setRange(result, assignmentExpression);
            return result;
        }

        // it's an aggregate initializer
        consume(IToken.tLBRACE);
        IASTInitializerList result = nodeFactory.newInitializerList();

        // bug 196468, gcc accepts empty braces.
        if (supportGCCStyleDesignators && LT(1) == IToken.tRBRACE) {
            int endOffset = consume().getEndOffset();
            setRange(result, offset, endOffset);
            return result;
        }

        for (;;) {
            final int checkOffset = LA(1).getOffset();
            // required at least one initializer list
            // get designator list
            List<? extends IObjCASTDesignator> designator = designatorList();
            if (designator == null) {
                IASTInitializer initializer = cInitializerClause(true);
                // depending on value of skipTrivialItemsInCompoundInitializers
                // initializer may be null
                if (initializer != null) {
                    result.addInitializer(initializer);
                }
            } else {
                if (LT(1) == IToken.tASSIGN) {
                    consume();
                }
                IASTInitializer initializer = cInitializerClause(false);
                IObjCASTDesignatedInitializer desigInitializer = nodeFactory
                        .newDesignatedInitializer(initializer);
                setRange(desigInitializer, designator.get(0));
                adjustLength(desigInitializer, initializer);

                for (IObjCASTDesignator d : designator) {
                    desigInitializer.addDesignator(d);
                }
                result.addInitializer(desigInitializer);
            }

            // can end with ", }" or "}"
            boolean canContinue = LT(1) == IToken.tCOMMA;
            if (canContinue) {
                consume();
            }

            switch (LT(1)) {
                case IToken.tRBRACE:
                    int lastOffset = consume().getEndOffset();
                    setRange(result, offset, lastOffset);
                    return result;

                case IToken.tEOC:
                    setRange(result, offset, LA(1).getOffset());
                    return result;
            }

            if (!canContinue || LA(1).getOffset() == checkOffset) {
                throwBacktrack(offset, LA(1).getEndOffset() - offset);
            }
        }
        // consume the closing brace
    }

    private IObjCASTCompositeTypeSpecifier classImplementation() throws EndOfFileException,
            BacktrackException {
        IASTName name = identifier();
        if (name.getSimpleID()[0] == '@') {
            throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, ((ASTNode) name).getOffset(),
                    ((ASTNode) name).getLength()));
        }
        IObjCASTCompositeTypeSpecifier astSpecifier = nodeFactory.newCompositeTypeSpecifier(
                IObjCASTCompositeTypeSpecifier.k_class, name);
        int lt1 = LT(1);
        if (lt1 == IToken.tCOLON) {
            consume();
            IToken bc = consume(IToken.tIDENTIFIER);
            IASTName bname = nodeFactory.newName(bc.getCharImage());
            ((ASTNode) bname).setOffsetAndLength(bc.getOffset(), bc.getLength());
            IObjCASTBaseSpecifier base = nodeFactory.newBaseSpecifier(bname, false);
            ((ASTNode) base).setOffsetAndLength((ASTNode) bname);
            astSpecifier.addBaseSpecifier(base);
            lt1 = LT(1);
        }
        if (lt1 == IToken.tLBRACE) {
            instanceVariables(astSpecifier);
            lt1 = LT(1);
        }
        implementationDefinitionList(astSpecifier);
        return astSpecifier;
    }

    public IObjCASTCompositeTypeSpecifier classInterface() throws EndOfFileException, BacktrackException {
        IASTName name = identifier();
        if (name.getSimpleID()[0] == '@') {
            throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, ((ASTNode) name).getOffset(),
                    ((ASTNode) name).getLength()));
        }
        IObjCASTCompositeTypeSpecifier astSpecifier = nodeFactory.newCompositeTypeSpecifier(
                IObjCASTCompositeTypeSpecifier.k_class, name);
        int lt1 = LT(1);
        if (lt1 == IToken.tCOLON) {
            consume();
            IToken bc = consume(IToken.tIDENTIFIER);
            IASTName bname = nodeFactory.newName(bc.getCharImage());
            ((ASTNode) bname).setOffsetAndLength(bc.getOffset(), bc.getLength());
            IObjCASTBaseSpecifier base = nodeFactory.newBaseSpecifier(bname, false);
            ((ASTNode) base).setOffsetAndLength((ASTNode) bname);
            astSpecifier.addBaseSpecifier(base);
            lt1 = LT(1);
        }
        if (lt1 == IToken.tLT) {
            protocolReferenceList(astSpecifier);
            lt1 = LT(1);
        }
        if (lt1 == IToken.tLBRACE) {
            instanceVariables(astSpecifier);
            lt1 = LT(1);
        }
        interfaceDeclarationList(astSpecifier, ObjCDeclarationOptions.INTERFACE_LIST);
        return astSpecifier;
    }

    private IASTDeclSpecifier classMethodDeclaration(int offset, int endOffset) throws EndOfFileException,
            BacktrackException {
        return buildMethodSimpleDeclSpec(IASTDeclSpecifier.sc_static, offset, endOffset);
    }

    private IASTDeclSpecifier classMethodDefinition(int offset, int endOffset) throws EndOfFileException,
            BacktrackException {
        return buildMethodSimpleDeclSpec(IASTDeclSpecifier.sc_static, offset, endOffset);
    }

    private void configureDeclSpec(IASTDeclSpecifier declSpec, int storageClass, int options) {
        declSpec.setStorageClass(storageClass);
        declSpec.setConst((options & CONST) != 0);
        declSpec.setVolatile((options & VOLATILE) != 0);
        declSpec.setInline((options & INLINE) != 0);
    }

    protected void consumeArrayModifiers(List<IASTArrayModifier> arrayMods) throws EndOfFileException,
            BacktrackException {
        while (LT(1) == IToken.tLBRACKET) {
            // eat the '['
            int startOffset = consume().getOffset();

            boolean isStatic = false;
            boolean isConst = false;
            boolean isRestrict = false;
            boolean isVolatile = false;
            boolean isVarSized = false;

            outerLoop: do {
                switch (LT(1)) {
                    case IToken.t_static:
                        isStatic = true;
                        consume();
                        break;
                    case IToken.t_const:
                        isConst = true;
                        consume();
                        break;
                    case IToken.t_volatile:
                        isVolatile = true;
                        consume();
                        break;
                    case IToken.t_restrict:
                        isRestrict = true;
                        consume();
                        break;
                    case IToken.tSTAR:
                        isVarSized = true;
                        consume();
                        break outerLoop;
                    default:
                        break outerLoop;
                }
            } while (true);

            IASTExpression exp = null;

            if (LT(1) != IToken.tRBRACKET) {
                if (!(isStatic || isRestrict || isConst || isVolatile)) {
                    exp = assignmentExpression();
                } else {
                    exp = constantExpression();
                }
            }
            int lastOffset;
            switch (LT(1)) {
                case IToken.tRBRACKET:
                    lastOffset = consume().getEndOffset();
                    break;
                case IToken.tEOC:
                    lastOffset = Integer.MAX_VALUE;
                    break;
                default:
                    throw backtrack;
            }

            IObjCASTArrayModifier arrayMod = nodeFactory.newArrayModifier(exp);
            arrayMod.setStatic(isStatic);
            arrayMod.setConst(isConst);
            arrayMod.setVolatile(isVolatile);
            arrayMod.setRestrict(isRestrict);
            arrayMod.setVariableSized(isVarSized);
            ((ASTNode) arrayMod).setOffsetAndLength(startOffset, lastOffset - startOffset);
            arrayMods.add(arrayMod);
        }
    }

    /**
     * Parse a Pointer Operator.
     * 
     * ptrOperator : "*" (cvQualifier)* | "&" | ::? nestedNameSpecifier "*"
     * (cvQualifier)*
     * 
     * @throws BacktrackException
     *             to request a backtrack
     */
    protected void consumePointerOperators(List<IASTPointerOperator> pointerOps) throws EndOfFileException,
            BacktrackException {
        for (;;) {
            // __attribute__ in-between pointers
            __attribute_decl_seq(supportAttributeSpecifiers, false);

            IToken mark = mark();
            IToken last = null;

            boolean isConst = false, isVolatile = false, isRestrict = false;

            if (LT(1) != IToken.tSTAR) {
                backup(mark);
                break;
            }

            last = consume();
            int startOffset = mark.getOffset();
            for (;;) {
                IToken t = LA(1);
                switch (LT(1)) {
                    case IToken.t_const:
                        last = consume();
                        isConst = true;
                        break;
                    case IToken.t_volatile:
                        last = consume();
                        isVolatile = true;
                        break;
                    case IToken.t_restrict:
                        last = consume();
                        isRestrict = true;
                        break;
                }

                if (t == LA(1)) {
                    break;
                }
            }

            IObjCASTPointer po = nodeFactory.newPointer();
            ((ASTNode) po).setOffsetAndLength(startOffset, last.getEndOffset() - startOffset);
            po.setConst(isConst);
            po.setVolatile(isVolatile);
            po.setRestrict(isRestrict);
            pointerOps.add(po);
        }
    }

    private int countKnRCParms() {
        IToken mark = null;
        int parmCount = 0;
        boolean previousWasIdentifier = false;

        try {
            mark = mark();

            // starts at the beginning of the parameter list
            for (;;) {
                if (LT(1) == IToken.tCOMMA) {
                    consume();
                    previousWasIdentifier = false;
                } else if (LT(1) == IToken.tIDENTIFIER) {
                    consume();
                    if (previousWasIdentifier == true) {
                        backup(mark);
                        return 0; // i.e. KnR C won't have int f(typedef x)
                        // char
                        // x; {}
                    }
                    previousWasIdentifier = true;
                    parmCount++;
                } else if (LT(1) == IToken.tRPAREN) {
                    if (!previousWasIdentifier) {
                        // if the first token encountered is tRPAREN then it's
                        // not K&R C
                        // the first token when counting K&R C parms is always
                        // an identifier
                        backup(mark);
                        return 0;
                    }
                    consume();
                    break;
                } else {
                    backup(mark);
                    return 0; // i.e. KnR C won't have int f(char) char x; {}
                }
            }

            // if the next token is a tSEMI then the declaration was a regular
            // declaration statement i.e. int f(type_def);
            final int lt1 = LT(1);
            if (lt1 == IToken.tSEMI || lt1 == IToken.tLBRACE) {
                backup(mark);
                return 0;
            }

            // look ahead for the start of the function body, if end of file is
            // found then return 0 parameters found (implies not KnR C)
            int previous = -1;
            int next = LA(1).hashCode();
            while (LT(1) != IToken.tLBRACE) {
                // fix for 100104: check if the parameter declaration is a valid
                // one
                try {
                    simpleDeclaration(DeclarationOptions.LOCAL);
                } catch (BacktrackException e) {
                    backup(mark);
                    return 0;
                }

                next = LA(1).hashCode();
                if (next == previous) { // infinite loop detected
                    break;
                }
                previous = next;
            }

            backup(mark);
            return parmCount;
        } catch (EndOfFileException eof) {
            if (mark != null) {
                backup(mark);
            }

            return 0;
        }
    }

    @Override
    protected ASTVisitor createAmbiguityNodeVisitor() {
        return new ObjCASTAmbiguityResolver();
    }

    @Override
    protected IASTAmbiguousExpression createAmbiguousBinaryVsCastExpression(IASTBinaryExpression binary,
            IASTCastExpression castExpr) {
        return new ObjCASTAmbiguousBinaryVsCastExpression(binary, castExpr);
    }

    @Override
    protected IASTAmbiguousExpression createAmbiguousCastVsFunctionCallExpression(
            IASTCastExpression castExpr, IASTFunctionCallExpression funcCall) {
        return new ObjCASTAmbiguousCastVsFunctionCallExpression(castExpr, funcCall);
    }

    @Override
    protected IASTAmbiguousExpression createAmbiguousExpression() {
        return new ObjCASTAmbiguousExpression();
    }

    @Override
    protected IASTAmbiguousStatement createAmbiguousStatement() {
        return new ObjCASTAmbiguousStatement();
    }

    private IASTProblemDeclaration createKnRCProblemDeclaration(int offset, int length)
            throws EndOfFileException {
        IASTProblem p = createProblem(IProblem.SYNTAX_ERROR, offset, length);
        IASTProblemDeclaration pd = nodeFactory.newProblemDeclaration(p);
        ((ASTNode) pd).setOffsetAndLength((ASTNode) p);

        // consume until LBRACE is found (to leave off at the function body and
        // continue from there)
        IToken previous = null;
        IToken next = null;
        while (LT(1) != IToken.tLBRACE) {
            next = consume();
            if (next == previous) { // infinite loop detected
                break;
            }
            previous = next;
        }

        return pd;
    }

    @Override
    protected IASTDeclaration declaration(final DeclarationOptions declOption) throws EndOfFileException,
            BacktrackException {
        switch (LT(1)) {
            case IToken.t_asm:
                return asmDeclaration();
            case IToken.tSEMI:
                IToken semi = consume();
                IASTDeclSpecifier declspec = nodeFactory.newSimpleDeclSpecifier();
                IASTSimpleDeclaration decl = nodeFactory.newSimpleDeclaration(declspec);
                decl.setDeclSpecifier(declspec);
                ((ASTNode) declspec).setOffsetAndLength(semi.getOffset(), 0);
                ((ASTNode) decl).setOffsetAndLength(semi.getOffset(), semi.getLength());
                return decl;
            case IObjCToken.t_AtPrivate:
                if (declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                    IToken t = consume();
                    IObjCASTVisibilityLabel label = nodeFactory
                            .newVisibilityLabel(IObjCASTVisibilityLabel.v_public);
                    setRange(label, t.getOffset(), t.getEndOffset());
                    return label;
                }
                break;
            case IObjCToken.t_AtProtected:
                if (declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                    IToken t = consume();
                    IObjCASTVisibilityLabel label = nodeFactory
                            .newVisibilityLabel(IObjCASTVisibilityLabel.v_public);
                    setRange(label, t.getOffset(), t.getEndOffset());
                    return label;
                }
                break;
            case IObjCToken.t_AtPublic:
                if (declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                    IToken t = consume();
                    IObjCASTVisibilityLabel label = nodeFactory
                            .newVisibilityLabel(IObjCASTVisibilityLabel.v_public);
                    setRange(label, t.getOffset(), t.getEndOffset());
                    return label;
                }
                break;
            case IObjCToken.t_AtRequired:
                if (declOption.equals(ObjCDeclarationOptions.PROTOCOL_LIST)) {
                    IToken t = consume();
                    IObjCASTOptionalityLabel label = nodeFactory
                            .newOptionalityLabel(IObjCASTOptionalityLabel.v_required);
                    setRange(label, t.getOffset(), t.getEndOffset());
                    return label;
                }
                break;
            case IObjCToken.t_AtOptional:
                if (declOption.equals(ObjCDeclarationOptions.PROTOCOL_LIST)) {
                    IToken t = consume();
                    IObjCASTOptionalityLabel label = nodeFactory
                            .newOptionalityLabel(IObjCASTOptionalityLabel.v_optional);
                    setRange(label, t.getOffset(), t.getEndOffset());
                    return label;
                }
                break;
            case IObjCToken.t_AtDefs:
                if (declOption.equals(DeclarationOptions.C_MEMBER)) {
                    IToken start = consume();
                    consume(IToken.tLPAREN);
                    IASTName clsName = identifier();
                    IASTDeclaration layout = nodeFactory.newMemoryLayoutDeclaration(clsName);
                    IToken stop = consume(IToken.tRPAREN);
                    ((ASTNode) layout).setOffsetAndLength(start.getOffset(), stop.getEndOffset()
                            - start.getOffset());
                    return layout;
                }
                break;
        }

        return simpleDeclaration(declOption);
    }

    private final void declarationList(final IASTDeclarationListOwner tu, DeclarationOptions options,
            boolean upToBrace, String endTokenImage, int codeBranchNesting) {
        final boolean wasActive = isActiveCode();
        while (true) {
            final boolean ok = acceptInactiveCodeBoundary(codeBranchNesting);
            if (!ok) {
                if (!wasActive) {
                    return;
                }
                try {
                    skipInactiveCode();
                } catch (OffsetLimitReachedException e) {
                    return;
                }
                codeBranchNesting = Math.min(getCodeBranchNesting() + 1, codeBranchNesting);
                continue;
            }

            final boolean active = isActiveCode();
            IToken next = LAcatchEOF(1);
            if (next == null || next.getType() == IToken.tEOC) {
                return;
            }

            if (upToBrace && next.getType() == IToken.tRBRACE && active == wasActive) {
                return;
            }

            if (endTokenImage != null && next.getImage().equals(endTokenImage) && active == wasActive) {
                return;
            }

            final int offset = next.getOffset();
            declarationMark = next;
            next = null; // don't hold on to the token while parsing namespaces,
            // class bodies, etc.
            try {
                IASTDeclaration declaration = declaration(options);
                if (((ASTNode) declaration).getLength() == 0 && LTcatchEOF(1) != IToken.tEOC) {
                    declaration = skipProblemDeclaration(offset);
                }
                addDeclaration(tu, declaration, active);
            } catch (BacktrackException bt) {
                IASTDeclaration[] decls = problemDeclaration(offset, bt, options);
                for (IASTDeclaration declaration : decls) {
                    addDeclaration(tu, declaration, active);
                }
            } catch (EndOfFileException e) {
                IASTDeclaration declaration = skipProblemDeclaration(offset);
                addDeclaration(tu, declaration, active);
                if (!e.endsInactiveCode()) {
                    break;
                }
            } finally {
                declarationMark = null;
            }
        }
    }

    protected IASTDeclarator declarator(DeclarationOptions option) throws EndOfFileException,
            BacktrackException {
        final int startingOffset = LA(1).getOffset();
        int endOffset = startingOffset;

        List<IASTPointerOperator> pointerOps = new ArrayList<IASTPointerOperator>(
                DEFAULT_POINTEROPS_LIST_SIZE);
        consumePointerOperators(pointerOps);
        if (!pointerOps.isEmpty()) {
            endOffset = calculateEndOffset(pointerOps.get(pointerOps.size() - 1));
        }

        if (option instanceof ObjCDeclarationOptions) {
            ObjCDeclarationOptions options = (ObjCDeclarationOptions) option;
            final IToken beforeConsume = mark();
            if (beforeConsume.getType() == IToken.tRPAREN
                    && (options.fIsInterface || options.fIsImplementation || options.fIsMethodParameter || options.fIsProtocol)) {
                consume();
                final IToken token = LA(1);
                final int sOffset = token.getOffset();
                int eOffset = sOffset;
                if (token.getType() == IToken.tIDENTIFIER) {
                    final IASTName declaratorName = identifier();
                    eOffset = calculateEndOffset(declaratorName);
                    return declarator(pointerOps, declaratorName, sOffset, eOffset, option);
                }
                backup(beforeConsume);
            }
        }

        // Accept __attribute__ or __declspec between pointer operators and
        // declarator.
        __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);

        // Look for identifier or nested declarator
        final int lt1 = LT(1);
        if (lt1 == IToken.tIDENTIFIER) {
            if (option.fRequireAbstract) {
                throwBacktrack(LA(1));
            }

            final IASTName declaratorName = identifier();
            endOffset = calculateEndOffset(declaratorName);
            return declarator(pointerOps, declaratorName, null, startingOffset, endOffset, option);
        }

        if (lt1 == IToken.tLPAREN) {
            IASTDeclarator cand1 = null;
            IToken cand1End = null;
            // try an abstract function declarator
            if (option.fAllowAbstract) {
                final IToken mark = mark();
                try {
                    cand1 = declarator(pointerOps, nodeFactory.newName(), null, startingOffset, endOffset,
                            option);
                    if (option.fRequireAbstract) {
                        return cand1;
                    }

                    cand1End = LA(1);
                } catch (BacktrackException e) {
                }
                backup(mark);
            }
            // try a nested declarator
            try {
                consume();
                if (LT(1) == IToken.tRPAREN) {
                    throwBacktrack(LA(1));
                }

                final IASTDeclarator nested = declarator(option);
                endOffset = consume(IToken.tRPAREN).getEndOffset();
                final IASTDeclarator cand2 = declarator(pointerOps, null, nested, startingOffset, endOffset,
                        option);
                if (cand1 == null || cand1End == null) {
                    return cand2;
                }
                final IToken cand2End = LA(1);
                if (cand1End == cand2End) {
                    ObjCASTAmbiguousDeclarator result = new ObjCASTAmbiguousDeclarator(cand1, cand2);
                    ((ASTNode) result).setOffsetAndLength((ASTNode) cand1);
                    return result;
                }
                // use the longer variant
                if (cand1End.getOffset() < cand2End.getOffset()) {
                    return cand2;
                }

            } catch (BacktrackException e) {
                if (cand1 == null) {
                    throw e;
                }
            }
            backup(cand1End);
            return cand1;
        }

        // try abstract declarator
        if (!option.fAllowAbstract) {
            throwBacktrack(LA(1));
        }
        return declarator(pointerOps, nodeFactory.newName(), null, startingOffset, endOffset, option);
    }

    private IASTDeclarator declarator(final List<IASTPointerOperator> pointerOps,
            final IASTName declaratorName, final IASTDeclarator nestedDeclarator, final int startingOffset,
            int endOffset, final DeclarationOptions option) throws EndOfFileException, BacktrackException {
        IASTDeclarator result = null;
        int lt1;
        loop: while (true) {
            lt1 = LTcatchEOF(1);
            switch (lt1) {
                case IToken.tLPAREN:
                    result = functionDeclarator(isAbstract(declaratorName, nestedDeclarator) ? DeclarationOptions.PARAMETER
                            : DeclarationOptions.C_PARAMETER_NON_ABSTRACT);
                    setDeclaratorID(result, declaratorName, nestedDeclarator);
                    break loop;

                case IToken.tLBRACKET:
                    result = arrayDeclarator();
                    setDeclaratorID(result, declaratorName, nestedDeclarator);
                    break loop;

                case IToken.tCOLON:
                    if (!option.fAllowBitField) {
                        throwBacktrack(LA(1));
                    }

                    result = bitFieldDeclarator();
                    setDeclaratorID(result, declaratorName, nestedDeclarator);
                    break loop;

                case IGCCToken.t__attribute__: // if __attribute__ is after a
                    // declarator
                    if (!supportAttributeSpecifiers) {
                        throwBacktrack(LA(1));
                    }
                    __attribute_decl_seq(true, supportDeclspecSpecifiers);
                    break;
                case IGCCToken.t__declspec:
                    if (!supportDeclspecSpecifiers) {
                        throwBacktrack(LA(1));
                    }
                    __attribute_decl_seq(supportAttributeSpecifiers, true);
                    break;
                default:
                    break loop;
            }
        }
        if (lt1 != 0) {
            __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);
        }

        if (result == null) {
            result = nodeFactory.newDeclarator(null);
            setDeclaratorID(result, declaratorName, nestedDeclarator);
        } else {
            endOffset = calculateEndOffset(result);
        }

        if (lt1 != 0 && LT(1) == IToken.t_asm) { // asm labels bug 226121
            consume();
            endOffset = asmExpression(null).getEndOffset();

            __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);
        }

        for (IASTPointerOperator po : pointerOps) {
            result.addPointerOperator(po);
        }

        ((ASTNode) result).setOffsetAndLength(startingOffset, endOffset - startingOffset);
        return result;
    }

    private IASTDeclarator declarator(final List<IASTPointerOperator> pointerOps,
            final IASTName declaratorName, final int startingOffset, int endOffset,
            final DeclarationOptions option) throws EndOfFileException, BacktrackException {
        IASTDeclarator result = null;
        int lt1;
        loop: while (true) {
            lt1 = LTcatchEOF(1);
            switch (lt1) {
                case IToken.tSEMI:
                    if (option instanceof ObjCDeclarationOptions
                            && ((ObjCDeclarationOptions) option).fIsMethodParameter) {
                        result = nodeFactory.newDeclarator(declaratorName);
                        ((ASTNode) result).setOffsetAndLength(startingOffset, endOffset - startingOffset);
                    } else {
                        result = nodeFactory.newMethodDeclarator(declaratorName);
                        if (option instanceof ObjCDeclarationOptions
                                && ((ObjCDeclarationOptions) option).fIsProtocol) {
                            ((IObjCASTMethodDeclarator) result).setProtocolMethod(true);
                        }
                        ((ASTNode) result).setOffsetAndLength(startingOffset, endOffset - startingOffset);
                    }
                    break loop;
                case IToken.tCOLON:
                    if (option instanceof ObjCDeclarationOptions) {
                        if (((ObjCDeclarationOptions) option).fIsMethodParameter) {
                            result = nodeFactory.newDeclarator(declaratorName);
                        } else if (((ObjCDeclarationOptions) option).fIsImplementation) {
                            result = methodDeclarator(ObjCDeclarationOptions.IMPLEMENTATION_METHOD_PARAMETER,
                                    declaratorName);
                        } else if (((ObjCDeclarationOptions) option).fIsInterface) {
                            result = methodDeclarator(ObjCDeclarationOptions.INTERFACE_METHOD_PARAMETER,
                                    declaratorName);
                        } else if (((ObjCDeclarationOptions) option).fIsProtocol) {
                            result = methodDeclarator(ObjCDeclarationOptions.PROTOCOL_METHOD_PARAMETER,
                                    declaratorName);
                        }
                    }

                    break loop;
                case IToken.tIDENTIFIER:
                    if (option instanceof ObjCDeclarationOptions
                            && ((ObjCDeclarationOptions) option).fIsMethodParameter) {
                        result = nodeFactory.newDeclarator(declaratorName);
                        ((ASTNode) result).setOffsetAndLength((ASTNode) declaratorName);
                    }
                    break loop;
                case IToken.tLBRACE:
                    if (option instanceof ObjCDeclarationOptions) {
                        if (((ObjCDeclarationOptions) option).fIsMethodParameter) {
                            result = nodeFactory.newDeclarator(declaratorName);
                        } else if (((ObjCDeclarationOptions) option).fIsImplementation) {
                            result = nodeFactory.newFunctionDeclarator(declaratorName);
                        }
                    }
                    break loop;
                default:
                    break loop;
            }
        }
        if (result == null) {
            throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, endOffset, 0), declaratorName);
        } else {
            endOffset = calculateEndOffset(result);
            for (IASTPointerOperator po : pointerOps) {
                result.addPointerOperator(po);
            }
            ((ASTNode) result).setOffsetAndLength(startingOffset, endOffset - startingOffset);
        }
        return result;
    }

    @Override
    protected IASTDeclSpecifier declSpecifierSeq(final DeclarationOptions declOption)
            throws BacktrackException, EndOfFileException, FoundDeclaratorException,
            FoundAggregateInitializer {

        int offset = LA(1).getOffset();
        int endOffset = offset;
        int storageClass = IASTDeclSpecifier.sc_unspecified;
        int simpleType = IASTSimpleDeclSpecifier.t_unspecified;
        int options = 0;
        int isLong = 0;

        IASTName identifier = null;
        IASTDeclSpecifier result = null;
        IASTExpression typeofExpression = null;
        IASTProblem problem = null;

        boolean encounteredRawType = false;
        boolean encounteredTypename = false;

        declSpecifiers: for (;;) {
            final int lt1 = LTcatchEOF(1);
            switch (lt1) {
                case 0: // eof
                    break declSpecifiers;
                case IObjCToken.t_AtImplementation:
                    if (!declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                        result = implementationDeclSpecifierSeq(declOption);
                        return result;
                    }
                    break declSpecifiers;
                case IObjCToken.t_AtProtocol:
                    if (!declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                        result = protocolDeclSpecifierSeq(declOption);
                        return result;
                    }
                    break declSpecifiers;
                case IObjCToken.t_AtInterface:
                    if (!declOption.equals(ObjCDeclarationOptions.INSTANCE_VARIABLES)) {
                        result = interfaceDeclSpecifierSeq(declOption);
                        return result;
                    }
                    break declSpecifiers;
                case IToken.tPLUS:
                    endOffset = consume().getEndOffset();
                    if (declOption.equals(ObjCDeclarationOptions.IMPLEMENTATION_LIST)) {
                        result = classMethodDefinition(offset, endOffset);
                        return result;
                    } else if (declOption.equals(ObjCDeclarationOptions.INTERFACE_LIST)) {
                        result = classMethodDeclaration(offset, endOffset);
                        return result;
                    } else if (declOption.equals(ObjCDeclarationOptions.PROTOCOL_LIST)) {
                        result = classMethodDeclaration(offset, endOffset);
                        return result;
                    }
                    break declSpecifiers;
                case IToken.tMINUS:
                    endOffset = consume().getEndOffset();
                    if (declOption.equals(ObjCDeclarationOptions.IMPLEMENTATION_LIST)) {
                        result = instanceMethodDefinition(offset, endOffset);
                        return result;
                    } else if (declOption.equals(ObjCDeclarationOptions.INTERFACE_LIST)) {
                        result = instanceMethodDeclaration(offset, endOffset);
                        return result;
                    } else if (declOption.equals(ObjCDeclarationOptions.PROTOCOL_LIST)) {
                        result = instanceMethodDeclaration(offset, endOffset);
                        return result;
                    }
                    break declSpecifiers;
                // storage class specifiers
                case IToken.t_auto:
                    storageClass = IASTDeclSpecifier.sc_auto;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_register:
                    storageClass = IASTDeclSpecifier.sc_register;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_static:
                    storageClass = IASTDeclSpecifier.sc_static;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_extern:
                    storageClass = IASTDeclSpecifier.sc_extern;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_typedef:
                    storageClass = IASTDeclSpecifier.sc_typedef;
                    endOffset = consume().getEndOffset();
                    break;

                // Function Specifier
                case IToken.t_inline:
                    options |= INLINE;
                    endOffset = consume().getEndOffset();
                    break;

                // Type Qualifiers
                case IToken.t_const:
                    options |= CONST;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_volatile:
                    options |= VOLATILE;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_restrict:
                    options |= RESTRICT;
                    endOffset = consume().getEndOffset();
                    break;

                // Type Specifiers
                case IToken.t_void:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IASTSimpleDeclSpecifier.t_void;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_char:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IASTSimpleDeclSpecifier.t_char;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_short:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    options |= SHORT;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_int:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IASTSimpleDeclSpecifier.t_int;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_long:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    isLong++;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_float:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IASTSimpleDeclSpecifier.t_float;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_double:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IASTSimpleDeclSpecifier.t_double;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_signed:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    options |= SIGNED;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t_unsigned:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    options |= UNSIGNED;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t__Bool:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    simpleType = IObjCASTSimpleDeclSpecifier.t_Bool;
                    encounteredRawType = true;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t__Complex:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    options |= COMPLEX;
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.t__Imaginary:
                    if (encounteredTypename) {
                        break declSpecifiers;
                    }
                    options |= IMAGINARY;
                    endOffset = consume().getEndOffset();
                    break;

                case IToken.tIDENTIFIER:
                    if (parsingClassDeclarationList && expectClassDeclaration) {
                        result = elaboratedTypeSpecifier(LT(1) == IObjCToken.t_AtClass);
                        int nlt1 = LT(1);
                        parsingClassDeclarationList = (nlt1 == IToken.tSEMI) ? false : true;
                        expectClassDeclaration = (parsingClassDeclarationList && (nlt1 == IToken.tCOMMA)) ? true
                                : false;
                        endOffset = calculateEndOffset(result);
                        encounteredTypename = true;
                        break declSpecifiers;
                    } else if (parsingProtocolDeclSpecList) {

                    }
                    //$FALL-THROUGH$
                case IToken.tCOMPLETION:
                case IToken.tEOC:
                    if (encounteredTypename || encounteredRawType) {
                        break declSpecifiers;
                    }

                    try {
                        if (endOffset != offset || declOption.fAllowEmptySpecifier) {
                            lookAheadForDeclarator(declOption);
                        }
                    } catch (FoundAggregateInitializer e) {
                        e.fDeclSpec = buildSimpleDeclSpec(storageClass, simpleType, options, isLong,
                                typeofExpression, offset, endOffset);
                        throw e;
                    } catch (FoundDeclaratorException e) {
                        e.declSpec = buildSimpleDeclSpec(storageClass, simpleType, options, isLong,
                                typeofExpression, offset, endOffset);

                        IToken mark = mark();
                        try {
                            final IASTName id = identifier(); // for the
                            // specifier
                            final IASTDeclarator altDtor = initDeclarator(declOption);
                            if (LA(1) == e.currToken) {
                                e.altDeclarator = altDtor;
                                e.altSpec = buildNamedTypeSpecifier(id, storageClass, options, offset,
                                        calculateEndOffset(id));
                            }
                        } catch (FoundAggregateInitializer lie) {
                            lie.fDeclSpec = e.declSpec;
                            throw lie;
                        } catch (BacktrackException bt) {
                        } finally {
                            backup(mark);
                        }
                        throw e;
                    }
                    identifier = identifier();
                    endOffset = calculateEndOffset(identifier);
                    encounteredTypename = true;
                    break;
                case IToken.t_struct:
                case IToken.t_union:
                    if (encounteredTypename || encounteredRawType) {
                        break declSpecifiers;
                    }
                    try {
                        result = structOrUnionSpecifier();
                    } catch (BacktrackException bt) {
                        result = elaboratedTypeSpecifier(true);
                    }
                    endOffset = calculateEndOffset(result);
                    encounteredTypename = true;
                    break;
                case IToken.t_enum:
                    if (encounteredTypename || encounteredRawType) {
                        break declSpecifiers;
                    }
                    try {
                        result = enumSpecifier();
                    } catch (BacktrackException bt) {
                        if (bt.getNodeBeforeProblem() instanceof IASTDeclSpecifier) {
                            result = (IASTDeclSpecifier) bt.getNodeBeforeProblem();
                            problem = bt.getProblem();
                            break declSpecifiers;
                        } else {
                            result = elaboratedTypeSpecifier(true);
                        }
                    }
                    endOffset = calculateEndOffset(result);
                    encounteredTypename = true;
                    break;
                case IObjCToken.t_AtClass:
                    if (encounteredTypename || encounteredRawType) {
                        break declSpecifiers;
                    }
                    result = elaboratedTypeSpecifier(LT(1) == IObjCToken.t_AtClass);
                    int nlt1 = LT(1);
                    parsingClassDeclarationList = (nlt1 == IToken.tSEMI) ? false : true;
                    expectClassDeclaration = (parsingClassDeclarationList && (nlt1 == IToken.tCOMMA)) ? true
                            : false;
                    offset = ((ASTNode) result).getOffset();
                    endOffset = calculateEndOffset(result);
                    encounteredTypename = true;
                    break declSpecifiers;
                case IGCCToken.t__attribute__: // if __attribute__ is after the
                    // declSpec
                    if (!supportAttributeSpecifiers) {
                        throwBacktrack(LA(1));
                    }
                    __attribute_decl_seq(true, false);
                    break;
                case IGCCToken.t__declspec: // __declspec precedes the
                    // identifier
                    if (identifier != null || !supportDeclspecSpecifiers) {
                        throwBacktrack(LA(1));
                    }
                    __attribute_decl_seq(false, true);
                    break;

                case IGCCToken.t_typeof:
                    if (encounteredRawType || encounteredTypename) {
                        throwBacktrack(LA(1));
                    }

                    final boolean wasInBinary = inBinaryExpression;
                    try {
                        inBinaryExpression = false;
                        typeofExpression = parseTypeidInParenthesisOrUnaryExpression(false, consume()
                                .getOffset(), IGNUASTTypeIdExpression.op_typeof,
                                IGNUASTUnaryExpression.op_typeof);
                    } finally {
                        inBinaryExpression = wasInBinary;
                    }
                    encounteredTypename = true;
                    endOffset = calculateEndOffset(typeofExpression);
                    break;

                case IToken.tLT:
                    if (encounteredRawType || encounteredTypename) {
                        throwBacktrack(LA(1));
                    }
                    consume();
                    break;

                default:
                    if (lt1 >= IExtensionToken.t__otherDeclSpecModifierFirst
                            && lt1 <= IExtensionToken.t__otherDeclSpecModifierLast) {
                        handleOtherDeclSpecModifier();
                        endOffset = LA(1).getOffset();
                        break;
                    }
                    break declSpecifiers;
            }

            if (encounteredRawType && encounteredTypename) {
                throwBacktrack(LA(1));
            }
        }

        // check for empty specification
        if (!encounteredRawType && !encounteredTypename && LT(1) != IToken.tEOC
                && !declOption.fAllowEmptySpecifier) {
            if (offset == endOffset) {
                throwBacktrack(LA(1));
            }
        }

        if (result != null) {
            configureDeclSpec(result, storageClass, options);
            if ((options & RESTRICT) != 0) {
                if (result instanceof IObjCASTCompositeTypeSpecifier) {
                    ((IObjCASTCompositeTypeSpecifier) result).setRestrict(true);
                } else if (result instanceof ObjCASTEnumerationSpecifier) {
                    ((ObjCASTEnumerationSpecifier) result).setRestrict(true);
                } else if (result instanceof ObjCASTElaboratedTypeSpecifier) {
                    ((ObjCASTElaboratedTypeSpecifier) result).setRestrict(true);
                }
            }
            ((ASTNode) result).setOffsetAndLength(offset, endOffset - offset);
            if (problem != null) {
                throwBacktrack(problem, result);
            }

            return result;
        }

        if (identifier != null) {
            return buildNamedTypeSpecifier(identifier, storageClass, options, offset, endOffset);
        }

        return buildSimpleDeclSpec(storageClass, simpleType, options, isLong, typeofExpression, offset,
                endOffset);
    }

    private void delimitedDeclarationListByImage(final IASTDeclarationListOwner decl, int offset,
            ObjCDeclarationOptions opts, final String startImage, final String stopImage)
            throws BacktrackException, EndOfFileException {
        int codeBranchNesting = getCodeBranchNesting();

        if (startImage != null) {
            final IToken s1 = consume();
            if (!s1.getImage().equals(startImage)) {
                throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, getEndOffset(), 0), decl);
            }
        }

        declarationList(decl, opts, false, stopImage, codeBranchNesting);

        final IToken la1 = LAcatchEOF(1);
        if (stopImage == null || la1.getImage().equals(stopImage)) {
            int endOffset = la1.getEndOffset();
            setRange(decl, offset, endOffset);
            return;
        }

        final int lt1 = la1.getType();
        final int endOffset = getEndOffset();
        setRange(decl, offset, endOffset);
        if (lt1 == IToken.tEOC || (lt1 == 0 && decl instanceof IASTCompositeTypeSpecifier)) {
            return;
        }
        throwBacktrack(createProblem(IProblem.SYNTAX_ERROR, endOffset, 0), decl);
    }

    private List<? extends IObjCASTDesignator> designatorList() throws EndOfFileException, BacktrackException {
        final int lt1 = LT(1);
        if (lt1 == IToken.tDOT || lt1 == IToken.tLBRACKET) {
            List<IObjCASTDesignator> designatorList = null;
            while (true) {
                switch (LT(1)) {
                    case IToken.tDOT:
                        int offset = consume().getOffset();
                        IASTName n = identifier();
                        IObjCASTFieldDesignator fieldDesignator = nodeFactory.newFieldDesignator(n);
                        setRange(fieldDesignator, offset, calculateEndOffset(n));
                        if (designatorList == null) {
                            designatorList = new ArrayList<IObjCASTDesignator>(DEFAULT_DESIGNATOR_LIST_SIZE);
                        }
                        designatorList.add(fieldDesignator);
                        break;

                    case IToken.tLBRACKET:
                        offset = consume().getOffset();
                        IASTExpression constantExpression = expression();
                        if (supportGCCStyleDesignators && LT(1) == IToken.tELLIPSIS) {
                            consume(IToken.tELLIPSIS);
                            IASTExpression constantExpression2 = expression();
                            int lastOffset = consume(IToken.tRBRACKET).getEndOffset();
                            IObjCGCCASTArrayRangeDesignator designator = nodeFactory
                                    .newArrayRangeDesignatorGCC(constantExpression, constantExpression2);
                            setRange(designator, offset, lastOffset);
                            if (designatorList == null) {
                                designatorList = new ArrayList<IObjCASTDesignator>(
                                        DEFAULT_DESIGNATOR_LIST_SIZE);
                            }
                            designatorList.add(designator);
                        } else {
                            int lastOffset = consume(IToken.tRBRACKET).getEndOffset();
                            IObjCASTArrayDesignator designator = nodeFactory
                                    .newArrayDesignator(constantExpression);
                            setRange(designator, offset, lastOffset);
                            if (designatorList == null) {
                                designatorList = new ArrayList<IObjCASTDesignator>(
                                        DEFAULT_DESIGNATOR_LIST_SIZE);
                            }
                            designatorList.add(designator);
                        }
                        break;

                    default:
                        return designatorList;
                }
            }
        }

        // fix for 84176: if reach identifier and it's not a designator then
        // return empty designator list
        if (supportGCCStyleDesignators && lt1 == IToken.tIDENTIFIER && LT(2) == IToken.tCOLON) {
            int offset = LA(1).getOffset();
            IASTName n = identifier();
            int lastOffset = consume(IToken.tCOLON).getEndOffset();
            IObjCASTFieldDesignator designator = nodeFactory.newFieldDesignator(n);
            setRange(designator, offset, lastOffset);
            return Collections.singletonList(designator);
        }

        return null;
    }

    protected IASTElaboratedTypeSpecifier elaboratedTypeSpecifier(boolean consumeFirstToken)
            throws BacktrackException, EndOfFileException {
        // this is an elaborated class specifier
        int eck = 0;
        if (consumeFirstToken) {
            currentClassElaboratedToken = consume();
            switch (currentClassElaboratedToken.getType()) {
                case IToken.t_struct:
                    eck = IASTElaboratedTypeSpecifier.k_struct;
                    // if __attribute__ or __declspec occurs after
                    // struct/union/class and
                    // before the identifier
                    __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);
                    break;
                case IToken.t_union:
                    eck = IASTElaboratedTypeSpecifier.k_union;
                    // if __attribute__ or __declspec occurs after
                    // struct/union/class and
                    // before the identifier
                    __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);
                    break;
                case IToken.t_enum:
                    eck = IASTElaboratedTypeSpecifier.k_enum;
                    // if __attribute__ or __declspec occurs after
                    // struct/union/class and
                    // before the identifier
                    __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);
                    break;
                case IObjCToken.t_AtClass:
                    eck = IObjCASTElaboratedTypeSpecifier.k_class;
                    break;
                case IObjCToken.t_AtProtocol:
                    eck = IObjCASTElaboratedTypeSpecifier.k_protocol;
                    break;
                default:
                    backup(currentClassElaboratedToken);
                    IToken t = currentClassElaboratedToken;
                    currentClassElaboratedToken = null;
                    throwBacktrack(t);
            }
        } else {
            /* "@class x, y, z;" is valid */
            eck = IObjCASTElaboratedTypeSpecifier.k_class;
        }

        IASTName name = identifier();
        IASTElaboratedTypeSpecifier result = nodeFactory.newElaboratedTypeSpecifier(eck, name);
        ((ASTNode) result).setOffsetAndLength(((ASTNode) name));
        return result;
    }

    private IASTStatement finallyBlockCompoundStatement() throws EndOfFileException, BacktrackException {
        consume(IObjCToken.t_AtFinally);
        if (mode == ParserMode.QUICK_PARSE || mode == ParserMode.STRUCTURAL_PARSE || !isActiveCode()) {
            int offset = LA(1).getOffset();
            IToken last = skipOverCompoundStatement();
            IASTCompoundStatement cs = nodeFactory.newCompoundStatement();
            setRange(cs, offset, last.getEndOffset());
            return cs;
        } else if (mode == ParserMode.COMPLETION_PARSE || mode == ParserMode.SELECTION_PARSE) {
            if (scanner.isOnTopContext()) {
                return compoundStatement();
            }
            int offset = LA(1).getOffset();
            IToken last = skipOverCompoundStatement();
            IASTCompoundStatement cs = nodeFactory.newCompoundStatement();
            setRange(cs, offset, last.getEndOffset());
            return cs;
        }
        return compoundStatement();
    }

    private IASTDeclarator functionDeclarator(DeclarationOptions paramOption) throws EndOfFileException,
            BacktrackException {
        IToken last = consume(IToken.tLPAREN);
        int startOffset = last.getOffset();

        // check for K&R C parameters (0 means it's not K&R C)
        if (fPreventKnrCheck == 0 && supportKnRC) {
            fPreventKnrCheck++;
            try {
                final int numKnRCParms = countKnRCParms();
                if (numKnRCParms > 0) { // KnR C parameters were found
                    IASTName[] parmNames = new IASTName[numKnRCParms];
                    IASTDeclaration[] parmDeclarations = new IASTDeclaration[numKnRCParms];

                    boolean seenParameter = false;
                    for (int i = 0; i <= parmNames.length; i++) {
                        switch (LT(1)) {
                            case IToken.tCOMMA:
                                last = consume();
                                parmNames[i] = identifier();
                                seenParameter = true;
                                break;
                            case IToken.tIDENTIFIER:
                                if (seenParameter) {
                                    throwBacktrack(startOffset, last.getEndOffset() - startOffset);
                                }

                                parmNames[i] = identifier();
                                seenParameter = true;
                                break;
                            case IToken.tRPAREN:
                                last = consume();
                                break;
                            default:
                                break;
                        }
                    }

                    // now that the parameter names are parsed, parse the
                    // parameter declarations
                    // count for parameter declarations <= count for parameter
                    // names.
                    int endOffset = last.getEndOffset();
                    for (int i = 0; i < numKnRCParms && LT(1) != IToken.tLBRACE; i++) {
                        try {
                            IASTDeclaration decl = simpleDeclaration(DeclarationOptions.LOCAL);
                            IASTSimpleDeclaration ok = checkKnrParameterDeclaration(decl, parmNames);
                            if (ok != null) {
                                parmDeclarations[i] = ok;
                                endOffset = calculateEndOffset(ok);
                            } else {
                                final ASTNode node = (ASTNode) decl;
                                parmDeclarations[i] = createKnRCProblemDeclaration(node.getOffset(), node
                                        .getLength());
                                endOffset = calculateEndOffset(node);
                            }
                        } catch (BacktrackException b) {
                            parmDeclarations[i] = createKnRCProblemDeclaration(b.getOffset(), b.getLength());
                            endOffset = b.getOffset() + b.getLength();
                        }
                    }

                    parmDeclarations = (IASTDeclaration[]) ArrayUtil.removeNulls(IASTDeclaration.class,
                            parmDeclarations);
                    IObjCASTKnRFunctionDeclarator functionDecltor = nodeFactory.newKnRFunctionDeclarator(
                            parmNames, parmDeclarations);
                    ((ASTNode) functionDecltor).setOffsetAndLength(startOffset, endOffset - startOffset);
                    return functionDecltor;
                }
            } finally {
                fPreventKnrCheck--;
            }
        }

        boolean seenParameter = false;
        boolean encounteredVarArgs = false;
        List<IASTParameterDeclaration> parameters = null;
        int endOffset = last.getEndOffset();

        paramLoop: while (true) {
            switch (LT(1)) {
                case IToken.tRPAREN:
                case IToken.tEOC:
                    endOffset = consume().getEndOffset();
                    break paramLoop;
                case IToken.tELLIPSIS:
                    endOffset = consume().getEndOffset();
                    encounteredVarArgs = true;
                    break;
                case IToken.tCOMMA:
                    endOffset = consume().getEndOffset();
                    seenParameter = false;
                    break;
                default:
                    if (seenParameter) {
                        throwBacktrack(startOffset, endOffset - startOffset);
                    }

                    IASTParameterDeclaration pd = parameterDeclaration(paramOption);
                    endOffset = calculateEndOffset(pd);
                    if (parameters == null) {
                        parameters = new ArrayList<IASTParameterDeclaration>(DEFAULT_PARAMETERS_LIST_SIZE);
                    }
                    parameters.add(pd);
                    seenParameter = true;
                    break;
            }
        }
        IASTStandardFunctionDeclarator fc = nodeFactory.newFunctionDeclarator(null);
        fc.setVarArgs(encounteredVarArgs);
        if (parameters != null) {
            for (IASTParameterDeclaration pd : parameters) {
                fc.addParameterDeclaration(pd);
            }
        }
        ((ASTNode) fc).setOffsetAndLength(startOffset, endOffset - startOffset);
        return fc;
    }

    private IASTDeclaration functionDefinition(int firstOffset, IASTDeclSpecifier declSpec,
            IASTDeclarator[] declarators) throws BacktrackException, EndOfFileException {
        if (declarators.length != 1) {
            throwBacktrack(firstOffset, LA(1).getEndOffset());
        }

        final IASTDeclarator outerDtor = declarators[0];
        final IASTDeclarator fdtor = ASTQueries.findTypeRelevantDeclarator(outerDtor);
        if (fdtor instanceof IASTFunctionDeclarator == false) {
            throwBacktrack(firstOffset, LA(1).getEndOffset() - firstOffset);
        }

        IASTFunctionDefinition funcDefinition = nodeFactory.newFunctionDefinition(declSpec,
                (IASTFunctionDeclarator) fdtor, null);

        try {
            IASTStatement s = handleFunctionBody();
            funcDefinition.setBody(s);
            ((ASTNode) funcDefinition).setOffsetAndLength(firstOffset, calculateEndOffset(s) - firstOffset);

            return funcDefinition;
        } catch (BacktrackException bt) {
            final IASTNode n = bt.getNodeBeforeProblem();
            if (n instanceof IASTCompoundStatement) {
                funcDefinition.setBody((IASTCompoundStatement) n);
                ((ASTNode) funcDefinition).setOffsetAndLength(firstOffset, calculateEndOffset(n)
                        - firstOffset);
                throwBacktrack(bt.getProblem(), funcDefinition);
            }
            throw bt;
        }
    }

    @Override
    protected IASTTranslationUnit getTranslationUnit() {
        return translationUnit;
    }

    @Override
    protected IASTName identifier() throws EndOfFileException, BacktrackException {
        final IToken t = LA(1);
        IASTName n;
        switch (t.getType()) {
            case IToken.tIDENTIFIER:
                consume();
                n = nodeFactory.newName(t.getCharImage());
                break;

            case IToken.tCOMPLETION:
            case IToken.tEOC:
                consume();
                n = nodeFactory.newName(t.getCharImage());
                createCompletionNode(t).addName(n);
                return n;

            default:
                throw backtrack;
        }

        setRange(n, t.getOffset(), t.getEndOffset());
        return n;
    }

    public IASTDeclSpecifier implementationDeclSpecifierSeq(final DeclarationOptions declOption)
            throws EndOfFileException, BacktrackException {
        IToken impl = consume();
        final int startOff = impl.getOffset();
        switch (LT(1)) {
            case IToken.tIDENTIFIER:
                IObjCASTCompositeTypeSpecifier declSpecifier;
                if (LT(2) == IToken.tLPAREN) {
                    declSpecifier = categoryImplementation();
                } else {
                    declSpecifier = classImplementation();
                }
                int endOff = ((ASTNode) declSpecifier).getLength() + ((ASTNode) declSpecifier).getOffset();
                ((ASTNode) declSpecifier).setOffsetAndLength(startOff, endOff - startOff);
                return declSpecifier;
            default:
                throwBacktrack(LA(1));
                return null;
        }
    }

    private void implementationDefinitionList(IObjCASTCompositeTypeSpecifier astSpecifier)
            throws BacktrackException, EndOfFileException {
        delimitedDeclarationListByImage(astSpecifier, 0, ObjCDeclarationOptions.IMPLEMENTATION_LIST, null,
                "@end");//$NON-NLS-1$
    }

    @Override
    protected IASTDeclarator initDeclarator(final DeclarationOptions option) throws EndOfFileException,
            BacktrackException, FoundAggregateInitializer {
        IASTDeclarator d = declarator(option);

        if (LTcatchEOF(1) == IToken.tASSIGN && LT(2) == IToken.tLBRACE) {
            throw new FoundAggregateInitializer(d);
        }

        IASTInitializer i = optionalCInitializer();
        if (i != null) {
            d.setInitializer(i);
            ((ASTNode) d).setLength(calculateEndOffset(i) - ((ASTNode) d).getOffset());
        }
        return d;
    }

    private IASTDeclSpecifier instanceMethodDeclaration(int offset, int endOffset) throws EndOfFileException,
            BacktrackException {
        return buildMethodSimpleDeclSpec(IASTDeclSpecifier.sc_unspecified, offset, endOffset);
    }

    private IASTDeclSpecifier instanceMethodDefinition(int offset, int endOffset) throws EndOfFileException,
            BacktrackException {
        return buildMethodSimpleDeclSpec(IASTDeclSpecifier.sc_unspecified, offset, endOffset);
    }

    public void instanceVariables(IObjCASTCompositeTypeSpecifier decl) throws EndOfFileException,
            BacktrackException {
        declarationListInBraces(decl, LA(1).getOffset(), ObjCDeclarationOptions.INSTANCE_VARIABLES);
    }

    public void interfaceDeclarationList(IObjCASTCompositeTypeSpecifier decl,
            final ObjCDeclarationOptions options) throws EndOfFileException, BacktrackException {
        delimitedDeclarationListByImage(decl, 0, options, null, "@end");//$NON-NLS-1$
    }

    public IASTDeclSpecifier interfaceDeclSpecifierSeq(final DeclarationOptions declOption)
            throws EndOfFileException, BacktrackException {
        IObjCASTCompositeTypeSpecifier declSpecifier;
        IToken intf = consume();
        final int startOff = intf.getOffset();
        switch (LT(1)) {
            case IToken.tIDENTIFIER:
                if (LT(2) == IToken.tLPAREN) {
                    declSpecifier = categoryInterface();
                } else {
                    declSpecifier = classInterface();
                }
                int endOff = ((ASTNode) declSpecifier).getLength() + ((ASTNode) declSpecifier).getOffset();
                ((ASTNode) declSpecifier).setOffsetAndLength(startOff, endOff - startOff);
                return declSpecifier;
            default:
                throwBacktrack(LA(1));
                return null;
        }
    }

    private boolean isAbstract(IASTName declaratorName, IASTDeclarator nestedDeclarator) {
        nestedDeclarator = ASTQueries.findInnermostDeclarator(nestedDeclarator);
        if (nestedDeclarator != null) {
            declaratorName = nestedDeclarator.getName();
        }
        return declaratorName == null || declaratorName.toCharArray().length == 0;
    }

    private IASTDeclarator methodDeclarator(final DeclarationOptions option, IASTName declaratorName)
            throws EndOfFileException, BacktrackException {
        boolean encounteredVarArgs = false;
        IASTName sName = declaratorName.copy();
        List<IASTParameterDeclaration> parameters = null;
        final int startOffset = ((ASTNode) declaratorName).getOffset();
        int endOffset = startOffset + ((ASTNode) declaratorName).getLength();
        IASTParameterDeclaration param = null;
        paramLoop: while (true) {
            IToken la1 = LA(1);
            switch (la1.getType()) {
                case IToken.tLBRACE:
                    break paramLoop;
                case IToken.tSEMI:
                    break paramLoop;
                case IToken.tEOC:
                    consume();
                    break paramLoop;
                case IToken.tCOMMA:
                    consume();
                    if (LT(1) == IToken.tELLIPSIS) {
                        consume();
                        encounteredVarArgs = true;
                        endOffset = la1.getOffset() + la1.getLength();
                    } else {
                        if (parameters == null) {
                            parameters = new ArrayList<IASTParameterDeclaration>(DEFAULT_PARAMETERS_LIST_SIZE);
                        }
                        param = parameterDeclaration(option);
                        parameters.add(param);
                        endOffset = ((ASTNode) param).getOffset() + ((ASTNode) param).getLength();
                    }
                    break paramLoop;
                case IToken.tIDENTIFIER:
                    sName = identifier();
                    //$FALL-THROUGH$
                case IToken.tCOLON:
                    if (parameters == null) {
                        parameters = new ArrayList<IASTParameterDeclaration>(DEFAULT_PARAMETERS_LIST_SIZE);
                    }
                    param = methodParameterDeclaration(sName, ((ASTNode) sName).getOffset(), option);
                    parameters.add(param);
                    endOffset = ((ASTNode) param).getOffset() + ((ASTNode) param).getLength();
                    break;
            }
        }
        IASTStandardFunctionDeclarator fc = nodeFactory.newMethodDeclarator(null);
        if (option instanceof ObjCDeclarationOptions && ((ObjCDeclarationOptions) option).fIsProtocol) {
            ((IObjCASTMethodDeclarator) fc).setProtocolMethod(true);
        }
        fc.setVarArgs(encounteredVarArgs);
        if (parameters != null) {
            for (IASTParameterDeclaration pd : parameters) {
                fc.addParameterDeclaration(pd);
            }
        }
        ((ASTNode) fc).setOffsetAndLength(startOffset, endOffset - startOffset);
        return fc;
    }

    private IASTParameterDeclaration methodParameterDeclaration(IASTName selectorName, int startOff,
            DeclarationOptions option) throws BacktrackException, EndOfFileException {

        consume(IToken.tCOLON);

        final IToken current = LA(1);
        IASTDeclSpecifier declSpec = null;
        IASTDeclarator declarator = null;
        IASTDeclSpecifier altDeclSpec = null;
        IASTDeclarator altDeclarator = null;

        try {
            if (current.getType() == IToken.tLPAREN) {
                consume(IToken.tLPAREN);
                declSpec = declSpecifierSeq(option);
            } else {
                declSpec = declSpecifierSeq(option);
            }
            declarator = declarator(option);
        } catch (FoundDeclaratorException fd) {
            declSpec = fd.declSpec;
            declarator = fd.declarator;
            altDeclSpec = fd.altSpec;
            altDeclarator = fd.altDeclarator;
            backup(fd.currToken);
        } catch (FoundAggregateInitializer lie) {
            declSpec = lie.fDeclSpec;
            declarator = lie.fDeclarator;
        }

        final int length = figureEndOffset(declSpec, declarator) - startOff;
        IASTParameterDeclaration result = nodeFactory.newMethodParameterDeclaration(selectorName, declSpec,
                declarator);
        ((ASTNode) result).setOffsetAndLength(startOff, length);

        if (altDeclarator != null && altDeclSpec != null) {
            IASTParameterDeclaration alt = nodeFactory.newMethodParameterDeclaration(selectorName,
                    altDeclSpec, altDeclarator);
            ((ASTNode) alt).setOffsetAndLength(startOff, length);
            // order is important, prefer alternative over the declarator found
            // via the lookahead.
            result = new ObjCASTAmbiguousParameterDeclaration(alt, result);
            ((ASTNode) result).setOffsetAndLength((ASTNode) alt);
        }
        return result;
    }

    @Override
    protected void nullifyTranslationUnit() {
        translationUnit = null;
    }

    protected IASTInitializer optionalCInitializer() throws EndOfFileException, BacktrackException {
        if (LTcatchEOF(1) == IToken.tASSIGN) {
            consume();
            return cInitializerClause(false);
        }
        return null;
    }

    protected IASTParameterDeclaration parameterDeclaration(DeclarationOptions option)
            throws BacktrackException, EndOfFileException {
        final IToken current = LA(1);
        int startingOffset = current.getOffset();
        if (current.getType() == IToken.tLBRACKET && supportParameterInfoBlock) {
            skipBrackets(IToken.tLBRACKET, IToken.tRBRACKET);
        }

        IASTDeclSpecifier declSpec = null;
        IASTDeclarator declarator = null;
        IASTDeclSpecifier altDeclSpec = null;
        IASTDeclarator altDeclarator = null;

        try {
            fPreventKnrCheck++;
            declSpec = declSpecifierSeq(option);
            declarator = declarator(option);
        } catch (FoundDeclaratorException fd) {
            declSpec = fd.declSpec;
            declarator = fd.declarator;
            altDeclSpec = fd.altSpec;
            altDeclarator = fd.altDeclarator;
            backup(fd.currToken);
        } catch (FoundAggregateInitializer lie) {
            declSpec = lie.fDeclSpec;
            declarator = lie.fDeclarator;
        } finally {
            fPreventKnrCheck--;
        }

        final int length = figureEndOffset(declSpec, declarator) - startingOffset;
        IASTParameterDeclaration result = nodeFactory.newParameterDeclaration(declSpec, declarator);
        ((ASTNode) result).setOffsetAndLength(startingOffset, length);
        if (altDeclarator != null && altDeclSpec != null) {
            IASTParameterDeclaration alt = nodeFactory.newParameterDeclaration(altDeclSpec, altDeclarator);
            ((ASTNode) alt).setOffsetAndLength(startingOffset, length);
            // order is important, prefer alternative over the declarator found
            // via the lookahead.
            result = new ObjCASTAmbiguousParameterDeclaration(alt, result);
            ((ASTNode) result).setOffsetAndLength((ASTNode) alt);
        }
        return result;
    }

    protected IASTStatement parseForStatement() throws EndOfFileException, BacktrackException {
        int startOffset;
        startOffset = consume().getOffset();
        consume(IToken.tLPAREN);
        IASTStatement init = forInitStatement(DeclarationOptions.LOCAL);
        IASTExpression for_condition = null;
        switch (LT(1)) {
            case IToken.tSEMI:
            case IToken.tEOC:
                break;
            default:
                for_condition = condition(false);
        }
        switch (LT(1)) {
            case IToken.tSEMI:
                consume();
                break;
            case IToken.tEOC:
                break;
            default:
                throw backtrack;
        }
        IASTExpression iterationExpression = null;
        switch (LT(1)) {
            case IToken.tRPAREN:
            case IToken.tEOC:
                break;
            default:
                iterationExpression = expression();
        }
        switch (LT(1)) {
            case IToken.tRPAREN:
                consume();
                break;
            case IToken.tEOC:
                break;
            default:
                throw backtrack;
        }

        IASTForStatement for_statement = nodeFactory.newForStatement(init, for_condition,
                iterationExpression, null);
        if (LT(1) != IToken.tEOC) {
            IASTStatement for_body = statement();
            ((ASTNode) for_statement).setOffsetAndLength(startOffset, calculateEndOffset(for_body)
                    - startOffset);
            for_statement.setBody(for_body);
        }
        return for_statement;
    }

    protected IASTStatement parseIfStatement() throws EndOfFileException, BacktrackException {
        IASTIfStatement result = null;
        IASTIfStatement if_statement = null;
        int start = LA(1).getOffset();
        if_loop: while (true) {
            int so = consume(IToken.t_if).getOffset();
            consume(IToken.tLPAREN);
            // condition
            IASTExpression condition = condition(true);
            if (LT(1) == IToken.tEOC) {
                // Completing in the condition
                IASTIfStatement new_if = nodeFactory.newIfStatement(condition, null, null);

                if (if_statement != null) {
                    if_statement.setElseClause(new_if);
                }
                return result != null ? result : new_if;
            }
            consume(IToken.tRPAREN);

            IASTStatement thenClause = statement();
            IASTIfStatement new_if_statement = nodeFactory.newIfStatement(null, null, null);
            ((ASTNode) new_if_statement).setOffset(so);
            if (condition != null) // shouldn't be possible but failure in
            // condition() makes it so
            {
                new_if_statement.setConditionExpression(condition);
            }
            if (thenClause != null) {
                new_if_statement.setThenClause(thenClause);
                ((ASTNode) new_if_statement).setLength(calculateEndOffset(thenClause)
                        - ((ASTNode) new_if_statement).getOffset());
            }
            if (LT(1) == IToken.t_else) {
                consume();
                if (LT(1) == IToken.t_if) {
                    // an else if, don't recurse, just loop and do another if

                    if (if_statement != null) {
                        if_statement.setElseClause(new_if_statement);
                        ((ASTNode) if_statement).setLength(calculateEndOffset(new_if_statement)
                                - ((ASTNode) if_statement).getOffset());
                    }
                    if (result == null && if_statement != null) {
                        result = if_statement;
                    }
                    if (result == null) {
                        result = new_if_statement;
                    }

                    if_statement = new_if_statement;
                    continue if_loop;
                }
                IASTStatement elseStatement = statement();
                new_if_statement.setElseClause(elseStatement);
                if (if_statement != null) {
                    if_statement.setElseClause(new_if_statement);
                    ((ASTNode) if_statement).setLength(calculateEndOffset(new_if_statement)
                            - ((ASTNode) if_statement).getOffset());
                } else {
                    if (result == null) {
                        result = new_if_statement;
                    }
                    if_statement = new_if_statement;
                }
            } else {
                if (thenClause != null) {
                    ((ASTNode) new_if_statement).setLength(calculateEndOffset(thenClause) - start);
                }
                if (if_statement != null) {
                    if_statement.setElseClause(new_if_statement);
                    ((ASTNode) new_if_statement).setLength(calculateEndOffset(new_if_statement) - start);
                }
                if (result == null && if_statement != null) {
                    result = if_statement;
                }
                if (result == null) {
                    result = new_if_statement;
                }

                if_statement = new_if_statement;
            }
            break if_loop;
        }

        reconcileLengths(result);
        return result;
    }

    protected IASTStatement parseSwitchStatement() throws EndOfFileException, BacktrackException {
        int startOffset;
        startOffset = consume().getOffset();
        consume(IToken.tLPAREN);
        IASTExpression switch_condition = condition(true);
        switch (LT(1)) {
            case IToken.tRPAREN:
                consume();
                break;
            case IToken.tEOC:
                break;
            default:
                throwBacktrack(LA(1));
        }

        IASTStatement switch_body = parseSwitchBody();
        IASTSwitchStatement switch_statement = nodeFactory.newSwitchStatement(switch_condition, switch_body);
        ((ASTNode) switch_statement).setOffsetAndLength(startOffset,
                (switch_body != null ? calculateEndOffset(switch_body) : LA(1).getEndOffset()) - startOffset);
        return switch_statement;
    }

    protected IASTStatement parseTryStatement() throws EndOfFileException, BacktrackException {
        int startO = consume().getOffset();
        IASTStatement tryBlock = compoundStatement();
        List<IObjCASTCatchHandler> catchHandlers = new ArrayList<IObjCASTCatchHandler>(
                DEFAULT_CATCH_HANDLER_LIST_SIZE);
        catchHandlerSequence(catchHandlers);
        IASTStatement finBlock = null;
        if (LT(1) == IObjCToken.t_AtFinally) {
            finBlock = finallyBlockCompoundStatement();
        }
        IObjCASTTryBlockStatement tryStatement = nodeFactory.newTryBlockStatement(tryBlock);
        ((ASTNode) tryStatement).setOffset(startO);
        for (int i = 0; i < catchHandlers.size(); ++i) {
            IObjCASTCatchHandler handler = catchHandlers.get(i);
            tryStatement.addCatchHandler(handler);
            ((ASTNode) tryStatement).setLength(calculateEndOffset(handler) - startO);
        }
        if (finBlock != null) {
            tryStatement.setFinallyBlock(finBlock);
            ((ASTNode) tryStatement).setLength(calculateEndOffset(finBlock) - startO);
        }
        return tryStatement;
    }

    @Override
    protected IASTExpression pmExpression() throws BacktrackException, EndOfFileException {
        return castExpression();
    }

    protected IASTExpression postfixExpression() throws EndOfFileException, BacktrackException {
        IASTExpression firstExpression = null;
        switch (LT(1)) {
            case IToken.tLPAREN:
                // ( type-name ) { initializer-list }
                // ( type-name ) { initializer-list , }
                IToken m = mark();
                try {
                    int offset = consume().getOffset();
                    IASTTypeId t = typeId(DeclarationOptions.TYPEID);
                    if (t != null) {
                        consume(IToken.tRPAREN);
                        if (LT(1) == IToken.tLBRACE) {
                            IASTInitializer i = cInitializerClause(false);
                            firstExpression = nodeFactory.newTypeIdInitializerExpression(t, i);
                            setRange(firstExpression, offset, calculateEndOffset(i));
                            break;
                        }
                    }
                } catch (BacktrackException bt) {
                }
                backup(m);
                firstExpression = primaryExpression();
                break;

            default:
                firstExpression = primaryExpression();
                break;
        }

        IASTExpression secondExpression = null;
        for (;;) {
            switch (LT(1)) {
                case IToken.tLBRACKET:
                    // array access
                    consume();
                    secondExpression = expression();
                    int last;
                    switch (LT(1)) {
                        case IToken.tRBRACKET:
                            last = consume().getEndOffset();
                            break;
                        case IToken.tEOC:
                            last = Integer.MAX_VALUE;
                            break;
                        default:
                            throw backtrack;
                    }

                    IASTArraySubscriptExpression s = nodeFactory.newArraySubscriptExpression(firstExpression,
                            secondExpression);
                    ((ASTNode) s).setOffsetAndLength(((ASTNode) firstExpression).getOffset(), last
                            - ((ASTNode) firstExpression).getOffset());
                    firstExpression = s;
                    break;
                case IToken.tLPAREN:
                    // function call
                    consume();
                    if (LT(1) != IToken.tRPAREN) {
                        secondExpression = expression();
                    }
                    if (LT(1) == IToken.tRPAREN) {
                        last = consume().getEndOffset();
                    } else {
                        // must be EOC
                        last = Integer.MAX_VALUE;
                    }
                    IASTFunctionCallExpression f = nodeFactory.newFunctionCallExpression(firstExpression,
                            secondExpression);
                    ((ASTNode) f).setOffsetAndLength(((ASTNode) firstExpression).getOffset(), last
                            - ((ASTNode) firstExpression).getOffset());
                    firstExpression = f;
                    break;
                case IToken.tINCR:
                    int offset = consume().getEndOffset();
                    firstExpression = buildUnaryExpression(IASTUnaryExpression.op_postFixIncr,
                            firstExpression, ((ASTNode) firstExpression).getOffset(), offset);
                    break;
                case IToken.tDECR:
                    offset = consume().getEndOffset();
                    firstExpression = buildUnaryExpression(IASTUnaryExpression.op_postFixDecr,
                            firstExpression, ((ASTNode) firstExpression).getOffset(), offset);
                    break;
                case IToken.tDOT:
                    // member access
                    IToken dot = consume();
                    IASTName name = identifier();
                    if (name == null) {
                        throwBacktrack(((ASTNode) firstExpression).getOffset(), ((ASTNode) firstExpression)
                                .getLength()
                                + dot.getLength());
                    }
                    IASTFieldReference result = nodeFactory.newFieldReference(name, firstExpression);
                    result.setIsPointerDereference(false);
                    ((ASTNode) result).setOffsetAndLength(((ASTNode) firstExpression).getOffset(),
                            calculateEndOffset(name) - ((ASTNode) firstExpression).getOffset());
                    firstExpression = result;
                    break;
                case IToken.tARROW:
                    // member access
                    IToken arrow = consume();
                    name = identifier();
                    if (name == null) {
                        throwBacktrack(((ASTNode) firstExpression).getOffset(), ((ASTNode) firstExpression)
                                .getLength()
                                + arrow.getLength());
                    }
                    result = nodeFactory.newFieldReference(name, firstExpression);
                    result.setIsPointerDereference(true);
                    ((ASTNode) result).setOffsetAndLength(((ASTNode) firstExpression).getOffset(),
                            calculateEndOffset(name) - ((ASTNode) firstExpression).getOffset());
                    firstExpression = result;
                    break;
                default:
                    return firstExpression;
            }
        }
    }

    @Override
    protected IASTExpression primaryExpression() throws EndOfFileException, BacktrackException {
        IToken t = null;
        IASTName name = null;
        IASTLiteralExpression literalExpression = null;
        switch (LT(1)) {
            // TO DO: we need more literals...
            case IToken.tINTEGER:
                t = consume();
                literalExpression = nodeFactory.newLiteralExpression(
                        IASTLiteralExpression.lk_integer_constant, t.getImage());
                ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getEndOffset()
                        - t.getOffset());
                return literalExpression;
            case IToken.tFLOATINGPT:
                t = consume();
                literalExpression = nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_float_constant,
                        t.getImage());
                ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getEndOffset()
                        - t.getOffset());
                return literalExpression;
            case IToken.tSTRING:
            case IToken.tLSTRING:
            case IToken.tUTF16STRING:
            case IToken.tUTF32STRING:
                t = consume();
                literalExpression = nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_string_literal,
                        t.getImage());
                ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getEndOffset()
                        - t.getOffset());
                return literalExpression;
            case IToken.tCHAR:
            case IToken.tLCHAR:
            case IToken.tUTF16CHAR:
            case IToken.tUTF32CHAR:
                t = consume();
                literalExpression = nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_char_constant,
                        t.getImage());
                ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getLength());
                return literalExpression;
            case IToken.tLPAREN:
                if (supportStatementsInExpressions && LT(2) == IToken.tLBRACE) {
                    return compoundStatementExpression();
                }
                t = consume();
                IASTExpression lhs = expression();
                int finalOffset = 0;
                switch (LT(1)) {
                    case IToken.tRPAREN:
                    case IToken.tEOC:
                        finalOffset = consume().getEndOffset();
                        break;
                    default:
                        throwBacktrack(LA(1));
                }
                return buildUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, lhs, t.getOffset(),
                        finalOffset);
            case IToken.tLBRACKET:
                return buildMessageExpression();
            case IToken.tIDENTIFIER:
                t = LA(1);
                if (AT.equals(t.getImage()) && LT(2) == IToken.tSTRING) {
                    consume();
                    t = consume();
                    literalExpression = nodeFactory.newLiteralExpression(
                            IASTLiteralExpression.lk_string_literal, t.getImage());
                    ((ASTNode) literalExpression).setOffsetAndLength(t.getOffset(), t.getEndOffset()
                            - t.getOffset());
                    return literalExpression;
                }
                //$FALL-THROUGH$
            case IToken.tCOMPLETION:
            case IToken.tEOC:
                int startingOffset = LA(1).getOffset();
                name = identifier();
                IASTIdExpression idExpression = nodeFactory.newIdExpression(name);
                ((ASTNode) idExpression).setOffsetAndLength((ASTNode) name);
                return idExpression;
            default:
                IToken la = LA(1);
                startingOffset = la.getOffset();
                throwBacktrack(startingOffset, la.getLength());
                return null;
        }

    }

    private IObjCASTCompositeTypeSpecifier protocolDeclaration() throws EndOfFileException,
            BacktrackException {
        IASTName name = identifier();
        IObjCASTCompositeTypeSpecifier astSpecifier = nodeFactory.newCompositeTypeSpecifier(
                IObjCASTCompositeTypeSpecifier.k_protocol, name);
        if (LT(1) == IToken.tLT) {
            protocolReferenceList(astSpecifier);
        }
        interfaceDeclarationList(astSpecifier, ObjCDeclarationOptions.PROTOCOL_LIST);
        return astSpecifier;
    }

    public IASTDeclSpecifier protocolDeclSpecifierSeq(final DeclarationOptions declOption)
            throws EndOfFileException, BacktrackException {
        IASTDeclSpecifier spec;
        IToken mark = mark();
        switch (LTcatchEOF(2)) {
            case IToken.tIDENTIFIER:
                try {
                    IToken proto = consume();
                    spec = protocolDeclaration();
                    ((ASTNode) spec).setOffsetAndLength(proto.getOffset(), ((ASTNode) spec).getLength());
                } catch (BacktrackException e) {
                    backup(mark);
                    spec = elaboratedTypeSpecifier(true);
                }
                return spec;
            default:
                throwBacktrack(mark);
                break;
        }
        return null;
    }

    public void protocolReferenceList(IObjCASTCompositeTypeSpecifier astSpec) throws EndOfFileException,
            BacktrackException {
        consume(IToken.tLT);
        boolean expectComma = false;
        IToken la1 = LA(1);
        while (la1.getType() != IToken.tGT) {
            switch (la1.getType()) {
                case IToken.tIDENTIFIER:
                    if (!expectComma) {
                        IToken protocolName = consume();
                        IASTName pname = nodeFactory.newName(protocolName.getCharImage());
                        ((ASTNode) pname).setOffsetAndLength(protocolName.getOffset(), protocolName
                                .getLength());
                        IObjCASTBaseSpecifier baseProto = nodeFactory.newBaseSpecifier(pname, true);
                        ((ASTNode) baseProto).setOffsetAndLength((ASTNode) pname);
                        astSpec.addBaseSpecifier(baseProto);
                        expectComma = true;
                    } else {
                        throwBacktrack(la1);
                    }
                    break;
                case IToken.tCOMMA:
                    if (expectComma) {
                        consume();
                        expectComma = false;
                        break;
                    }
                    //$FALL-THROUGH$
                default:
                    throwBacktrack(la1);
                    break;
            }
            la1 = LA(1);
        }
        consume(IToken.tGT);
    }

    private IASTName selectorId() throws EndOfFileException, BacktrackException {
        StringBuilder builder = new StringBuilder();
        IASTName pName;
        int offset = LA(1).getOffset();
        int endOffset = offset;
        WhileLoop: while (true) {
            int lt1 = LT(1);
            switch (lt1) {
                case IToken.tIDENTIFIER:
                    pName = identifier();
                    endOffset = ((ASTNode) pName).getOffset() + ((ASTNode) pName).getLength();
                    builder.append(((ObjCASTName) pName).getSimpleID());
                    break;
                case IToken.tCOLON:
                    IToken c = consume();
                    endOffset = c.getEndOffset();
                    builder.append(':');
                    break;
                default:
                    break WhileLoop;
            }
        }
        if (endOffset == offset) {
            throwBacktrack(LA(1));
        }
        IASTName name = nodeFactory.newName(builder.toString().toCharArray());
        ((ASTNode) name).setOffsetAndLength(offset, endOffset - offset);
        return name;
    }

    private void setDeclaratorID(IASTDeclarator declarator, IASTName declaratorName,
            IASTDeclarator nestedDeclarator) {
        if (nestedDeclarator != null) {
            declarator.setNestedDeclarator(nestedDeclarator);
            declarator.setName(nodeFactory.newName());
        } else {
            declarator.setName(declaratorName);
        }
    }

    @Override
    protected void setupTranslationUnit() throws DOMException {
        translationUnit = nodeFactory.newTranslationUnit();
        translationUnit.setIndex(index);

        // add built-in names to the scope
        if (builtinBindingsProvider != null) {
            IScope tuScope = translationUnit.getScope();

            IBinding[] bindings = builtinBindingsProvider.getBuiltinBindings(tuScope);
            for (IBinding binding : bindings) {
                ASTInternal.addBinding(tuScope, binding);
            }
        }
        if (translationUnit instanceof ASTTranslationUnit) {
            ((ASTTranslationUnit) translationUnit).setLocationResolver(scanner.getLocationResolver());
        }
    }

    private IASTDeclaration simpleDeclaration(final DeclarationOptions declOption) throws BacktrackException,
            EndOfFileException {
        if (LT(1) == IToken.tLBRACE) {
            throwBacktrack(LA(1));
        }

        final int firstOffset = LA(1).getOffset();
        int endOffset = firstOffset;
        boolean insertSemi = false;
        boolean parseDtors = true;

        IASTDeclSpecifier declSpec = null;
        IASTDeclarator dtor = null;
        IASTDeclSpecifier altDeclSpec = null;
        IASTDeclarator altDeclarator = null;
        IToken markBeforDtor = null;
        try {
            declSpec = declSpecifierSeq(declOption);
            int k = 0;
            if (declSpec instanceof IObjCASTCompositeTypeSpecifier) {
                k = ((IObjCASTCompositeTypeSpecifier) declSpec).getKey();
            } else if (declSpec instanceof IObjCASTElaboratedTypeSpecifier) {
                k = ((IObjCASTElaboratedTypeSpecifier) declSpec).getKind();
            }
            final int lt1 = LTcatchEOF(1);
            switch (lt1) {
                case 0: // eof
                case IToken.tEOC:
                case IToken.tSEMI:
                    parseDtors = false;
                    insertSemi = (lt1 == 0 && !(k == IObjCASTCompositeTypeSpecifier.k_category
                            || k == IObjCASTCompositeTypeSpecifier.k_class || k == IObjCASTCompositeTypeSpecifier.k_protocol));
                    if (lt1 == IToken.tSEMI) {
                        endOffset = consume().getEndOffset();
                    } else {
                        endOffset = calculateEndOffset(declSpec);
                    }
                    break;
                case IObjCToken.t_AtEnd:
                    parseDtors = false;
                    insertSemi = !(k == IObjCASTCompositeTypeSpecifier.k_category
                            || k == IObjCASTCompositeTypeSpecifier.k_class || k == IObjCASTCompositeTypeSpecifier.k_protocol);
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.tCOMMA:
                    if (declSpec instanceof IObjCASTElaboratedTypeSpecifier
                            && k == IObjCASTElaboratedTypeSpecifier.k_class) {
                        parseDtors = false;
                        insertSemi = false;
                        endOffset = consume().getEndOffset();
                        break;
                    }
                    //$FALL-THROUGH$
                default:
                    markBeforDtor = mark();
                    try {
                        dtor = initDeclarator(declOption);
                    } catch (BacktrackException e) {
                        backup(markBeforDtor);
                    } catch (EndOfFileException e) {
                        backup(markBeforDtor);
                    }
            }
        } catch (FoundAggregateInitializer lie) {
            if (declSpec == null) {
                declSpec = lie.fDeclSpec;
            }
            // scalability: don't keep references to tokens, initializer may be
            // large
            declarationMark = null;
            markBeforDtor = null;
            dtor = addInitializer(lie, declOption);
        } catch (FoundDeclaratorException e) {
            if (e.altSpec != null) {
                declSpec = e.altSpec;
                dtor = e.altDeclarator;
                altDeclSpec = e.declSpec;
                altDeclarator = e.declarator;
            } else {
                declSpec = e.declSpec;
                dtor = e.declarator;
            }
            backup(e.currToken);
        } catch (BacktrackException e) {
            IASTNode node = e.getNodeBeforeProblem();
            if (node instanceof IASTDeclSpecifier) {
                IASTSimpleDeclaration d = nodeFactory.newSimpleDeclaration((IASTDeclSpecifier) node);
                setRange(d, node);
                throwBacktrack(e.getProblem(), d);
            }
            throw e;
        }

        IASTDeclarator[] declarators = IASTDeclarator.EMPTY_DECLARATOR_ARRAY;
        if (parseDtors) {
            declarators = new IASTDeclarator[] { dtor };
            while (LTcatchEOF(1) == IToken.tCOMMA) {
                consume();
                try {
                    dtor = initDeclarator(declOption);
                } catch (FoundAggregateInitializer e) {
                    // scalability: don't keep references to tokens, initializer
                    // may be large
                    declarationMark = null;
                    markBeforDtor = null;
                    dtor = addInitializer(e, declOption);
                }
                declarators = (IASTDeclarator[]) ArrayUtil.append(IASTDeclarator.class, declarators, dtor);
            }
            declarators = (IASTDeclarator[]) ArrayUtil.removeNulls(IASTDeclarator.class, declarators);

            final int lt1 = LTcatchEOF(1);
            switch (lt1) {
                case IToken.tLBRACE:
                    return functionDefinition(firstOffset, declSpec, declarators);

                case IToken.tSEMI:
                    endOffset = consume().getEndOffset();
                    break;
                case IToken.tEOC:
                    endOffset = figureEndOffset(declSpec, declarators);
                    break;
                default:
                    if (declOption != DeclarationOptions.LOCAL) {
                        insertSemi = true;
                        if (markBeforDtor != null) {
                            endOffset = calculateEndOffset(declSpec);
                            if (firstOffset != endOffset
                                    && !isOnSameLine(endOffset, markBeforDtor.getOffset())) {
                                backup(markBeforDtor);
                                declarators = IASTDeclarator.EMPTY_DECLARATOR_ARRAY;
                                break;
                            }
                        }
                        endOffset = figureEndOffset(declSpec, declarators);
                        if (lt1 == 0) {
                            break;
                        }
                        if (firstOffset != endOffset) {
                            if (!isOnSameLine(endOffset, LA(1).getOffset())) {
                                break;
                            }
                            if (declarators.length == 1 && declarators[0] instanceof IASTFunctionDeclarator) {
                                break;
                            }
                        }
                    }
                    throwBacktrack(LA(1));
            }
        }

        // no function body
        IASTSimpleDeclaration simpleDeclaration = nodeFactory.newSimpleDeclaration(declSpec);
        for (IASTDeclarator declarator : declarators) {
            simpleDeclaration.addDeclarator(declarator);
        }

        setRange(simpleDeclaration, firstOffset, endOffset);
        if (altDeclSpec != null && altDeclarator != null) {
            simpleDeclaration = new ObjCASTAmbiguousSimpleDeclaration(simpleDeclaration, altDeclSpec,
                    altDeclarator);
            setRange(simpleDeclaration, firstOffset, endOffset);
        }

        if (insertSemi) {
            IASTProblem problem = createProblem(IProblem.SYNTAX_ERROR, endOffset, 0);
            throwBacktrack(problem, simpleDeclaration);
        }
        return simpleDeclaration;
    }

    private IASTSimpleDeclaration simpleSingleDeclaration(DeclarationOptions options)
            throws BacktrackException, EndOfFileException {
        final int startOffset = LA(1).getOffset();
        IASTDeclSpecifier declSpec = null;
        IASTDeclarator declarator;

        try {
            declSpec = declSpecifierSeq(options);
            declarator = initDeclarator(options);
        } catch (FoundDeclaratorException e) {
            declSpec = e.declSpec;
            declarator = e.declarator;
            backup(e.currToken);
        } catch (FoundAggregateInitializer lie) {
            if (declSpec == null) {
                declSpec = lie.fDeclSpec;
            }
            declarator = addInitializer(lie, options);
        }

        final int endOffset = figureEndOffset(declSpec, declarator);
        final IASTSimpleDeclaration decl = nodeFactory.newSimpleDeclaration(declSpec);
        decl.addDeclarator(declarator);
        ((ASTNode) decl).setOffsetAndLength(startOffset, endOffset - startOffset);

        return decl;
    }

    @Override
    protected IASTStatement statement() throws EndOfFileException, BacktrackException {
        switch (LT(1)) {
            // labeled statements
            case IToken.t_case:
                return parseCaseStatement();
            case IToken.t_default:
                return parseDefaultStatement();
                // compound statement
            case IToken.tLBRACE:
                return parseCompoundStatement();
                // selection statement
            case IToken.t_if:
                return parseIfStatement();
            case IToken.t_switch:
                return parseSwitchStatement();
                // iteration statements
            case IToken.t_while:
                return parseWhileStatement();
            case IToken.t_do:
                return parseDoStatement();
            case IToken.t_for:
                return parseForStatement();
                // jump statement
            case IToken.t_break:
                return parseBreakStatement();
            case IToken.t_continue:
                return parseContinueStatement();
            case IToken.t_return:
                return parseReturnStatement();
            case IToken.t_goto:
                return parseGotoStatement();
            case IToken.tSEMI:
                return parseNullStatement();
            case IObjCToken.t_AtTry:
                return parseTryStatement();
            default:
                // can be many things:
                // label
                if (LT(1) == IToken.tIDENTIFIER && LT(2) == IToken.tCOLON) {
                    return parseLabelStatement();
                }

                return parseDeclarationOrExpressionStatement(DeclarationOptions.LOCAL);
        }

    }

    /**
     * Parse a class/struct/union definition.
     * 
     * classSpecifier : classKey name (baseClause)? "{" (memberSpecification)*
     * "}"
     * 
     * @throws BacktrackException
     *             to request a backtrack
     */
    protected IObjCASTCompositeTypeSpecifier structOrUnionSpecifier() throws BacktrackException,
            EndOfFileException {
        int classKind = 0;
        IToken mark = mark();
        final int offset = mark.getOffset();

        // class key
        switch (LT(1)) {
            case IToken.t_struct:
                consume();
                classKind = IASTCompositeTypeSpecifier.k_struct;
                break;
            case IToken.t_union:
                consume();
                classKind = IASTCompositeTypeSpecifier.k_union;
                break;
            default:
                throwBacktrack(LA(1));
                return null; // line never reached, hint for the parser.
        }

        // if __attribute__ or __declspec occurs after struct/union/class and
        // before the identifier
        __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);

        // class name
        IASTName name = null;
        if (LT(1) == IToken.tIDENTIFIER) {
            name = identifier();
        }

        // if __attribute__ or __declspec occurs after struct/union/class
        // identifier and before the { or ;
        __attribute_decl_seq(supportAttributeSpecifiers, supportDeclspecSpecifiers);

        if (LT(1) != IToken.tLBRACE) {
            IToken errorPoint = LA(1);
            backup(mark);
            throwBacktrack(errorPoint);
        }

        if (name == null) {
            name = nodeFactory.newName();
        }
        IObjCASTCompositeTypeSpecifier result = nodeFactory.newCompositeTypeSpecifier(classKind, name);
        declarationListInBraces(result, offset, DeclarationOptions.C_MEMBER);
        return result;
    }

    @Override
    protected IASTTypeId typeId(DeclarationOptions option) throws EndOfFileException {
        if (!canBeTypeSpecifier()) {
            return null;
        }
        IToken mark = mark();
        int startingOffset = mark.getOffset();
        IASTDeclSpecifier declSpecifier = null;
        IASTDeclarator declarator = null;

        fPreventKnrCheck++;
        try {
            try {
                declSpecifier = declSpecifierSeq(option);
                declarator = declarator(option);
            } catch (FoundDeclaratorException e) {
                declSpecifier = e.declSpec;
                declarator = e.declarator;
                backup(e.currToken);
            } catch (FoundAggregateInitializer lie) {
                // type-ids have not compound initializers
                return null;
            }
        } catch (BacktrackException bt) {
            return null;
        } finally {
            fPreventKnrCheck--;
        }

        IASTTypeId result = nodeFactory.newTypeId(declSpecifier, declarator);
        ((ASTNode) result).setOffsetAndLength(startingOffset, figureEndOffset(declSpecifier, declarator)
                - startingOffset);
        return result;
    }

    @Override
    protected IASTExpression unaryExpression() throws EndOfFileException, BacktrackException {
        switch (LT(1)) {
            case IToken.tSTAR:
                return unarayExpression(IASTUnaryExpression.op_star);
            case IToken.tAMPER:
                return unarayExpression(IASTUnaryExpression.op_amper);
            case IToken.tPLUS:
                return unarayExpression(IASTUnaryExpression.op_plus);
            case IToken.tMINUS:
                return unarayExpression(IASTUnaryExpression.op_minus);
            case IToken.tNOT:
                return unarayExpression(IASTUnaryExpression.op_not);
            case IToken.tBITCOMPLEMENT:
                return unarayExpression(IASTUnaryExpression.op_tilde);
            case IToken.tINCR:
                return unarayExpression(IASTUnaryExpression.op_prefixIncr);
            case IToken.tDECR:
                return unarayExpression(IASTUnaryExpression.op_prefixDecr);
            case IToken.t_sizeof:
                return parseTypeidInParenthesisOrUnaryExpression(false, consume().getOffset(),
                        IASTTypeIdExpression.op_sizeof, IASTUnaryExpression.op_sizeof);
            case IGCCToken.t_typeof:
                return parseTypeidInParenthesisOrUnaryExpression(false, consume().getOffset(),
                        IASTTypeIdExpression.op_typeof, IASTUnaryExpression.op_typeof);
            case IGCCToken.t___alignof__:
                return parseTypeidInParenthesisOrUnaryExpression(false, consume().getOffset(),
                        IASTTypeIdExpression.op_alignof, IASTUnaryExpression.op_alignOf);
            case IObjCToken.t_AtThrow:
                return unarayExpression(IObjCASTUnaryExpression.op_AtThrow);
            case IObjCToken.t_AtSelector:
                return buildSelectorExpression();
            case IObjCToken.t_AtProtocol:
                return buildProtocolExpression();
            case IObjCToken.t_AtEncode:
                return buildEncodeExpression();
            default:
                return postfixExpression();
        }
    }

    @Override
    protected boolean verifyLookaheadDeclarator(DeclarationOptions option, IASTDeclarator dtor,
            IToken nextToken) {
        switch (nextToken.getType()) {
            case IToken.tCOMMA:
                return true;
            case IToken.tLBRACE:
                if (option == DeclarationOptions.GLOBAL || option == DeclarationOptions.C_MEMBER
                        || option == DeclarationOptions.FUNCTION_STYLE_ASM) {
                    if (ASTQueries.findTypeRelevantDeclarator(dtor) instanceof IASTFunctionDeclarator) {
                        return true;
                    }
                }
                break;
            case IToken.tSEMI:
                return option == DeclarationOptions.GLOBAL || option == DeclarationOptions.C_MEMBER
                        || option == DeclarationOptions.LOCAL;

            case IToken.tRPAREN:
                return option == DeclarationOptions.PARAMETER
                        || option == DeclarationOptions.C_PARAMETER_NON_ABSTRACT;
        }
        return false;
    }
}
