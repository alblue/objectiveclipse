/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Niefer (IBM Corporation) - initial API and
 * implementation Markus Schorn (Wind River Systems) Bryan Wilkinson (QNX)
 * Andrew Ferguson (Symbian)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.c.CASTVisitor;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTUnaryExpression;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileSet;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.core.parser.util.CharArrayObjectMap;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.IASTInternalScope;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTClassMemoryLayoutDeclaration;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTEnumerationSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCScope;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Base implementation for c-scopes
 */
@SuppressWarnings("restriction")
public class ObjCScope implements IObjCScope, IASTInternalScope {
    protected static class CollectNamesAction extends CASTVisitor {
        private final char[] name;
        private IASTName[] result = null;

        CollectNamesAction(char[] n) {
            name = n;
            shouldVisitNames = true;
        }

        public IASTName[] getNames() {
            return (IASTName[]) ArrayUtil.trim(IASTName.class, result);
        }

        @Override
        public int visit(IASTName n) {
            ASTNodeProperty prop = n.getPropertyInParent();
            if (prop == IASTElaboratedTypeSpecifier.TYPE_NAME || prop == IASTCompositeTypeSpecifier.TYPE_NAME
                    || prop == IASTDeclarator.DECLARATOR_NAME) {
                if (CharArrayUtils.equals(n.toCharArray(), name)) {
                    result = (IASTName[]) ArrayUtil.append(IASTName.class, result, n);
                }
            }

            return PROCESS_CONTINUE;
        }

        @Override
        public int visit(IASTStatement statement) {
            if (statement instanceof IASTDeclarationStatement) {
                return PROCESS_CONTINUE;
            }
            return PROCESS_SKIP;
        }
    }

    private static final IndexFilter[] INDEX_FILTERS = { new IndexFilter() { // namespace
                // type
                // tag
                @Override
                public boolean acceptBinding(IBinding binding) throws CoreException {
                    return IndexFilter.C_DECLARED_OR_IMPLICIT.acceptBinding(binding)
                            && (binding instanceof ICompositeType || binding instanceof IEnumeration);
                }

                @Override
                public boolean acceptLinkage(ILinkage linkage) {
                    return IndexFilter.C_DECLARED_OR_IMPLICIT.acceptLinkage(linkage);
                }
            }, new IndexFilter() { // namespace type other
                @Override
                public boolean acceptBinding(IBinding binding) throws CoreException {
                    return IndexFilter.C_DECLARED_OR_IMPLICIT.acceptBinding(binding)
                            && !(binding instanceof ICompositeType || binding instanceof IEnumeration);
                }

                @Override
                public boolean acceptLinkage(ILinkage linkage) {
                    return IndexFilter.C_DECLARED_OR_IMPLICIT.acceptLinkage(linkage);
                }
            },
            // namespace type both
            IndexFilter.C_DECLARED_OR_IMPLICIT };
    public static final int NAMESPACE_TYPE_BOTH = 2;

    public static final int NAMESPACE_TYPE_OTHER = 1;

    /**
     * ISO C:99 6.2.3 there are separate namespaces for various categories of
     * identifiers: - label names ( labels have ICFunctionScope ) - tags of
     * structures or unions : NAMESPACE_TYPE_TAG - members of structures or
     * unions ( members have ICCompositeTypeScope ) - all other identifiers :
     * NAMESPACE_TYPE_OTHER
     */
    public static final int NAMESPACE_TYPE_TAG = 0;
    private boolean isCached = false;

    private final EScopeKind kind;
    private final CharArrayObjectMap[] mapsToNameOrBinding = { CharArrayObjectMap.EMPTY_MAP,
            CharArrayObjectMap.EMPTY_MAP };

    private IASTNode physicalNode = null;

    public ObjCScope(IASTNode physical, EScopeKind eKind) {
        physicalNode = physical;
        kind = eKind;
    }

