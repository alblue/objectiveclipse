/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Markus
 * Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.c.ICCompositeTypeScope;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.index.IIndexType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCBase;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCCompositeType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCField;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCMethod;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Represents structs and unions.
 */
@SuppressWarnings("restriction")
public class ObjCCompositeType extends PlatformObject implements IObjCCompositeType, IObjCInternalBinding {

    public static class ObjCCompositeTypeProblem extends ProblemBinding implements IObjCCompositeType {
        public ObjCCompositeTypeProblem(IASTNode node, int id, char[] arg) {
            super(node, id, arg);
        }

        public IField findField(String name) throws DOMException {
            throw new DOMException(this);
        }

        public IObjCMethod[] getAllMethods() throws DOMException {
            throw new DOMException(this);
        }

        public IObjCBase getBaseClass() throws DOMException {
            throw new DOMException(this);
        }

        public IBinding[] getCategories() throws DOMException {
            throw new DOMException(this);
        }

        public IScope getCompositeScope() throws DOMException {
            throw new DOMException(this);
        }

        public IObjCField[] getDeclaredFields() throws DOMException {
            throw new DOMException(this);
        }

        public IField[] getFields() throws DOMException {
            throw new DOMException(this);
        }

        public int getKey() throws DOMException {
            throw new DOMException(this);
        }

        public IObjCMethod[] getMethods() throws DOMException {
            throw new DOMException(this);
        }

        public IObjCBase[] getProtocols() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isAnonymous() throws DOMException {
            throw new DOMException(this);
        }
    }

    private boolean checked;
    private IASTName[] declarations = null;
    private IASTName definition;
    private ICompositeType typeInIndex;

    public ObjCCompositeType(IASTName name) {
        if (name.getPropertyInParent() == IASTCompositeTypeSpecifier.TYPE_NAME) {
            definition = name;
        } else {
            declarations = new IASTName[] { name };
        }
        name.setBinding(this);
    }

    public void addDeclaration(IASTName decl) {
        if (!decl.isActive() || decl.getPropertyInParent() != IASTElaboratedTypeSpecifier.TYPE_NAME) {
            return;
        }

        decl.setBinding(this);
        if (declarations == null || declarations.length == 0) {
            declarations = new IASTName[] { decl };
            return;
        }
        IASTName first = declarations[0];
        if (((ASTNode) first).getOffset() > ((ASTNode) decl).getOffset()) {
            declarations[0] = decl;
            decl = first;
        }
        declarations = (IASTName[]) ArrayUtil.append(IASTName.class, declarations, decl);
    }

    public void addDefinition(IObjCASTCompositeTypeSpecifier compositeTypeSpec) {
        if (compositeTypeSpec.isActive()) {
            definition = compositeTypeSpec.getName();
            compositeTypeSpec.getName().setBinding(this);
        }
    }

    private void checkForDefinition() {
        if (!checked && definition == null) {
            IASTNode declSpec = declarations[0].getParent();
            if (declSpec instanceof IObjCASTElaboratedTypeSpecifier) {
                IASTDeclSpecifier spec = ObjCVisitor
                        .findDefinition((IObjCASTElaboratedTypeSpecifier) declSpec);
                if (spec instanceof IObjCASTCompositeTypeSpecifier) {
                    IObjCASTCompositeTypeSpecifier compTypeSpec = (IObjCASTCompositeTypeSpecifier) spec;
                    definition = compTypeSpec.getName();
                    definition.setBinding(this);
                }
            }

            if (definition == null && typeInIndex == null) {
                final IASTTranslationUnit translationUnit = declSpec.getTranslationUnit();
                IIndex index = translationUnit.getIndex();
                if (index != null) {
                    typeInIndex = (ICompositeType) index.adaptBinding(this);
                }
            }
        }
        checked = true;
    }

    @Override
    public Object clone() {
        IType t = null;
        try {
            t = (IType) super.clone();
        } catch (CloneNotSupportedException e) {
            // not going to happen
        }
        return t;
    }

    public IField findField(String name) throws DOMException {
        IScope scope = getCompositeScope();
        if (scope == null) {
            return new ObjCField.CFieldProblem(declarations[0],
                    IProblemBinding.SEMANTIC_DEFINITION_NOT_FOUND, getNameCharArray());
        }

        final ObjCASTName astName = new ObjCASTName(name.toCharArray());
        astName.setPropertyInParent(ObjCVisitor.STRING_LOOKUP_PROPERTY);
        IBinding binding = scope.getBinding(astName, true);
        if (binding instanceof IField) {
            return (IField) binding;
        }

        return null;
    }

