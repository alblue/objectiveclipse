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
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.IInternalVariable;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.Value;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Binding for a global or a local variable, serves as base class for fields.
 */
@SuppressWarnings("restriction")
public class ObjCVariable extends PlatformObject implements IVariable, IInternalVariable,
        IObjCInternalBinding {
    public static class CVariableProblem extends ProblemBinding implements IVariable {
        public CVariableProblem(IASTNode node, int id, char[] arg) {
            super(node, id, arg);
        }

        public IValue getInitialValue() {
            return null;
        }

        public IType getType() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isAuto() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isExtern() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isRegister() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isStatic() throws DOMException {
            throw new DOMException(this);
        }
    }

    private IASTName[] declarations = null;
    private IType type = null;

    public ObjCVariable(IASTName name) {
        declarations = new IASTName[] { name };
    }

    public void addDeclaration(IASTName name) {
        if (name != null && name.isActive()) {
            declarations = (IASTName[]) ArrayUtil.append(IASTName.class, declarations, name);
        }
    }

    private IASTDeclarator findDeclarator(IASTName name) {
        IASTNode node = name.getParent();
        if (!(node instanceof IASTDeclarator)) {
            return null;
        }

        return ASTQueries.findOutermostDeclarator((IASTDeclarator) node);
    }

    public IASTNode[] getDeclarations() {
        return declarations;
    }

    public IASTNode getDefinition() {
        return getPhysicalNode();
    }

    public IValue getInitialValue() {
        return getInitialValue(Value.MAX_RECURSION_DEPTH);
    }

    private IValue getInitialValue(IASTName name, int maxDepth) {
        IASTDeclarator dtor = findDeclarator(name);
        if (dtor != null) {
            IASTInitializer init = dtor.getInitializer();
            if (init instanceof IASTInitializerExpression) {
                IASTExpression expr = ((IASTInitializerExpression) init).getExpression();
                if (expr != null) {
                    return Value.create(expr, maxDepth);
                }
            }
            if (init != null) {
                return Value.UNKNOWN;
            }
        }
        return null;
    }

    public IValue getInitialValue(int maxDepth) {
        if (declarations != null) {
            for (IASTName decl : declarations) {
                if (decl == null) {
                    break;
                }
                final IValue val = getInitialValue(decl, maxDepth);
                if (val != null) {
                    return val;
                }
            }
        }
        return null;
    }

    public ILinkage getLinkage() {
        return Linkage.OBJC_LINKAGE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getName()
     */
    public String getName() {
        return declarations[0].toString();
    }

    public char[] getNameCharArray() {
        return declarations[0].toCharArray();
    }

    public IBinding getOwner() throws DOMException {
        if (declarations == null || declarations.length == 0) {
            return null;
        }

        return ObjCVisitor.findDeclarationOwner(declarations[0], true);
    }

    public IASTNode getPhysicalNode() {
        return declarations[0];
    }

    public String[] getQualifiedName() throws DOMException {
        return new String[] { getName() };
    }

    public char[][] getQualifiedNameCharArray() throws DOMException {
        return new char[][] { getNameCharArray() };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() {
        IASTDeclarator declarator = (IASTDeclarator) declarations[0].getParent();
        return ObjCVisitor.getContainingScope(declarator.getParent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#getType()
     */
    public IType getType() {
        if (type == null && declarations[0].getParent() instanceof IASTDeclarator) {
            type = ObjCVisitor.createType((IASTDeclarator) declarations[0].getParent());
        }
        return type;
    }

    public boolean hasStorageClass(int storage) {
        if (declarations == null) {
            return false;
        }

        for (int i = 0; i < declarations.length && declarations[i] != null; i++) {
            final IASTName name = declarations[i];

            IASTNode parent = name.getParent();
            while (!(parent instanceof IASTDeclaration)) {
                parent = parent.getParent();
            }

            if (parent instanceof IASTSimpleDeclaration) {
                IASTDeclSpecifier declSpec = ((IASTSimpleDeclaration) parent).getDeclSpecifier();
                if (declSpec.getStorageClass() == storage) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isAuto()
     */
    public boolean isAuto() {
        return hasStorageClass(IASTDeclSpecifier.sc_auto);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isExtern()
     */
    public boolean isExtern() {
        return hasStorageClass(IASTDeclSpecifier.sc_extern);
    }

    public boolean isGloballyQualified() throws DOMException {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isRegister()
     */
    public boolean isRegister() {
        return hasStorageClass(IASTDeclSpecifier.sc_register);
    }

    public boolean isStatic() {
        return hasStorageClass(IASTDeclSpecifier.sc_static);
    }
}
