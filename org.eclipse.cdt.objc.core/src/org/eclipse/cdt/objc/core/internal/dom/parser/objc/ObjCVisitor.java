/*******************************************************************************
 * 
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Niefer (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems) Bryan Wilkinson (QNX)
 * Andrew Ferguson (Symbian)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemHolder;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IEnumerator;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.ILabel;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFileSet;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.core.parser.util.CharArrayObjectMap;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.IASTInternalScope;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayModifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTClassMemoryLayoutDeclaration;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTEnumerationSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFieldDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPointer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypedefNameSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCArrayType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCBasicType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCCompositeTypeScope;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCFunctionScope;
import org.eclipse.cdt.objc.core.dom.ast.objc.ObjCASTVisitor;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTSimpleDeclSpecifier;

/**
 * Collection of methods to find information in an AST.
 */
@SuppressWarnings("restriction")
public class ObjCVisitor extends ASTQueries {
    public static class CollectDeclarationsAction extends ObjCASTVisitor {
        private static final int DEFAULT_CHILDREN_LIST_SIZE = 8;

        IBinding binding = null;
        boolean compositeTypeDeclared = false;
        private IASTName[] declsFound = null;
        int numFound = 0;
        {
            shouldVisitDeclarators = true;
            shouldVisitDeclSpecifiers = true;
            shouldVisitEnumerators = true;
            shouldVisitStatements = true;
        }

        public CollectDeclarationsAction(IBinding binding) {
            declsFound = new IASTName[DEFAULT_CHILDREN_LIST_SIZE];
            this.binding = binding;
        }

        private void addName(IASTName name) {
            if (declsFound.length == numFound) // if the found array is full,
            // then double the array
            {
                IASTName[] old = declsFound;
                declsFound = new IASTName[old.length * 2];
                for (int j = 0; j < old.length; ++j) {
                    declsFound[j] = old[j];
                }
            }
            declsFound[numFound++] = name;
        }

        public IASTName[] getDeclarationNames() {
            return removeNullFromNames();
        }