    public IObjCMethod[] getAllMethods() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public IObjCBase getBaseClass() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public IBinding[] getCategories() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.ICompositeType#getCompositeScope()
     */
    public IScope getCompositeScope() {
        checkForDefinition();
        if (definition != null) {
            return ((IASTCompositeTypeSpecifier) definition.getParent()).getScope();
        }
        // fwd-declarations must be backed up from the index
        if (typeInIndex != null) {
            try {
                IScope scope = typeInIndex.getCompositeScope();
                if (scope instanceof ICCompositeTypeScope) {
                    return scope;
                }
            } catch (DOMException e) {
                // index bindings don't throw DOMExeptions.
            }
        }
        return null;
    }

    public IASTNode[] getDeclarations() {
        return declarations;
    }

    public IObjCField[] getDeclaredFields() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    public IASTNode getDefinition() {
        return definition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.ICompositeType#getFields()
     */
    public IField[] getFields() throws DOMException {
        checkForDefinition();
        if (definition == null) {
            return new IField[] { new ObjCField.CFieldProblem(declarations[0],
                    IProblemBinding.SEMANTIC_DEFINITION_NOT_FOUND, getNameCharArray()) };
        }
        IObjCASTCompositeTypeSpecifier compSpec = (IObjCASTCompositeTypeSpecifier) definition.getParent();
        IASTDeclaration[] members = compSpec.getMembers();
        int size = members.length;
        IField[] fields = new IField[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                IASTNode node = members[i];
                if (node instanceof IASTSimpleDeclaration) {
                    IASTDeclarator[] declarators = ((IASTSimpleDeclaration) node).getDeclarators();
                    for (int j = 0; j < declarators.length; j++) {
                        IASTDeclarator declarator = declarators[j];
                        IASTName name = declarator.getName();
                        IBinding binding = name.resolveBinding();
                        if (binding != null) {
                            fields = (IField[]) ArrayUtil.append(IField.class, fields, binding);
                        }
                    }
                }
            }
        }
        return (IField[]) ArrayUtil.trim(IField.class, fields);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.ICompositeType#getKey()
     */
    public int getKey() {
        return (definition != null) ? ((IASTCompositeTypeSpecifier) definition.getParent()).getKey()
                : ((IASTElaboratedTypeSpecifier) declarations[0].getParent()).getKind();
    }

    public ILinkage getLinkage() {
        return Linkage.OBJC_LINKAGE;
    }

    public IObjCMethod[] getMethods() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getName()
     */
    public String getName() {
        if (definition != null) {
            return definition.toString();
        }

        return declarations[0].toString();
    }

    public char[] getNameCharArray() {
        if (definition != null) {
            return definition.toCharArray();
        }

        return declarations[0].toCharArray();
    }

    public IBinding getOwner() throws DOMException {
        IASTNode node = definition;
        if (node == null) {
            if (declarations != null && declarations.length > 0) {
                node = declarations[0];
            }
        }
        IBinding result = ObjCVisitor.findEnclosingFunction(node); // local or
        // global
        if (result != null) {
            return result;
        }

        if (definition != null && isAnonymous()) {
            return ObjCVisitor.findDeclarationOwner(definition, false);
        }
        return null;
    }

    public IASTNode getPhysicalNode() {
        return (definition != null) ? (IASTNode) definition : (IASTNode) declarations[0];
    }

    public IObjCBase[] getProtocols() throws DOMException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() throws DOMException {
        IASTDeclSpecifier declSpec = (IASTDeclSpecifier) ((definition != null) ? (IASTNode) definition
                .getParent() : declarations[0].getParent());
        IScope scope = ObjCVisitor.getContainingScope(declSpec);
        while (scope instanceof ICCompositeTypeScope) {
            scope = scope.getParent();
        }
        return scope;
    }

    public boolean isAnonymous() throws DOMException {
        if (getNameCharArray().length > 0 || definition == null) {
            return false;
        }

        IASTCompositeTypeSpecifier spec = ((IASTCompositeTypeSpecifier) definition.getParent());
        if (spec != null) {
            IASTNode node = spec.getParent();
            if (node instanceof IASTSimpleDeclaration) {
                if (((IASTSimpleDeclaration) node).getDeclarators().length == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IType#isSameType(org.eclipse.cdt.core.dom
     * .ast.IType)
     */
    public boolean isSameType(IType type) {
        if (type == this) {
            return true;
        }
        if (type instanceof ITypedef || type instanceof IIndexType) {
            return type.isSameType(this);
        }
        return false;
    }
}
