/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
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
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Represents the parameter of a function.
 */
@SuppressWarnings("restriction")
public class ObjCParameter extends PlatformObject implements IParameter {
    public static class CParameterProblem extends ProblemBinding implements IParameter {
        public CParameterProblem(IASTNode node, int id, char[] arg) {
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

    private IASTName[] declarations;
    private IType type = null;

    public ObjCParameter(IASTName parameterName) {
        declarations = new IASTName[] { parameterName };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#getType()
     */

    /**
     * @param name
     */
    public void addDeclaration(IASTName name) {
        if (name != null && name.isActive()) {
            declarations = (IASTName[]) ArrayUtil.append(IASTName.class, declarations, name);
        }
    }

    public IValue getInitialValue() {
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
        IASTName name = getPrimaryDeclaration();
        if (name != null) {
            return name.toString();
        }
        return ObjCVisitor.EMPTY_STRING;
    }

    public char[] getNameCharArray() {
        IASTName name = getPrimaryDeclaration();
        if (name != null) {
            return name.toCharArray();
        }
        return ObjCVisitor.EMPTY_CHAR_ARRAY;
    }

    public IBinding getOwner() throws DOMException {
        if (declarations == null || declarations.length == 0) {
            return null;
        }

        return ObjCVisitor.findEnclosingFunction(declarations[0]);
    }

    private IASTName getPrimaryDeclaration() {
        if (declarations != null) {
            for (int i = 0; i < declarations.length && declarations[i] != null; i++) {
                IASTNode node = declarations[i].getParent();
                while (!(node instanceof IASTDeclaration)) {
                    node = node.getParent();
                }

                if (node.getPropertyInParent() == IObjCASTKnRFunctionDeclarator.FUNCTION_PARAMETER
                        || node instanceof IASTFunctionDefinition) {
                    return declarations[i];
                }
            }
            return declarations[0];
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() {
        // IASTParameterDeclaration or IASTSimpleDeclaration
        for (int i = 0; i < declarations.length; i++) {
            IASTNode parent = declarations[i].getParent();
            if (parent instanceof IObjCASTKnRFunctionDeclarator) {
                parent = parent.getParent();
                return ((IASTCompoundStatement) ((IASTFunctionDefinition) parent).getBody()).getScope();
            }

            IASTNode fdtorNode = parent.getParent().getParent();
            if (fdtorNode instanceof IASTFunctionDeclarator) {
                IASTFunctionDeclarator fdtor = (IASTFunctionDeclarator) fdtorNode;
                parent = fdtor.getParent();
                if (parent instanceof IASTFunctionDefinition) {
                    return ((IASTCompoundStatement) ((IASTFunctionDefinition) parent).getBody()).getScope();
                }
            }
        }
        // TODO: if not definition, find definition
        return null;
    }

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
            IASTNode parent = declarations[i].getParent();
            while (!(parent instanceof IASTParameterDeclaration) && !(parent instanceof IASTDeclaration)) {
                parent = parent.getParent();
            }

            IASTDeclSpecifier declSpec = null;
            if (parent instanceof IASTSimpleDeclaration) {
                declSpec = ((IASTSimpleDeclaration) parent).getDeclSpecifier();
            } else if (parent instanceof IASTParameterDeclaration) {
                declSpec = ((IASTParameterDeclaration) parent).getDeclSpecifier();
            }
            if (declSpec != null) {
                return declSpec.getStorageClass() == storage;
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
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isRegister()
     */
    public boolean isRegister() {
        return hasStorageClass(IASTDeclSpecifier.sc_register);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isStatic()
     */
    public boolean isStatic() {
        return false;
    }
}
