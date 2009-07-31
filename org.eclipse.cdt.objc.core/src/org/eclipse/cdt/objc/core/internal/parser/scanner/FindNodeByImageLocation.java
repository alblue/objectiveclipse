package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTVisitor;
import org.eclipse.cdt.core.dom.ast.cpp.CPPASTVisitor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNodeSpecification;

/**
 * Visitor to select nodes by image-location.
 * 
 * @since 5.0
 */
@SuppressWarnings("restriction")
public class FindNodeByImageLocation extends CPPASTVisitor implements ICASTVisitor {
    private final int fLength;
    private final ASTNodeSpecification<?> fNodeSpec;
    private final int fOffset;

    public FindNodeByImageLocation(int offset, int length, ASTNodeSpecification<?> nodeSpec) {
        fNodeSpec = nodeSpec;
        fOffset = offset;
        fLength = length;

        shouldVisitNames = true;
        shouldVisitDeclarations = true;

        shouldVisitInitializers = shouldVisitParameterDeclarations = shouldVisitDeclarators = shouldVisitDeclSpecifiers = shouldVisitDesignators = shouldVisitEnumerators = shouldVisitExpressions = shouldVisitStatements = shouldVisitTypeIds = shouldVisitEnumerators = shouldVisitBaseSpecifiers = shouldVisitNamespaces = shouldVisitTemplateParameters = shouldVisitTranslationUnit = !nodeSpec
                .requiresClass(IASTName.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.c.ICASTVisitor#leave(org.eclipse.cdt.core
     * .dom.ast.c.ICASTDesignator)
     */
    public int leave(ICASTDesignator designator) {
        return PROCESS_CONTINUE;
    }

    public int processNode(IASTNode node) {
        if (node instanceof ASTNode) {
            final ASTNode astNode = (ASTNode) node;
            if (astNode.getOffset() > fOffset + fLength
                    || astNode.getOffset() + astNode.getLength() < fOffset) {
                return PROCESS_SKIP;
            }

            if (fNodeSpec.isAcceptableNode(astNode)) {
                IASTImageLocation imageLocation = astNode.getImageLocation();
                if (imageLocation != null
                        && imageLocation.getLocationKind() == IASTImageLocation.ARGUMENT_TO_MACRO_EXPANSION) {
                    fNodeSpec.visit(astNode, imageLocation);
                }
            }
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        // use declarations to determine if the search has gone past the
        // offset (i.e. don't know the order the visitor visits the nodes)
        if (declaration instanceof ASTNode && ((ASTNode) declaration).getOffset() > fOffset + fLength) {
            return PROCESS_ABORT;
        }

        return processNode(declaration);
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        return processNode(declarator);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        return processNode(declSpec);
    }

    @Override
    public int visit(IASTEnumerator enumerator) {
        return processNode(enumerator);
    }

    @Override
    public int visit(IASTExpression expression) {
        return processNode(expression);
    }

    @Override
    public int visit(IASTInitializer initializer) {
        return processNode(initializer);
    }

    @Override
    public int visit(IASTName name) {
        if (name.toString() != null) {
            return processNode(name);
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        return processNode(parameterDeclaration);
    }

    @Override
    public int visit(IASTProblem problem) {
        return processNode(problem);
    }

    @Override
    public int visit(IASTStatement statement) {
        return processNode(statement);
    }

    @Override
    public int visit(IASTTranslationUnit tu) {
        return processNode(tu);
    }

    @Override
    public int visit(IASTTypeId typeId) {
        return processNode(typeId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
     * #processDesignator(org.eclipse.cdt.core.dom.ast.c.ICASTDesignator)
     */
    public int visit(ICASTDesignator designator) {
        return processNode(designator);
    }

    @Override
    public int visit(ICPPASTBaseSpecifier baseSpecifier) {
        return processNode(baseSpecifier);
    }

    @Override
    public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
        return processNode(namespaceDefinition);
    }

    @Override
    public int visit(ICPPASTTemplateParameter templateParameter) {
        return processNode(templateParameter);
    }
}