    private boolean acceptDeclaredAfter(IASTName name) {
        if (getKind() != EScopeKind.eGlobal) {
            return false;
        }
        final ASTNodeProperty propertyInParent = name.getPropertyInParent();
        if (propertyInParent == IASTNamedTypeSpecifier.NAME
                || propertyInParent == IASTElaboratedTypeSpecifier.TYPE_NAME) {
            return false;
        }
        IASTNode parent = name.getParent();
        while (parent != null) {
            if (parent instanceof IASTUnaryExpression) {
                if (((IASTUnaryExpression) parent).getOperator() == IGNUASTUnaryExpression.op_typeof) {
                    return false;
                }
            } else if (parent instanceof IASTTypeIdExpression) {
                if (((IASTTypeIdExpression) parent).getOperator() == IASTTypeIdExpression.op_typeof) {
                    return false;
                }
            }
            parent = parent.getParent();
        }
        return true;
    }

    public void addBinding(IBinding binding) {
        int type = NAMESPACE_TYPE_OTHER;
        if (binding instanceof ICompositeType || binding instanceof IEnumeration) {
            type = NAMESPACE_TYPE_TAG;
        }

        CharArrayObjectMap map = mapsToNameOrBinding[type];
        if (map == CharArrayObjectMap.EMPTY_MAP) {
            map = mapsToNameOrBinding[type] = new CharArrayObjectMap(2);
        }

        map.put(binding.getNameCharArray(), binding);
    }

    public void addName(IASTName name) {
        final char[] nchars = name.toCharArray();
        if (nchars.length == 0) {
            return;
        }

        int type = getNamespaceType(name);
        CharArrayObjectMap map = mapsToNameOrBinding[type];
        if (map == CharArrayObjectMap.EMPTY_MAP) {
            map = mapsToNameOrBinding[type] = new CharArrayObjectMap(1);
        }

        Object o = map.get(nchars);
        if (o instanceof IASTName) {
            if (o != name) {
                map.put(nchars, new IASTName[] { (IASTName) o, name });
            }
        } else if (o instanceof IASTName[]) {
            final IASTName[] names = (IASTName[]) o;
            for (IASTName n : names) {
                if (n == null) {
                    break;
                }
                if (n == name) {
                    return;
                }
            }
            final IASTName[] newNames = (IASTName[]) ArrayUtil.append(IASTName.class, names, name);
            if (newNames != names) {
                map.put(nchars, newNames);
            }
        } else {
            map.put(nchars, name);
        }
    }