        private IASTName[] removeNullFromNames() {
            if (declsFound[declsFound.length - 1] != null) { // if the last
                // element in the
                // list is not null
                // then return the
                // list
                return declsFound;
            } else if (declsFound[0] == null) { // if the first element in the
                // list is null, then return
                // empty list
                return new IASTName[0];
            }

            IASTName[] results = new IASTName[numFound];
            for (int i = 0; i < results.length; i++) {
                results[i] = declsFound[i];
            }

            return results;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processDeclarator(org.eclipse.cdt.core.dom.ast.IASTDeclarator)
         */
        @Override
        public int visit(IASTDeclarator declarator) {
            // GCC allows declarations in expressions, so we have to continue
            // from the
            // declarator in case there is something in the initializer
            // expression
            if (declarator == null || declarator.getName() == null
                    || declarator.getName().toCharArray().length == 0) {
                return PROCESS_CONTINUE;
            }

            // if the binding is something not declared in a declarator,
            // continue
            if (binding instanceof ICompositeType) {
                return PROCESS_CONTINUE;
            }
            if (binding instanceof IEnumeration) {
                return PROCESS_CONTINUE;
            }

            IASTNode parent = declarator.getParent();
            while (parent != null
                    && !(parent instanceof IASTDeclaration || parent instanceof IASTParameterDeclaration)) {
                parent = parent.getParent();
            }

            if (parent instanceof IASTDeclaration) {
                if (parent instanceof IASTFunctionDefinition) {
                    if (declarator.getName() != null && declarator.getName().resolveBinding() == binding) {
                        addName(declarator.getName());
                    }
                } else if (parent instanceof IASTSimpleDeclaration) {
                    // prototype parameter with no identifier isn't a
                    // declaration of the K&R C parameter
                    // if (binding instanceof CKnRParameter &&
                    // declarator.getName().toCharArray().length == 0)
                    // return PROCESS_CONTINUE;

                    if ((declarator.getName() != null && declarator.getName().resolveBinding() == binding)) {
                        addName(declarator.getName());
                    }
                }
            } else if (parent instanceof IASTParameterDeclaration) {
                if (declarator.getName() != null && declarator.getName().resolveBinding() == binding) {
                    addName(declarator.getName());
                }
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processDeclSpecifier(org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier)
         */
        @Override
        public int visit(IASTDeclSpecifier declSpec) {
            if (compositeTypeDeclared && declSpec instanceof IObjCASTTypedefNameSpecifier) {
                return PROCESS_CONTINUE;
            }

            // if the binding isn't declared in a decl spec, skip it
            if (!(binding instanceof ICompositeType) && !(binding instanceof IEnumeration)) {
                return PROCESS_CONTINUE;
            }

            if (binding instanceof ICompositeType && declSpec instanceof IASTCompositeTypeSpecifier) {
                if (((IASTCompositeTypeSpecifier) declSpec).getName().resolveBinding() == binding) {
                    compositeTypeDeclared = true;
                    addName(((IASTCompositeTypeSpecifier) declSpec).getName());
                }
            } else if (binding instanceof IEnumeration && declSpec instanceof IASTEnumerationSpecifier) {
                if (((IASTEnumerationSpecifier) declSpec).getName().resolveBinding() == binding) {
                    compositeTypeDeclared = true;
                    addName(((IASTEnumerationSpecifier) declSpec).getName());
                }
            } else if (declSpec instanceof IASTElaboratedTypeSpecifier) {
                if (compositeTypeDeclared) {
                    IASTNode parent = declSpec.getParent();
                    if (!(parent instanceof IASTSimpleDeclaration)
                            || ((IASTSimpleDeclaration) parent).getDeclarators().length > 0) {
                        return PROCESS_CONTINUE;
                    }
                }
                if (((IASTElaboratedTypeSpecifier) declSpec).getName().resolveBinding() == binding) {
                    compositeTypeDeclared = true;
                    addName(((IASTElaboratedTypeSpecifier) declSpec).getName());
                }
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #
         * processEnumerator(org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier
         * .IASTEnumerator)
         */
        @Override
        public int visit(IASTEnumerator enumerator) {
            if (binding instanceof IEnumerator && enumerator.getName().resolveBinding() == binding) {
                addName(enumerator.getName());
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processStatement(org.eclipse.cdt.core.dom.ast.IASTStatement)
         */
        @Override
        public int visit(IASTStatement statement) {
            if (statement instanceof IASTLabelStatement && binding instanceof ILabel) {
                if (((IASTLabelStatement) statement).getName().resolveBinding() == binding) {
                    addName(((IASTLabelStatement) statement).getName());
                }
                return PROCESS_SKIP;
            }

            return PROCESS_CONTINUE;
        }
    }

    public static class CollectProblemsAction extends ObjCASTVisitor {
        private static final int DEFAULT_CHILDREN_LIST_SIZE = 8;

        int numFound = 0;
        private IASTProblem[] problems = null;
        {
            shouldVisitDeclarations = true;
            shouldVisitExpressions = true;
            shouldVisitStatements = true;
            shouldVisitTypeIds = true;
        }

        public CollectProblemsAction() {
            problems = new IASTProblem[DEFAULT_CHILDREN_LIST_SIZE];
        }

        private void addProblem(IASTProblem problem) {
            if (problems.length == numFound) { // if the found array is full,
                // then double the array
                IASTProblem[] old = problems;
                problems = new IASTProblem[old.length * 2];
                for (int j = 0; j < old.length; ++j) {
                    problems[j] = old[j];
                }
            }
            problems[numFound++] = problem;
        }

        public IASTProblem[] getProblems() {
            return removeNullFromProblems();
        }

        private IASTProblem[] removeNullFromProblems() {
            if (problems[problems.length - 1] != null) { // if the last element
                // in the list is not
                // null then return the
                // list
                return problems;
            } else if (problems[0] == null) { // if the first element in the
                // list is null, then return empty
                // list
                return new IASTProblem[0];
            }

            IASTProblem[] results = new IASTProblem[numFound];
            for (int i = 0; i < results.length; i++) {
                results[i] = problems[i];
            }

            return results;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processDeclaration(org.eclipse.cdt.core.dom.ast.IASTDeclaration)
         */
        @Override
        public int visit(IASTDeclaration declaration) {
            if (declaration instanceof IASTProblemHolder) {
                addProblem(((IASTProblemHolder) declaration).getProblem());
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processExpression(org.eclipse.cdt.core.dom.ast.IASTExpression)
         */
        @Override
        public int visit(IASTExpression expression) {
            if (expression instanceof IASTProblemHolder) {
                addProblem(((IASTProblemHolder) expression).getProblem());
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processStatement(org.eclipse.cdt.core.dom.ast.IASTStatement)
         */
        @Override
        public int visit(IASTStatement statement) {
            if (statement instanceof IASTProblemHolder) {
                addProblem(((IASTProblemHolder) statement).getProblem());
            }

            return PROCESS_CONTINUE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.cdt.internal.core.dom.parser.c.CVisitor.CBaseVisitorAction
         * #processTypeId(org.eclipse.cdt.core.dom.ast.IASTTypeId)
         */
        @Override
        public int visit(IASTTypeId typeId) {
            if (typeId instanceof IASTProblemHolder) {
                addProblem(((IASTProblemHolder) typeId).getProblem());
            }

            return PROCESS_CONTINUE;
        }
    }

    public static class CollectReferencesAction extends ObjCASTVisitor {
        private static final int DEFAULT_LIST_SIZE = 8;
        private static final int KIND_LABEL = 1;
        private static final int KIND_OBJ_FN = 2;
        private static final int KIND_TYPE = 3;
        private final IBinding binding;

        private int idx = 0;
        private int kind;
        private IASTName[] refs;

        public CollectReferencesAction(IBinding binding) {
            this.binding = binding;
            refs = new IASTName[DEFAULT_LIST_SIZE];

            shouldVisitNames = true;
            if (binding instanceof ILabel) {
                kind = KIND_LABEL;
            } else if (binding instanceof ICompositeType || binding instanceof ITypedef
                    || binding instanceof IEnumeration) {
                kind = KIND_TYPE;
            } else {
                kind = KIND_OBJ_FN;
            }
        }

        public IASTName[] getReferences() {
            if (idx < refs.length) {
                IASTName[] temp = new IASTName[idx];
                System.arraycopy(refs, 0, temp, 0, idx);
                refs = temp;
            }
            return refs;
        }

        private boolean sameBinding(IBinding binding1, IBinding binding2) {
            if (binding1 == binding2) {
                return true;
            }
            if (binding1 != null && binding1.equals(binding2)) {
                return true;
            }
            return false;
        }

        @Override
        public int visit(IASTName name) {
            ASTNodeProperty prop = name.getPropertyInParent();
            switch (kind) {
                case KIND_LABEL:
                    if (prop == IASTGotoStatement.NAME) {
                        break;
                    }
                    return PROCESS_CONTINUE;
                case KIND_TYPE:
                    if (prop == IASTNamedTypeSpecifier.NAME) {
                        break;
                    } else if (prop == IASTElaboratedTypeSpecifier.TYPE_NAME) {
                        IASTNode p = name.getParent().getParent();
                        if (!(p instanceof IASTSimpleDeclaration)
                                || ((IASTSimpleDeclaration) p).getDeclarators().length > 0) {
                            break;
                        }
                    }
                    return PROCESS_CONTINUE;
                case KIND_OBJ_FN:
                    if (prop == IASTIdExpression.ID_NAME || prop == IASTFieldReference.FIELD_NAME
                            || prop == IObjCASTFieldDesignator.FIELD_NAME) {
                        break;
                    }
                    return PROCESS_CONTINUE;
            }

            if (CharArrayUtils.equals(name.toCharArray(), binding.getNameCharArray())) {
                if (sameBinding(name.resolveBinding(), binding)) {
                    if (refs.length == idx) {
                        IASTName[] temp = new IASTName[refs.length * 2];
                        System.arraycopy(refs, 0, temp, 0, refs.length);
                        refs = temp;
                    }
                    refs[idx++] = name;
                }
            }
            return PROCESS_CONTINUE;
        }
    }

    // definition lookup start loc
    protected static final int AT_BEGINNING = 1;
    protected static final int AT_NEXT = 2;
    public static final char[] EMPTY_CHAR_ARRAY = "".toCharArray(); //$NON-NLS-1$
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String PTRDIFF_T = "ptrdiff_t"; //$NON-NLS-1$
    private static final String SEL = "SEL"; //$NON-NLS-1$
    private static final String SIZE_T = "size_t"; //$NON-NLS-1$

    //    private static final String id = "id"; //$NON-NLS-1$
    //    private static final String Nil = "Nil"; //$NON-NLS-1$
    //    private static final String nil = "nil"; //$NON-NLS-1$
    //    private static final String Protocol = "Protocol"; //$NON-NLS-1$
    //    private static final String BOOL = "BOOL"; //$NON-NLS-1$

    protected static final ASTNodeProperty STRING_LOOKUP_PROPERTY = new ASTNodeProperty(
            "ObjCVisitor.STRING_LOOKUP_PROPERTY - STRING_LOOKUP"); //$NON-NLS-1$
    protected static final ASTNodeProperty STRING_LOOKUP_TAGS_PROPERTY = new ASTNodeProperty(
            "ObjCVisitor.STRING_LOOKUP_TAGS_PROPERTY - STRING_LOOKUP"); //$NON-NLS-1$ 

    /**
     * This is used to create a base IType corresponding to an IASTDeclarator
     * and the IASTDeclSpecifier. This method doesn't have any recursive
     * behaviour and is used as the foundation of the ITypes being created. The
     * parameter isParm is used to specify whether the declarator is a parameter
     * or not.
     * 
     * @param declSpec
     *            the IASTDeclSpecifier used to determine if the base type is a
     *            CQualifierType or not
     * @return the base IType
     */
    public static IType createBaseType(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IObjCGCCASTSimpleDeclSpecifier) {
            IASTExpression exp = ((IObjCGCCASTSimpleDeclSpecifier) declSpec).getTypeofExpression();
            if (exp != null) {
                return exp.getExpressionType();
            }
            return new ObjCBasicType((IObjCASTSimpleDeclSpecifier) declSpec);
        } else if (declSpec instanceof IObjCASTSimpleDeclSpecifier) {
            return new ObjCBasicType((IObjCASTSimpleDeclSpecifier) declSpec);
        }
        IBinding binding = null;
        IASTName name = null;
        if (declSpec instanceof IObjCASTTypedefNameSpecifier) {
            name = ((IObjCASTTypedefNameSpecifier) declSpec).getName();
        } else if (declSpec instanceof IASTElaboratedTypeSpecifier) {
            name = ((IASTElaboratedTypeSpecifier) declSpec).getName();
        } else if (declSpec instanceof IASTCompositeTypeSpecifier) {
            name = ((IASTCompositeTypeSpecifier) declSpec).getName();
        } else if (declSpec instanceof IASTEnumerationSpecifier) {
            name = ((IASTEnumerationSpecifier) declSpec).getName();
        } else {
            return new ProblemBinding(declSpec, IProblemBinding.SEMANTIC_NAME_NOT_FOUND, declSpec
                    .getRawSignature().toCharArray());
        }

        binding = name.resolveBinding();
        if (binding instanceof IType) {
            return (IType) binding;
        }

        if (binding != null) {
            return new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_TYPE, name.toCharArray());
        }
        return new ProblemBinding(name, IProblemBinding.SEMANTIC_NAME_NOT_FOUND, name.toCharArray());
    }

    private static IBinding createBinding(IASTDeclarator declarator) {
        IASTNode parent = declarator.getParent();
        while (parent instanceof IASTDeclarator) {
            parent = parent.getParent();
        }

        declarator = ASTQueries.findInnermostDeclarator(declarator);
        IASTDeclarator typeRelevant = ASTQueries.findTypeRelevantDeclarator(declarator);
        IASTFunctionDeclarator funcDeclarator = null;
        if (typeRelevant instanceof IASTFunctionDeclarator) {
            funcDeclarator = (IASTFunctionDeclarator) typeRelevant;
        }

        IScope scope = getContainingScope(parent);
        ASTNodeProperty prop = parent.getPropertyInParent();
        if (prop == IASTDeclarationStatement.DECLARATION) {
            // implicit scope, see 6.8.4-3
            prop = parent.getParent().getPropertyInParent();
            if (prop != IASTCompoundStatement.NESTED_STATEMENT) {
                scope = null;
            }
        }

        IASTName name = declarator.getName();

        IBinding binding = null;
        try {
            binding = (scope != null) ? scope.getBinding(name, false) : null;
        } catch (DOMException e1) {
        }

        if (parent instanceof IASTParameterDeclaration
                || parent.getPropertyInParent() == IObjCASTKnRFunctionDeclarator.FUNCTION_PARAMETER) {
            IASTDeclarator fdtor = (IASTDeclarator) parent.getParent();
            if (ASTQueries.findTypeRelevantDeclarator(fdtor) instanceof IASTFunctionDeclarator) {
                IASTName n = ASTQueries.findInnermostDeclarator(fdtor).getName();
                IBinding temp = n.resolveBinding();
                if (temp != null && temp instanceof ObjCFunction) {
                    binding = ((ObjCFunction) temp).resolveParameter(name);
                } else if (temp instanceof IFunction) {
                    // problems with the function, still create binding for the
                    // parameter
                    binding = new ObjCParameter(name);
                }
                return binding;
            }
        } else if (funcDeclarator != null) {
            if (binding != null && !(binding instanceof IIndexBinding) && name.isActive()) {
                if (binding instanceof IFunction) {
                    IFunction function = (IFunction) binding;
                    if (function instanceof ObjCFunction) {
                        ((ObjCFunction) function).addDeclarator(funcDeclarator);
                    }
                    return function;
                }
                binding = new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_OVERLOAD, name
                        .toCharArray());
            } else if (parent instanceof IASTSimpleDeclaration
                    && ((IASTSimpleDeclaration) parent).getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                binding = new ObjCTypedef(name);
            } else {
                binding = new ObjCFunction(funcDeclarator);
            }
        } else if (parent instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) parent;
            if (simpleDecl.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                binding = new ObjCTypedef(name);
            } else {
                IType t1 = null, t2 = null;
                if (binding != null && !(binding instanceof IIndexBinding) && name.isActive()) {
                    if (binding instanceof IParameter) {
                        return new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_REDECLARATION, name
                                .toCharArray());
                    } else if (binding instanceof IVariable) {
                        t1 = createType(declarator);
                        try {
                            t2 = ((IVariable) binding).getType();
                        } catch (DOMException e1) {
                        }
                        if (t1 != null && t2 != null && t1.isSameType(t2)) {
                            if (binding instanceof ObjCVariable) {
                                ((ObjCVariable) binding).addDeclaration(name);
                            }
                        } else {
                            return new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_REDECLARATION,
                                    name.toCharArray());
                        }
                    }
                } else if (simpleDecl.getParent() instanceof IObjCASTCompositeTypeSpecifier) {
                    binding = new ObjCField(name);
                } else {
                    binding = new ObjCVariable(name);
                }
            }
        }
        return binding;
    }

    /**
     * @param parent
     * @return
     */
    private static IBinding createBinding(IASTDeclarator declarator, IASTName name) {
        IBinding binding = null;
        if (declarator instanceof IObjCASTKnRFunctionDeclarator) {
            if (CharArrayUtils.equals(declarator.getName().toCharArray(), name.toCharArray())) {
                IScope scope = ObjCVisitor.getContainingScope(declarator);
                try {
                    binding = scope.getBinding(name, false);
                } catch (DOMException e) {
                }
                if (binding != null && !(binding instanceof IIndexBinding) && name.isActive()) {
                    if (binding instanceof IObjCInternalFunction) {
                        ((IObjCInternalFunction) binding)
                                .addDeclarator((IObjCASTKnRFunctionDeclarator) declarator);
                    } else {
                        binding = new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_OVERLOAD, name
                                .toCharArray());
                    }
                } else {
                    binding = createBinding(declarator);
                }
            } else { // createBinding for one of the
                // IObjCASTKnRFunctionDeclarator's parameterNames
                IBinding f = declarator.getName().resolveBinding();
                if (f instanceof ObjCFunction) {
                    binding = ((ObjCFunction) f).resolveParameter(name);
                }
            }
        } else {
            binding = createBinding(declarator);
        }
        return binding;
    }

    private static IBinding createBinding(IASTEnumerator enumerator) {
        IEnumerator binding = new ObjCEnumerator(enumerator);
        try {
            ASTInternal.addName(binding.getScope(), enumerator.getName());
        } catch (DOMException e) {
        }
        return binding;
    }

    static protected void createBinding(IASTName name) {
        IBinding binding = null;
        IASTNode parent = name.getParent();

        if (parent instanceof ObjCASTIdExpression) {
            binding = resolveBinding(parent);
        } else if (parent instanceof IObjCASTTypedefNameSpecifier) {
            binding = resolveBinding(parent);
        } else if (parent instanceof IASTFieldReference) {
            binding = (IBinding) findBinding((IASTFieldReference) parent, false);
            if (binding == null) {
                binding = new ProblemBinding(name, IProblemBinding.SEMANTIC_NAME_NOT_FOUND, name
                        .toCharArray());
            }
        } else if (parent instanceof IASTDeclarator) {
            binding = createBinding((IASTDeclarator) parent, name);
        } else if (parent instanceof IObjCASTCompositeTypeSpecifier) {
            binding = createBinding((IObjCASTCompositeTypeSpecifier) parent);
        } else if (parent instanceof IObjCASTElaboratedTypeSpecifier) {
            binding = createBinding((IObjCASTElaboratedTypeSpecifier) parent);
        } else if (parent instanceof IASTStatement) {
            binding = createBinding((IASTStatement) parent);
        } else if (parent instanceof IObjCASTEnumerationSpecifier) {
            binding = createBinding((IObjCASTEnumerationSpecifier) parent);
        } else if (parent instanceof IASTEnumerator) {
            binding = createBinding((IASTEnumerator) parent);
        } else if (parent instanceof IObjCASTFieldDesignator) {
            binding = resolveBinding(parent);
        } else if (parent instanceof IObjCASTClassMemoryLayoutDeclaration) {
            binding = resolveBinding(parent);
        }
        name.setBinding(binding);
    }

    private static IBinding createBinding(IASTStatement statement) {
        if (statement instanceof IASTGotoStatement) {
            char[] gotoName = ((IASTGotoStatement) statement).getName().toCharArray();
            IScope scope = getContainingScope(statement);
            if (scope != null && scope instanceof IObjCFunctionScope) {
                ObjCFunctionScope functionScope = (ObjCFunctionScope) scope;
                ILabel[] labels = functionScope.getLabels();
                for (ILabel label : labels) {
                    if (CharArrayUtils.equals(label.getNameCharArray(), gotoName)) {
                        return label;
                    }
                }
                // label not found
                return new ObjCLabel.CLabelProblem(((IASTGotoStatement) statement).getName(),
                        IProblemBinding.SEMANTIC_LABEL_STATEMENT_NOT_FOUND, gotoName);
            }
        } else if (statement instanceof IASTLabelStatement) {
            IASTName name = ((IASTLabelStatement) statement).getName();
            IBinding binding = new ObjCLabel(name);
            try {
                IScope scope = binding.getScope();
                if (scope instanceof IObjCFunctionScope) {
                    ASTInternal.addName(binding.getScope(), name);
                }
            } catch (DOMException e) {
            }
            return binding;
        }
        return null;
    }

    private static IBinding createBinding(IObjCASTCompositeTypeSpecifier compositeTypeSpec) {
        IScope scope = null;
        IBinding binding = null;
        IASTName name = compositeTypeSpec.getName();
        try {
            scope = getContainingScope(compositeTypeSpec);
            while (scope instanceof IObjCCompositeTypeScope) {
                scope = scope.getParent();
            }

            if (scope != null) {
                binding = scope.getBinding(name, false);
                if (binding != null && !(binding instanceof IIndexBinding) && name.isActive()) {
                    if (binding instanceof ObjCCompositeType) {
                        ((ObjCCompositeType) binding).addDefinition(compositeTypeSpec);
                    }
                    return binding;
                }
            }
        } catch (DOMException e2) {
        }
        return new ObjCCompositeType(name);
    }

    private static IBinding createBinding(IObjCASTElaboratedTypeSpecifier elabTypeSpec) {
        IASTNode parent = elabTypeSpec.getParent();
        IASTName name = elabTypeSpec.getName();
        if (parent instanceof IASTDeclaration) {
            IBinding binding = null;
            IScope insertIntoScope = null;
            if (parent instanceof IASTSimpleDeclaration
                    && ((IASTSimpleDeclaration) parent).getDeclarators().length == 0) {
                IScope scope = getContainingScope(elabTypeSpec);
                try {
                    while (scope instanceof IObjCCompositeTypeScope) {
                        scope = scope.getParent();
                    }

                    binding = scope.getBinding(name, false);
                } catch (DOMException e) {
                }
                if (binding != null && name.isActive()) {
                    if (binding instanceof ObjCEnumeration) {
                        ((ObjCEnumeration) binding).addDeclaration(name);
                    } else if (binding instanceof ObjCCompositeType) {
                        ((ObjCCompositeType) binding).addDeclaration(name);
                    }
                }
            } else {
                binding = resolveBinding(elabTypeSpec);
                if (binding == null) {
                    insertIntoScope = elabTypeSpec.getTranslationUnit().getScope();
                    try {
                        binding = insertIntoScope.getBinding(name, false);
                        if (binding != null && name.isActive()) {
                            if (binding instanceof ObjCEnumeration) {
                                ((ObjCEnumeration) binding).addDeclaration(name);
                            } else if (binding instanceof ObjCCompositeType) {
                                ((ObjCCompositeType) binding).addDeclaration(name);
                            }
                        }
                    } catch (DOMException e) {
                    }
                }
            }
            if (binding == null) {
                if (elabTypeSpec.getKind() == IASTElaboratedTypeSpecifier.k_enum) {
                    binding = new ObjCEnumeration(name);
                } else {
                    binding = new ObjCCompositeType(name);
                }
                if (insertIntoScope != null) {
                    ASTInternal.addName(insertIntoScope, name);
                }
            }

            return binding;
        } else if (parent instanceof IASTTypeId || parent instanceof IASTParameterDeclaration) {
            return resolveBinding(elabTypeSpec);
        }
        return null;
    }

    private static IBinding createBinding(IObjCASTEnumerationSpecifier enumeration) {
        IASTName name = enumeration.getName();
        IScope scope = getContainingScope(enumeration);
        IBinding binding = null;
        if (scope != null) {
            try {
                binding = scope.getBinding(name, false);
            } catch (DOMException e) {
            }
        }
        if (binding != null && !(binding instanceof IIndexBinding) && name.isActive()) {
            if (binding instanceof IEnumeration) {
                if (binding instanceof ObjCEnumeration) {
                    ((ObjCEnumeration) binding).addDefinition(name);
                }
            } else {
                return new ProblemBinding(name, IProblemBinding.SEMANTIC_INVALID_OVERLOAD, name.toCharArray());
            }
        } else {
            binding = new ObjCEnumeration(name);
            ASTInternal.addName(scope, name);
        }
        return binding;
    }

    /**
     * Create an IType for an IASTDeclarator.
     * 
     * @param declarator
     *            the IASTDeclarator whose IType will be created
     * @return the IType of the IASTDeclarator parameter
     */
    public static IType createType(IASTDeclarator declarator) {
        IASTDeclSpecifier declSpec = null;

        IASTNode node = declarator.getParent();
        while (node instanceof IASTDeclarator) {
            declarator = (IASTDeclarator) node;
            node = node.getParent();
        }

        if (node instanceof IASTParameterDeclaration) {
            declSpec = ((IASTParameterDeclaration) node).getDeclSpecifier();
        } else if (node instanceof IASTSimpleDeclaration) {
            declSpec = ((IASTSimpleDeclaration) node).getDeclSpecifier();
        } else if (node instanceof IASTFunctionDefinition) {
            declSpec = ((IASTFunctionDefinition) node).getDeclSpecifier();
        } else if (node instanceof IASTTypeId) {
            declSpec = ((IASTTypeId) node).getDeclSpecifier();
        }

        boolean isParameter = (node instanceof IASTParameterDeclaration || node.getParent() instanceof IObjCASTKnRFunctionDeclarator);

        IType type = null;

        // C99 6.7.5.3-12 The storage class specifier for a parameter
        // declaration is ignored unless the declared parameter is one of the
        // members of the parameter type list for a function definition.
        if (isParameter && node.getParent().getParent() instanceof IASTFunctionDefinition) {
            type = createBaseType(declSpec);
        } else {
            type = createType((IObjCASTDeclSpecifier) declSpec);
        }

        type = createType(type, declarator);

        if (isParameter) {
            // C99: 6.7.5.3-7 a declaration of a parameter as "array of type"
            // shall be adjusted to "qualified pointer to type", where the
            // type qualifiers (if any) are those specified within the[and] of
            // the array type derivation
            if (type instanceof IObjCArrayType) {
                IObjCArrayType at = (IObjCArrayType) type;
                try {
                    int q = 0;
                    if (at.isConst()) {
                        q |= ObjCPointerType.IS_CONST;
                    }
                    if (at.isVolatile()) {
                        q |= ObjCPointerType.IS_VOLATILE;
                    }
                    if (at.isRestrict()) {
                        q |= ObjCPointerType.IS_RESTRICT;
                    }
                    type = new ObjCPointerType(at.getType(), q);
                } catch (DOMException e) {
                    // stick to the array
                }
            } else if (type instanceof IFunctionType) {
                // -8 A declaration of a parameter as "function returning type"
                // shall be adjusted to "pointer to function returning type"
                type = new ObjCPointerType(type, 0);
            }
        }

        return type;
    }

    public static IType createType(IObjCASTDeclSpecifier declSpec) {
        if (declSpec.isConst() || declSpec.isVolatile() || declSpec.isRestrict()) {
            return new ObjCQualifierType(declSpec);
        }

        return createBaseType(declSpec);
    }

    public static IType createType(IType baseType, IASTDeclarator declarator) {
        if (declarator instanceof IASTFunctionDeclarator) {
            return createType(baseType, (IASTFunctionDeclarator) declarator);
        }

        IType type = baseType;
        type = setupPointerChain(declarator.getPointerOperators(), type);
        type = setupArrayChain(declarator, type);

        IASTDeclarator nested = declarator.getNestedDeclarator();
        if (nested != null) {
            return createType(type, nested);
        }
        return type;
    }

    public static IType createType(IType returnType, IASTFunctionDeclarator declarator) {

        IType[] pTypes = getParmTypes(declarator);
        returnType = setupPointerChain(declarator.getPointerOperators(), returnType);

        IType type = new ObjCFunctionType(returnType, pTypes);

        IASTDeclarator nested = declarator.getNestedDeclarator();
        if (nested != null) {
            return createType(type, nested);
        }
        return type;
    }

    static public boolean declaredBefore(IASTNode nodeA, IASTNode nodeB) {
        if (nodeB == null) {
            return true;
        }
        if (nodeB.getPropertyInParent() == STRING_LOOKUP_PROPERTY) {
            return true;
        }

        if (nodeA instanceof ASTNode) {
            ASTNode nd = (ASTNode) nodeA;
            int pointOfDecl = 0;

            ASTNodeProperty prop = nd.getPropertyInParent();
            // point of declaration for a name is immediately after its complete
            // declarator and before its initializer
            if (prop == IASTDeclarator.DECLARATOR_NAME || nd instanceof IASTDeclarator) {
                IASTDeclarator dtor = (IASTDeclarator) ((nd instanceof IASTDeclarator) ? nd : nd.getParent());
                while (dtor.getParent() instanceof IASTDeclarator) {
                    dtor = (IASTDeclarator) dtor.getParent();
                }
                IASTInitializer init = dtor.getInitializer();
                if (init != null) {
                    pointOfDecl = ((ASTNode) init).getOffset() - 1;
                } else {
                    pointOfDecl = ((ASTNode) dtor).getOffset() + ((ASTNode) dtor).getLength();
                }
            }
            // point of declaration for an enumerator is immediately after it
            // enumerator-definition
            else if (prop == IASTEnumerator.ENUMERATOR_NAME) {
                IASTEnumerator enumtor = (IASTEnumerator) nd.getParent();
                if (enumtor.getValue() != null) {
                    ASTNode exp = (ASTNode) enumtor.getValue();
                    pointOfDecl = exp.getOffset() + exp.getLength();
                } else {
                    pointOfDecl = nd.getOffset() + nd.getLength();
                }
            } else {
                pointOfDecl = nd.getOffset() + nd.getLength();
            }

            return (pointOfDecl < ((ASTNode) nodeB).getOffset());
        }

        return true;
    }

    private static IBinding externalBinding(IASTTranslationUnit tu, IASTName name) {
        IASTNode parent = name.getParent();
        IBinding external = null;
        if (parent instanceof IASTIdExpression) {
            if (parent.getPropertyInParent() == IASTFunctionCallExpression.FUNCTION_NAME) {
                // external function
                external = new ObjCExternalFunction(tu, name);
                ASTInternal.addName(tu.getScope(), name);
            } else {
                // external variable
                // external = new CExternalVariable(tu, name);
                // ((CScope)tu.getScope()).addName(name);
                external = new ProblemBinding(name, IProblemBinding.SEMANTIC_NAME_NOT_FOUND, name
                        .toCharArray());
            }
        }
        return external;
    }

    /**
     * if prefix == false, return an IBinding or null if prefix == true, return
     * an IBinding[] or null
     * 
     * @param fieldReference
     * @param prefix
     * @return
     */
    private static Object findBinding(IASTFieldReference fieldReference, boolean prefix) {
        IASTExpression fieldOwner = fieldReference.getFieldOwner();
        if (fieldOwner == null) {
            return null;
        }

        IType type = fieldOwner.getExpressionType();
        while (type != null && type instanceof ITypeContainer) {
            try {
                type = ((ITypeContainer) type).getType();
            } catch (DOMException e) {
                return e.getProblem();
            }
        }

        if (type != null && type instanceof ICompositeType) {
            if (type instanceof IIndexBinding) {
                type = ((ObjCASTTranslationUnit) fieldReference.getTranslationUnit())
                        .mapToASTType((ICompositeType) type);
            }
            if (prefix) {
                IBinding[] result = null;
                try {
                    char[] p = fieldReference.getFieldName().toCharArray();
                    IField[] fields = ((ICompositeType) type).getFields();
                    for (IField field : fields) {
                        if (CharArrayUtils.equals(field.getNameCharArray(), 0, p.length, p, true)) {
                            result = (IBinding[]) ArrayUtil.append(IBinding.class, result, field);
                        }
                    }
                    return ArrayUtil.trim(IBinding.class, result);
                } catch (DOMException e) {
                    return new IBinding[] { e.getProblem() };
                }
            }
            try {
                return ((ICompositeType) type).findField(fieldReference.getFieldName().toString());
            } catch (DOMException e) {
                return e.getProblem();
            }
        }
        return null;
    }

    public static IBinding[] findBindings(IScope scope, String name) throws DOMException {
        ObjCASTName astName = new ObjCASTName(name.toCharArray());

        // normal names
        astName.setPropertyInParent(STRING_LOOKUP_PROPERTY);
        Object o1 = lookup(scope, astName);

        IBinding[] b1 = null;
        if (o1 instanceof IBinding) {
            b1 = new IBinding[] { (IBinding) o1 };
        } else {
            b1 = (IBinding[]) o1;
        }

        // structure names
        astName.setPropertyInParent(STRING_LOOKUP_TAGS_PROPERTY);
        Object o2 = lookup(scope, astName);

        IBinding[] b2 = null;
        if (o2 instanceof IBinding) {
            b2 = new IBinding[] { (IBinding) o2 };
        } else {
            b2 = (IBinding[]) o2;
        }

        // label names
        List<ILabel> b3 = new ArrayList<ILabel>();
        do {
            char[] n = name.toCharArray();
            if (scope instanceof IObjCFunctionScope) {
                ILabel[] labels = ((ObjCFunctionScope) scope).getLabels();
                for (ILabel label : labels) {
                    if (CharArrayUtils.equals(label.getNameCharArray(), n)) {
                        b3.add(label);
                        break;
                    }
                }
                break;
            }
            scope = scope.getParent();
        } while (scope != null);

        int c = (b1 == null ? 0 : b1.length) + (b2 == null ? 0 : b2.length) + b3.size();

        IBinding[] result = new IBinding[c];

        if (b1 != null) {
            ArrayUtil.addAll(IBinding.class, result, b1);
        }

        if (b2 != null) {
            ArrayUtil.addAll(IBinding.class, result, b2);
        }

        ArrayUtil.addAll(IBinding.class, result, b3.toArray(new IBinding[b3.size()]));

        return result;
    }

    public static IBinding[] findBindingsForContentAssist(IASTName name, boolean isPrefix) {
        ASTNodeProperty prop = name.getPropertyInParent();

        IBinding[] result = null;

        if (prop == IASTFieldReference.FIELD_NAME) {
            result = (IBinding[]) findBinding((IASTFieldReference) name.getParent(), isPrefix);
        } else {
            IScope scope = getContainingScope(name);
            try {
                if (isPrefix) {
                    result = lookupPrefix(scope, name);
                } else {
                    result = new IBinding[] { lookup(scope, name) };
                }
            } catch (DOMException e) {
            }
        }
        return (IBinding[]) ArrayUtil.trim(IBinding.class, result);
    }

    /**
     * Searches for the first function, struct or union enclosing the
     * declaration the provided node belongs to and returns the binding for it.
     * Returns <code>null</code>, if the declaration is not enclosed by any of
     * the above constructs.
     */
    public static IBinding findDeclarationOwner(IASTNode node, boolean allowFunction) {
        // search for declaration
        while (node instanceof IASTDeclaration == false) {
            if (node == null) {
                return null;
            }

            node = node.getParent();
        }

        // search for enclosing binding
        IASTName name = null;
        node = node.getParent();
        for (; node != null; node = node.getParent()) {
            if (node instanceof IASTFunctionDefinition) {
                if (!allowFunction) {
                    continue;
                }

                IASTDeclarator dtor = findInnermostDeclarator(((IASTFunctionDefinition) node).getDeclarator());
                if (dtor != null) {
                    name = dtor.getName();
                }
                break;
            }
            if (node instanceof IASTCompositeTypeSpecifier) {
                name = ((IASTCompositeTypeSpecifier) node).getName();
                break;
            }
        }
        if (name == null) {
            return null;
        }

        return name.resolveBinding();
    }

    protected static IASTDeclarator findDefinition(IASTDeclarator declarator, int beginAtLoc) {
        return (IASTDeclarator) findDefinition(declarator, declarator.getName().toCharArray(), beginAtLoc);
    }

    protected static IASTFunctionDeclarator findDefinition(IASTFunctionDeclarator declarator) {
        return (IASTFunctionDeclarator) findDefinition(declarator, declarator.getName().toCharArray(),
                AT_NEXT);
    }

    private static IASTNode findDefinition(IASTNode decl, char[] declName, int beginAtLoc) {
        IASTNode blockItem = getContainingBlockItem(decl);
        IASTNode parent = blockItem.getParent();
        IASTNode[] list = null;
        if (parent instanceof IASTCompoundStatement) {
            IASTCompoundStatement compound = (IASTCompoundStatement) parent;
            list = compound.getStatements();
        } else if (parent instanceof IASTTranslationUnit) {
            IASTTranslationUnit translation = (IASTTranslationUnit) parent;
            list = translation.getDeclarations();
        }
        boolean begun = (beginAtLoc == AT_BEGINNING);
        if (list != null) {
            for (IASTNode node : list) {
                if (node == blockItem) {
                    begun = true;
                    continue;
                }

                if (begun) {
                    if (node instanceof IASTDeclarationStatement) {
                        node = ((IASTDeclarationStatement) node).getDeclaration();
                    }

                    if (node instanceof IASTFunctionDefinition && decl instanceof IASTFunctionDeclarator) {
                        IASTFunctionDeclarator dtor = ((IASTFunctionDefinition) node).getDeclarator();
                        IASTName name = ASTQueries.findInnermostDeclarator(dtor).getName();
                        if (name.toString().equals(declName)) {
                            return dtor;
                        }
                    } else if (node instanceof IASTSimpleDeclaration
                            && decl instanceof IObjCASTElaboratedTypeSpecifier) {
                        IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) node;
                        IASTDeclSpecifier declSpec = simpleDecl.getDeclSpecifier();
                        IASTName name = null;

                        if (declSpec instanceof IObjCASTCompositeTypeSpecifier) {
                            name = ((IObjCASTCompositeTypeSpecifier) declSpec).getName();
                        } else if (declSpec instanceof IObjCASTEnumerationSpecifier) {
                            name = ((IObjCASTEnumerationSpecifier) declSpec).getName();
                        }
                        if (name != null) {
                            if (CharArrayUtils.equals(name.toCharArray(), declName)) {
                                return declSpec;
                            }
                        }
                    } else if (node instanceof IASTSimpleDeclaration && decl instanceof IASTDeclarator) {
                        IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) node;
                        IASTDeclarator[] dtors = simpleDecl.getDeclarators();
                        for (int j = 0; dtors != null && j < dtors.length; j++) {
                            if (CharArrayUtils.equals(dtors[j].getName().toCharArray(), declName)) {
                                return dtors[j];
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    protected static IASTDeclSpecifier findDefinition(IObjCASTElaboratedTypeSpecifier declSpec) {
        return (IASTDeclSpecifier) findDefinition(declSpec, declSpec.getName().toCharArray(), AT_BEGINNING);
    }

    /**
     * Searches for the function enclosing the given node. May return
     * <code>null</code>.
     */
    public static IBinding findEnclosingFunction(IASTNode node) {
        while (node != null && node instanceof IASTFunctionDefinition == false) {
            node = node.getParent();
        }
        if (node == null) {
            return null;
        }

        IASTDeclarator dtor = findInnermostDeclarator(((IASTFunctionDefinition) node).getDeclarator());
        if (dtor != null) {
            IASTName name = dtor.getName();
            if (name != null) {
                return name.resolveBinding();
            }
        }
        return null;
    }

    private static IASTNode getContainingBlockItem(IASTNode node) {
        IASTNode parent = node.getParent();
        if (parent instanceof IASTDeclaration) {
            IASTNode p = parent.getParent();
            if (p instanceof IASTDeclarationStatement) {
                return p;
            }
            return parent;
        }
        // if parent is something that can contain a declaration
        else if (parent instanceof IASTCompoundStatement || parent instanceof IASTTranslationUnit
                || parent instanceof IASTForStatement || parent instanceof IASTFunctionDeclarator) {
            return node;
        }

        return getContainingBlockItem(parent);
    }

    /**
     * May return <code>null</code>, e.g. for parameter names in
     * function-prototypes.
     */
    public static IScope getContainingScope(IASTNode node) {
        if (node == null) {
            return null;
        }
        while (node != null) {
            if (node instanceof IASTDeclaration) {
                IASTNode parent = node.getParent();
                if (parent instanceof IASTTranslationUnit) {
                    return ((IASTTranslationUnit) parent).getScope();
                } else if (parent instanceof IASTDeclarationStatement) {
                    return getContainingScope((IASTStatement) parent);
                } else if (parent instanceof IASTForStatement) {
                    return ((IASTForStatement) parent).getScope();
                } else if (parent instanceof IASTCompositeTypeSpecifier) {
                    return ((IASTCompositeTypeSpecifier) parent).getScope();
                } else if (parent instanceof IObjCASTKnRFunctionDeclarator) {
                    parent = ((IASTDeclarator) parent).getParent();
                    if (parent instanceof IASTFunctionDefinition) {
                        return ((IASTCompoundStatement) ((IASTFunctionDefinition) parent).getBody())
                                .getScope();
                    }
                }
            } else if (node instanceof IASTStatement) {
                return getContainingScope((IASTStatement) node);
            } else if (node instanceof IASTExpression) {
                IASTNode parent = node.getParent();
                if (parent instanceof IASTForStatement) {
                    return ((IASTForStatement) parent).getScope();
                }
            } else if (node instanceof IASTParameterDeclaration) {
                IASTNode parent = node.getParent();
                if (parent instanceof IASTStandardFunctionDeclarator) {
                    IASTStandardFunctionDeclarator dtor = (IASTStandardFunctionDeclarator) parent;
                    if (ASTQueries.findTypeRelevantDeclarator(dtor) == dtor) {
                        parent = ASTQueries.findOutermostDeclarator(dtor);
                        ASTNodeProperty prop = parent.getPropertyInParent();
                        if (prop == IASTSimpleDeclaration.DECLARATOR) {
                            return dtor.getFunctionScope();
                        } else if (prop == IASTFunctionDefinition.DECLARATOR) {
                            return ((IASTCompoundStatement) ((IASTFunctionDefinition) parent.getParent())
                                    .getBody()).getScope();
                        }
                    }
                }
            } else if (node instanceof IASTEnumerator) {
                // put the enumerators in the same scope as the enumeration
                node = node.getParent();
            } else if (node instanceof IASTName) {
                ASTNodeProperty prop = node.getPropertyInParent();
                if (prop == IASTLabelStatement.NAME) {
                    IScope scope = getContainingScope(node.getParent());
                    // labels have function scope
                    while (scope != null && !(scope instanceof IObjCFunctionScope)) {
                        try {
                            scope = scope.getParent();
                        } catch (DOMException e) {
                            scope = e.getProblem();
                            break;
                        }
                    }
                    return scope;
                }
            }

            node = node.getParent();
        }
        return null;
    }

    public static IScope getContainingScope(IASTStatement statement) {
        IASTNode parent = statement.getParent();
        IScope scope = null;
        if (parent instanceof IASTCompoundStatement) {
            IASTCompoundStatement compound = (IASTCompoundStatement) parent;
            scope = compound.getScope();
        } else if (parent instanceof IASTStatement) {
            if (parent instanceof IASTForStatement) {
                scope = ((IASTForStatement) parent).getScope();
            } else {
                scope = getContainingScope((IASTStatement) parent);
            }
        } else if (parent instanceof IASTFunctionDefinition) {
            return ((IASTFunctionDefinition) parent).getScope();
        } else {
            return getContainingScope(parent);
        }

        if (statement instanceof IASTGotoStatement) {
            // labels have function scope
            while (scope != null && !(scope instanceof IObjCFunctionScope)) {
                try {
                    scope = scope.getParent();
                } catch (DOMException e) {
                    scope = e.getProblem();
                    break;
                }
            }
        }

        return scope;
    }

    public static IASTName[] getDeclarations(IASTTranslationUnit tu, IBinding binding) {
        CollectDeclarationsAction action = new CollectDeclarationsAction(binding);
        tu.accept(action);

        return action.getDeclarationNames();
    }

    protected static IASTDeclarator getKnRParameterDeclarator(IObjCASTKnRFunctionDeclarator fKnRDtor,
            IASTName name) {
        IASTDeclaration[] decls = fKnRDtor.getParameterDeclarations();
        char[] n = name.toCharArray();
        for (int i = 0; i < decls.length; i++) {
            if (!(decls[i] instanceof IASTSimpleDeclaration)) {
                continue;
            }

            IASTDeclarator[] dtors = ((IASTSimpleDeclaration) decls[i]).getDeclarators();
            for (IASTDeclarator dtor : dtors) {
                if (CharArrayUtils.equals(dtor.getName().toCharArray(), n)) {
                    return dtor;
                }
            }
        }
        return null;
    }

    /**
     * Returns an IType[] corresponding to the parameter types of the
     * IASTFunctionDeclarator parameter.
     * 
     * @param decltor
     *            the IASTFunctionDeclarator to create an IType[] for its
     *            parameters
     * @return IType[] corresponding to the IASTFunctionDeclarator parameters
     */
    private static IType[] getParmTypes(IASTFunctionDeclarator decltor) {
        if (decltor instanceof IASTStandardFunctionDeclarator) {
            IASTParameterDeclaration parms[] = ((IASTStandardFunctionDeclarator) decltor).getParameters();
            IType parmTypes[] = new IType[parms.length];

            for (int i = 0; i < parms.length; i++) {
                parmTypes[i] = createType(parms[i].getDeclarator());
            }
            return parmTypes;
        } else if (decltor instanceof IObjCASTKnRFunctionDeclarator) {
            IASTName parms[] = ((IObjCASTKnRFunctionDeclarator) decltor).getParameterNames();
            IType parmTypes[] = new IType[parms.length];

            for (int i = 0; i < parms.length; i++) {
                IASTDeclarator dtor = getKnRParameterDeclarator((IObjCASTKnRFunctionDeclarator) decltor,
                        parms[i]);
                if (dtor != null) {
                    parmTypes[i] = createType(dtor);
                }
            }
            return parmTypes;
        } else {
            return null;
        }
    }

    public static IASTProblem[] getProblems(IASTTranslationUnit tu) {
        CollectProblemsAction action = new CollectProblemsAction();
        tu.accept(action);

        return action.getProblems();
    }

    static IType getProtocol(IASTName name) {
        IType type = null;
        IBinding binding = name.resolveBinding();
        if (binding instanceof IType) {
            return (IType) binding;
        }
        return new ObjCPointerType(type, 0);
    }

    static IType getPtrDiffType(IASTBinaryExpression expr) {
        IScope scope = getContainingScope(expr);
        try {
            IBinding[] bs = scope.find(PTRDIFF_T);
            if (bs.length > 0) {
                for (IBinding b : bs) {
                    if (b instanceof IType) {
                        if (b instanceof IObjCInternalBinding == false
                                || ObjCVisitor.declaredBefore(((IObjCInternalBinding) b).getPhysicalNode(),
                                        expr)) {
                            return (IType) b;
                        }
                    }
                }
            }
        } catch (DOMException e) {
        }

        ObjCBasicType basicType = new ObjCBasicType(IBasicType.t_int, ObjCBasicType.IS_UNSIGNED
                | ObjCBasicType.IS_LONG);
        basicType.setValue(expr);
        return basicType;
    }

    public static IASTName[] getReferences(IASTTranslationUnit tu, IBinding binding) {
        CollectReferencesAction action = new CollectReferencesAction(binding);
        tu.accept(action);
        return action.getReferences();
    }

    static IType getSEL(IASTExpression expr) {
        IScope scope = getContainingScope(expr);
        try {
            IBinding[] bs = scope.find(SEL);
            if (bs.length > 0 && bs[0] instanceof IType) {
                return (IType) bs[0];
            }
        } catch (DOMException e) {
        }
        return new ObjCBasicType(IObjCBasicType.t_SEL, 0);
    }

    static IType getSize_T(IASTExpression expr) {
        IScope scope = getContainingScope(expr);
        try {
            IBinding[] bs = scope.find(SIZE_T);
            if (bs.length > 0 && bs[0] instanceof IType) {
                return (IType) bs[0];
            }
        } catch (DOMException e) {
        }
        return new ObjCBasicType(IBasicType.t_int, ObjCBasicType.IS_LONG | ObjCBasicType.IS_UNSIGNED);
    }

    /**
     * Lookup for a name starting from the given scope.
     */
    protected static IBinding lookup(IScope scope, IASTName name) throws DOMException {
        if (scope == null) {
            return null;
        }

        IIndexFileSet fileSet = IIndexFileSet.EMPTY;
        IASTTranslationUnit tu = name.getTranslationUnit();
        if (tu == null && scope instanceof IASTInternalScope) {
            tu = ((IASTInternalScope) scope).getPhysicalNode().getTranslationUnit();
        }
        if (tu != null) {
            final IIndexFileSet fs = (IIndexFileSet) tu.getAdapter(IIndexFileSet.class);
            if (fs != null) {
                fileSet = fs;
            }
        }

        while (scope != null) {
            try {
                if (!(scope instanceof IObjCCompositeTypeScope)) {
                    IBinding binding = scope.getBinding(name, true, fileSet);
                    if (binding != null) {
                        return binding;
                    }
                }
            } catch (DOMException e) {
            }
            scope = scope.getParent();
        }

        return externalBinding(tu, name);
    }

    /**
     * if (bits & PREFIX_LOOKUP) then returns IBinding[] otherwise returns
     * IBinding
     */
    protected static IBinding[] lookupPrefix(IScope scope, IASTName name) throws DOMException {
        if (scope == null) {
            return null;
        }

        IIndexFileSet fileSet = IIndexFileSet.EMPTY;
        IASTTranslationUnit tu = name.getTranslationUnit();
        if (tu == null && scope instanceof IASTInternalScope) {
            tu = ((IASTInternalScope) scope).getPhysicalNode().getTranslationUnit();
        }
        if (tu != null) {
            final IIndexFileSet fs = (IIndexFileSet) tu.getAdapter(IIndexFileSet.class);
            if (fs != null) {
                fileSet = fs;
            }
        }

        CharArrayObjectMap prefixMap = new CharArrayObjectMap(2);
        while (scope != null) {
            try {
                if (!(scope instanceof IObjCCompositeTypeScope)) {
                    IBinding[] bindings = scope.getBindings(name, true, true, fileSet);
                    for (IBinding b : bindings) {
                        final char[] n = b.getNameCharArray();
                        if (!prefixMap.containsKey(n)) {
                            prefixMap.put(n, b);
                        }
                    }
                }
            } catch (DOMException e) {
            }
            scope = scope.getParent();
        }

        IBinding[] result = null;
        Object[] vals = prefixMap.valueArray();
        for (Object val : vals) {
            result = (IBinding[]) ArrayUtil.append(IBinding.class, result, val);
        }
        return (IBinding[]) ArrayUtil.trim(IBinding.class, result);
    }

    protected static IBinding resolveBinding(IASTNode node) {
        if (node instanceof IObjCASTClassMemoryLayoutDeclaration) {
            IASTName name = ((IObjCASTClassMemoryLayoutDeclaration) node).getClassName();
            IScope scope = getContainingScope(node.getParent());
            try {
                return lookup(scope, name);
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDef = (IASTFunctionDefinition) node;
            IASTFunctionDeclarator functionDeclartor = functionDef.getDeclarator();
            IASTName name = findInnermostDeclarator(functionDeclartor).getName();
            IScope scope = getContainingScope(node);
            try {
                return lookup(scope, name);
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IASTIdExpression) {
            IScope scope = getContainingScope(node);
            try {
                IBinding binding = lookup(scope, ((IASTIdExpression) node).getName());
                if (binding instanceof IType && !(binding instanceof IProblemBinding)) {
                    return new ProblemBinding(node, IProblemBinding.SEMANTIC_INVALID_TYPE, binding
                            .getNameCharArray(), new IBinding[] { binding });
                }
                return binding;
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IObjCASTTypedefNameSpecifier) {
            IScope scope = getContainingScope(node);
            try {
                IASTName name = ((IObjCASTTypedefNameSpecifier) node).getName();
                IBinding binding = lookup(scope, name);
                if (binding == null) {
                    return new ProblemBinding(node, IProblemBinding.SEMANTIC_NAME_NOT_FOUND, name
                            .toCharArray());
                }
                if (binding instanceof IType) {
                    return binding;
                }
                return new ProblemBinding(node, IProblemBinding.SEMANTIC_INVALID_TYPE, binding
                        .getNameCharArray(), new IBinding[] { binding });
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IObjCASTElaboratedTypeSpecifier) {
            IScope scope = getContainingScope(node);
            try {
                return lookup(scope, ((IObjCASTElaboratedTypeSpecifier) node).getName());
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IObjCASTCompositeTypeSpecifier) {
            IScope scope = getContainingScope(node);
            try {
                return lookup(scope, ((IObjCASTCompositeTypeSpecifier) node).getName());
            } catch (DOMException e) {
                return null;
            }
        } else if (node instanceof IASTTypeId) {
            IASTTypeId typeId = (IASTTypeId) node;
            IASTDeclSpecifier declSpec = typeId.getDeclSpecifier();
            IASTName name = null;
            if (declSpec instanceof IObjCASTElaboratedTypeSpecifier) {
                name = ((IObjCASTElaboratedTypeSpecifier) declSpec).getName();
            } else if (declSpec instanceof IObjCASTCompositeTypeSpecifier) {
                name = ((IObjCASTCompositeTypeSpecifier) declSpec).getName();
            } else if (declSpec instanceof IObjCASTTypedefNameSpecifier) {
                name = ((IObjCASTTypedefNameSpecifier) declSpec).getName();
            }
            if (name != null) {
                IBinding binding = name.resolveBinding();
                if (binding instanceof IType) {
                    return binding;
                } else if (binding != null) {
                    return new ProblemBinding(node, IProblemBinding.SEMANTIC_INVALID_TYPE, binding
                            .getNameCharArray(), new IBinding[] { binding });
                }
                return null;
            }
        } else if (node instanceof IObjCASTFieldDesignator) {
            IASTNode blockItem = getContainingBlockItem(node);

            if ((blockItem instanceof IASTSimpleDeclaration || (blockItem instanceof IASTDeclarationStatement && ((IASTDeclarationStatement) blockItem)
                    .getDeclaration() instanceof IASTSimpleDeclaration))) {

                IASTSimpleDeclaration simpleDecl = null;
                if (blockItem instanceof IASTDeclarationStatement
                        && ((IASTDeclarationStatement) blockItem).getDeclaration() instanceof IASTSimpleDeclaration) {
                    simpleDecl = (IASTSimpleDeclaration) ((IASTDeclarationStatement) blockItem)
                            .getDeclaration();
                } else if (blockItem instanceof IASTSimpleDeclaration) {
                    simpleDecl = (IASTSimpleDeclaration) blockItem;
                }

                if (simpleDecl != null) {
                    IBinding struct = null;
                    if (simpleDecl.getDeclSpecifier() instanceof IASTNamedTypeSpecifier) {
                        struct = ((IASTNamedTypeSpecifier) simpleDecl.getDeclSpecifier()).getName()
                                .resolveBinding();
                    } else if (simpleDecl.getDeclSpecifier() instanceof IASTElaboratedTypeSpecifier) {
                        struct = ((IASTElaboratedTypeSpecifier) simpleDecl.getDeclSpecifier()).getName()
                                .resolveBinding();
                    } else if (simpleDecl.getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                        struct = ((IASTCompositeTypeSpecifier) simpleDecl.getDeclSpecifier()).getName()
                                .resolveBinding();
                    }

                    if (struct instanceof ObjCCompositeType) {
                        try {
                            return ((ObjCCompositeType) struct).findField(((IObjCASTFieldDesignator) node)
                                    .getName().toString());
                        } catch (DOMException e) {
                            return e.getProblem();
                        }
                    } else if (struct instanceof ITypeContainer) {
                        IType type;
                        try {
                            type = ((ITypeContainer) struct).getType();
                            while (type instanceof ITypeContainer && !(type instanceof ObjCCompositeType)) {
                                type = ((ITypeContainer) type).getType();
                            }
                        } catch (DOMException e) {
                            return e.getProblem();
                        }

                        if (type instanceof ObjCCompositeType) {
                            try {
                                return ((ObjCCompositeType) type).findField(((IObjCASTFieldDesignator) node)
                                        .getName().toString());
                            } catch (DOMException e1) {
                                return e1.getProblem();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Traverse through an array of IASTArrayModifier[] corresponding to the
     * IASTDeclarator decl parameter. For each IASTArrayModifier in the array,
     * create a corresponding CArrayType object and link it in a chain. The
     * returned IType is the start of the CArrayType chain that represents the
     * types of the IASTArrayModifier objects in the declarator.
     * 
     * @param decl
     *            the IASTDeclarator containing the IASTArrayModifier[] array to
     *            create a CArrayType chain for
     * @param lastType
     *            the IType that the end of the CArrayType chain points to
     * @return the starting CArrayType at the beginning of the CArrayType chain
     */
    private static IType setupArrayChain(IASTDeclarator decl, IType lastType) {
        if (decl instanceof IASTArrayDeclarator) {
            IASTArrayModifier[] mods = ((IASTArrayDeclarator) decl).getArrayModifiers();
            for (int i = mods.length - 1; i >= 0; i--) {
                ObjCArrayType arrayType = new ObjCArrayType(lastType);
                if (mods[i] instanceof IObjCASTArrayModifier) {
                    arrayType.setModifier((IObjCASTArrayModifier) mods[i]);
                }
                lastType = arrayType;
            }
        }

        return lastType;
    }

    /**
     * Traverse through an array of IASTPointerOperator[] pointers and set up a
     * pointer chain corresponding to the types of the IASTPointerOperator[].
     * 
     * @param ptrs
     *            an array of IASTPointerOperator[] used to setup the pointer
     *            chain
     * @param lastType
     *            the IType that the end of the CPointerType chain points to
     * @return the starting CPointerType at the beginning of the CPointerType
     *         chain
     */
    private static IType setupPointerChain(IASTPointerOperator[] ptrs, IType lastType) {
        ObjCPointerType pointerType = null;

        if (ptrs != null && ptrs.length > 0) {
            pointerType = new ObjCPointerType();

            if (ptrs.length == 1) {
                pointerType.setType(lastType);
                pointerType.setQualifiers((((IObjCASTPointer) ptrs[0]).isConst() ? ObjCPointerType.IS_CONST
                        : 0)
                        | (((IObjCASTPointer) ptrs[0]).isRestrict() ? ObjCPointerType.IS_RESTRICT : 0)
                        | (((IObjCASTPointer) ptrs[0]).isVolatile() ? ObjCPointerType.IS_VOLATILE : 0));
            } else {
                ObjCPointerType tempType = new ObjCPointerType();
                pointerType.setType(tempType);
                pointerType
                        .setQualifiers((((IObjCASTPointer) ptrs[ptrs.length - 1]).isConst() ? ObjCPointerType.IS_CONST
                                : 0)
                                | (((IObjCASTPointer) ptrs[ptrs.length - 1]).isRestrict() ? ObjCPointerType.IS_RESTRICT
                                        : 0)
                                | (((IObjCASTPointer) ptrs[ptrs.length - 1]).isVolatile() ? ObjCPointerType.IS_VOLATILE
                                        : 0));
                int i = ptrs.length - 2;
                for (; i > 0; i--) {
                    tempType.setType(new ObjCPointerType());
                    tempType.setQualifiers((((IObjCASTPointer) ptrs[i]).isConst() ? ObjCPointerType.IS_CONST
                            : 0)
                            | (((IObjCASTPointer) ptrs[i]).isRestrict() ? ObjCPointerType.IS_RESTRICT : 0)
                            | (((IObjCASTPointer) ptrs[i]).isVolatile() ? ObjCPointerType.IS_VOLATILE : 0));
                    tempType = (ObjCPointerType) tempType.getType();
                }
                tempType.setType(lastType);
                tempType.setQualifiers((((IObjCASTPointer) ptrs[i]).isConst() ? ObjCPointerType.IS_CONST : 0)
                        | (((IObjCASTPointer) ptrs[i]).isRestrict() ? ObjCPointerType.IS_RESTRICT : 0)
                        | (((IObjCASTPointer) ptrs[i]).isVolatile() ? ObjCPointerType.IS_VOLATILE : 0));
            }

            return pointerType;
        }

        return lastType;
    }

    static IType unwrapTypedefs(IType type) throws DOMException {
        while (type instanceof ITypedef) {
            type = ((ITypedef) type).getType();
        }
        return type;
    }
}