    private void collectNames(IASTDeclaration declaration) {
        if (declaration instanceof IASTAmbiguousSimpleDeclaration) {
            return;
        }

        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
            for (IASTDeclarator dtor : declarators) {
                collectNames(dtor);
            }
            collectNames(simpleDeclaration.getDeclSpecifier(), declarators.length == 0);
        } else if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDef = (IASTFunctionDefinition) declaration;
            collectNames(functionDef.getDeclarator());
            collectNames(functionDef.getDeclSpecifier(), false);
        }
    }

    private void collectNames(IASTDeclarator dtor) {
        IASTDeclarator innermost = null;
        while (dtor != null) {
            if (dtor instanceof IASTAmbiguousDeclarator) {
                innermost = null;
                break;
            }
            innermost = dtor;
            dtor = dtor.getNestedDeclarator();
        }
        if (innermost != null) {
            ASTInternal.addName(this, innermost.getName());
        }
    }

    private void collectNames(IASTDeclSpecifier declSpec, boolean forceElabSpec) {
        IASTName tempName = null;
        if (declSpec instanceof IObjCASTElaboratedTypeSpecifier) {
            if (forceElabSpec || physicalNode instanceof IASTTranslationUnit) {
                tempName = ((IObjCASTElaboratedTypeSpecifier) declSpec).getName();
                ASTInternal.addName(this, tempName);
            }
        } else if (declSpec instanceof IObjCASTCompositeTypeSpecifier) {
            tempName = ((IObjCASTCompositeTypeSpecifier) declSpec).getName();
            ASTInternal.addName(this, tempName);

            // also have to check for any nested structs
            IASTDeclaration[] nested = ((IObjCASTCompositeTypeSpecifier) declSpec).getMembers();
            for (IASTDeclaration element : nested) {
                if (element instanceof IASTSimpleDeclaration) {
                    IASTDeclSpecifier d = ((IASTSimpleDeclaration) element).getDeclSpecifier();
                    if (d instanceof IObjCASTCompositeTypeSpecifier || d instanceof IASTEnumerationSpecifier) {
                        collectNames(d, false);
                    }
                }
            }
        } else if (declSpec instanceof IObjCASTEnumerationSpecifier) {
            IObjCASTEnumerationSpecifier enumeration = (IObjCASTEnumerationSpecifier) declSpec;
            tempName = enumeration.getName();
            ASTInternal.addName(this, tempName);

            // check enumerators
            IASTEnumerator[] list = ((IObjCASTEnumerationSpecifier) declSpec).getEnumerators();
            for (IASTEnumerator enumerator : list) {
                if (enumerator == null) {
                    break;
                }
                tempName = enumerator.getName();
                ASTInternal.addName(this, tempName);
            }
        }
    }

    public void collectNames(IASTNode node) {
        if (node instanceof IASTDeclaration) {
            collectNames((IASTDeclaration) node);
        } else if (node instanceof IASTParameterDeclaration) {
            collectNames((IASTParameterDeclaration) node);
        } else if (node instanceof IASTDeclarationStatement) {
            collectNames(((IASTDeclarationStatement) node).getDeclaration());
        }
    }

    private void collectNames(IASTParameterDeclaration paramDecl) {
        if (paramDecl == null || paramDecl instanceof IASTAmbiguousParameterDeclaration) {
            return;
        }

        collectNames(paramDecl.getDeclarator());
        collectNames(paramDecl.getDeclSpecifier(), false);
    }

    protected void doPopulateCache() {
        final IASTNode scopeNode = physicalNode;
        IASTNode[] nodes = null;
        if (scopeNode instanceof IASTCompoundStatement) {
            IASTCompoundStatement compound = (IASTCompoundStatement) scopeNode;
            if (scopeNode.getParent() instanceof IASTFunctionDefinition) {
                IASTFunctionDeclarator dtor = ((IASTFunctionDefinition) scopeNode.getParent())
                        .getDeclarator();
                if (dtor instanceof IASTStandardFunctionDeclarator) {
                    nodes = ((IASTStandardFunctionDeclarator) dtor).getParameters();
                } else if (dtor instanceof IObjCASTKnRFunctionDeclarator) {
                    nodes = ((IObjCASTKnRFunctionDeclarator) dtor).getParameterDeclarations();
                }
            }
            if (nodes == null || nodes.length == 0) {
                nodes = compound.getStatements();
            }
        } else if (scopeNode instanceof IASTTranslationUnit) {
            IASTTranslationUnit translation = (IASTTranslationUnit) scopeNode;
            nodes = translation.getDeclarations();
        } else if (scopeNode instanceof IASTStandardFunctionDeclarator) {
            IASTStandardFunctionDeclarator dtor = (IASTStandardFunctionDeclarator) scopeNode;
            nodes = dtor.getParameters();
        } else if (scopeNode instanceof IObjCASTKnRFunctionDeclarator) {
            IObjCASTKnRFunctionDeclarator dtor = (IObjCASTKnRFunctionDeclarator) scopeNode;
            nodes = dtor.getParameterDeclarations();
        } else if (scopeNode instanceof IASTForStatement) {
            final IASTForStatement forStmt = (IASTForStatement) scopeNode;
            nodes = new IASTNode[] { forStmt.getInitializerStatement() };
        }

        if (nodes != null) {
            int idx = -1;
            IASTNode node = nodes.length > 0 ? nodes[++idx] : null;
            while (node != null) {
                collectNames(node);
                if (idx > -1 && ++idx < nodes.length) {
                    node = nodes[idx];
                } else {
                    node = null;
                    if (nodes[0].getPropertyInParent() == IObjCASTKnRFunctionDeclarator.FUNCTION_PARAMETER
                            || nodes[0].getPropertyInParent() == IASTStandardFunctionDeclarator.FUNCTION_PARAMETER) {
                        // function body, we were looking at parameters, now
                        // check the body itself
                        IASTCompoundStatement compound = null;
                        if (scopeNode instanceof IASTCompoundStatement) {
                            compound = (IASTCompoundStatement) scopeNode;
                        } else if (scopeNode instanceof IASTFunctionDeclarator) {
                            IASTNode n = scopeNode.getParent();
                            while (n instanceof IASTDeclarator) {
                                n = n.getParent();
                            }
                            if (n instanceof IASTFunctionDefinition) {
                                compound = (IASTCompoundStatement) ((IASTFunctionDefinition) n).getBody();
                            }
                        }
                        if (compound != null) {
                            nodes = compound.getStatements();
                            if (nodes.length > 0) {
                                idx = 0;
                                node = nodes[0];
                            }
                        }
                    }
                }
            }
        }
    }

    private IBinding extractBinding(final IASTName candidate, boolean resolve, IASTName forName) {
        if (!resolve || acceptDeclaredAfter(forName) || ObjCVisitor.declaredBefore(candidate, forName)) {
            if (resolve && candidate != forName) {
                return candidate.resolveBinding();
            }
            return candidate.getBinding();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IScope#find(java.lang.String)
     */
    public IBinding[] find(String name) throws DOMException {
        return ObjCVisitor.findBindings(this, name);
    }

    public final IBinding getBinding(IASTName name, boolean resolve) {
        return getBinding(name, resolve, IIndexFileSet.EMPTY);
    }

    public final IBinding getBinding(IASTName name, boolean resolve, IIndexFileSet fileSet) {
        char[] c = name.toCharArray();
        if (c.length == 0) {
            return null;
        }

        populateCache();
        final int type = getNamespaceType(name);
        Object o = mapsToNameOrBinding[type].get(name.toCharArray());

        if (o instanceof IBinding) {
            return (IBinding) o;
        }

        if (o instanceof IASTName) {
            IBinding b = extractBinding((IASTName) o, resolve, name);
            if (b != null) {
                return b;
            }
        } else if (o instanceof IASTName[]) {
            for (IASTName n : ((IASTName[]) o)) {
                if (n == null) {
                    break;
                }
                IBinding b = extractBinding(n, resolve, name);
                if (b != null) {
                    return b;
                }
            }
        }

        IBinding result = null;
        if (resolve && physicalNode instanceof IASTTranslationUnit) {
            final IASTTranslationUnit tu = (IASTTranslationUnit) physicalNode;
            IIndex index = tu.getIndex();
            if (index != null) {
                try {
                    IBinding[] bindings = index.findBindings(name.toCharArray(), INDEX_FILTERS[type],
                            new NullProgressMonitor());
                    if (fileSet != null) {
                        bindings = fileSet.filterFileLocalBindings(bindings);
                    }
                    result = processIndexResults(name, bindings);
                } catch (CoreException ce) {
                    CCorePlugin.log(ce);
                }
            }
        }
        return result;
    }

    public IBinding getBinding(int namespaceType, char[] name) {
        Object o = mapsToNameOrBinding[namespaceType].get(name);
        if (o instanceof IBinding) {
            return (IBinding) o;
        }

        if (o instanceof IASTName) {
            return ((IASTName) o).resolveBinding();
        }

        if (o instanceof IASTName[]) {
            return ((IASTName[]) o)[0].resolveBinding();
        }
        return null;
    }

    public final IBinding[] getBindings(IASTName name, boolean resolve, boolean prefix) throws DOMException {
        return getBindings(name, resolve, prefix, IIndexFileSet.EMPTY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.c.ICScope#getBinding(org.eclipse.cdt.core
     * .dom.ast.IASTName, boolean)
     */
    public final IBinding[] getBindings(IASTName name, boolean resolve, boolean prefixLookup,
            IIndexFileSet fileSet) {
        char[] c = name.toCharArray();
        Object[] obj = null;

        populateCache();
        for (CharArrayObjectMap map : mapsToNameOrBinding) {
            if (prefixLookup) {
                Object[] keys = map.keyArray();
                for (Object key2 : keys) {
                    char[] key = (char[]) key2;
                    if (CharArrayUtils.equals(key, 0, c.length, c, true)) {
                        obj = ArrayUtil.append(obj, map.get(key));
                    }
                }
            } else {
                obj = ArrayUtil.append(obj, map.get(c));
            }
        }

        if (physicalNode instanceof IASTTranslationUnit) {
            final IASTTranslationUnit tu = (IASTTranslationUnit) physicalNode;
            IIndex index = tu.getIndex();
            if (index != null) {
                try {
                    IBinding[] bindings = prefixLookup ? index.findBindingsForPrefix(name.toCharArray(),
                            true, INDEX_FILTERS[NAMESPACE_TYPE_BOTH], null) : index.findBindings(name
                            .toCharArray(), INDEX_FILTERS[NAMESPACE_TYPE_BOTH], null);
                    if (fileSet != null) {
                        bindings = fileSet.filterFileLocalBindings(bindings);
                    }

                    obj = ArrayUtil.addAll(Object.class, obj, bindings);
                } catch (CoreException ce) {
                    CCorePlugin.log(ce);
                }
            }
        }
        obj = ArrayUtil.trim(Object.class, obj);
        IBinding[] result = null;

        for (Object element : obj) {
            if (element instanceof IBinding) {
                result = (IBinding[]) ArrayUtil.append(IBinding.class, result, element);
            } else {
                IASTName n = null;
                if (element instanceof IASTName) {
                    n = (IASTName) element;
                } else if (element instanceof IASTName[]) {
                    n = ((IASTName[]) element)[0];
                }
                if (n != null) {
                    IBinding b = n.getBinding();
                    if (b == null) {
                        if (resolve && n != name) {
                            b = n.resolveBinding();
                        }
                    }
                    if (b != null) {
                        result = (IBinding[]) ArrayUtil.append(IBinding.class, result, b);
                    }
                }
            }
        }

        return (IBinding[]) ArrayUtil.trim(IBinding.class, result);
    }

    public EScopeKind getKind() {
        return kind;
    }

    private int getNamespaceType(IASTName name) {
        ASTNodeProperty prop = name.getPropertyInParent();
        if (prop == IASTCompositeTypeSpecifier.TYPE_NAME || prop == IASTElaboratedTypeSpecifier.TYPE_NAME
                || prop == IASTEnumerationSpecifier.ENUMERATION_NAME
                || prop == ObjCVisitor.STRING_LOOKUP_TAGS_PROPERTY
                || prop == IObjCASTClassMemoryLayoutDeclaration.CMEMORYLAYOUT) {
            return NAMESPACE_TYPE_TAG;
        }

        return NAMESPACE_TYPE_OTHER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IScope#getParent()
     */
    public IScope getParent() {
        return ObjCVisitor.getContainingScope(physicalNode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IScope#getPhysicalNode()
     */
    public IASTNode getPhysicalNode() {
        return physicalNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IScope#getScopeName()
     */
    public IName getScopeName() {
        if (physicalNode instanceof IASTCompositeTypeSpecifier) {
            return ((IASTCompositeTypeSpecifier) physicalNode).getName();
        }
        return null;
    }

    /**
     * In case there was an ambiguity the cache has to be populated for a second
     * time. However, we do not clear any names in order not to loose bindings.
     */
    public void markAsUncached() {
        isCached = false;
    }

    public void populateCache() {
        if (isCached) {
            return;
        }

        doPopulateCache();
        isCached = true;
    }

    /**
     * Index results from global scope, differ from ast results from translation
     * unit scope. This routine is intended to fix results from the index to be
     * consistent with ast scope behaviour.
     * 
     * @param name
     *            the name as it occurs in the ast
     * @param bindings
     *            the set of candidate bindings
     * @return the appropriate binding, or null if no binding is appropriate for
     *         the ast name
     */
    private IBinding processIndexResults(IASTName name, IBinding[] bindings) {
        if (bindings.length != 1) {
            return null;
        }

        return bindings[0];
    }
}
