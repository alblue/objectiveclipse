/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
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
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypedefNameSpecifier;
import org.eclipse.core.runtime.PlatformObject;

/**
 * A K&R C parameter.
 */
@SuppressWarnings("restriction")
public class ObjCKnRParameter extends PlatformObject implements IParameter {
    final private IASTDeclaration declaration;
    final private IASTName name;

    public ObjCKnRParameter(IASTDeclaration declaration, IASTName name) {
        this.declaration = declaration;
        this.name = name;
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
        return name.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getNameCharArray()
     */
    public char[] getNameCharArray() {
        return name.toCharArray();
    }

    public IBinding getOwner() throws DOMException {
        return ObjCVisitor.findEnclosingFunction(declaration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getPhysicalNode()
     */
    public IASTNode getPhysicalNode() {
        return declaration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() {
        return ObjCVisitor.getContainingScope(declaration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#getType()
     */
    public IType getType() {
        IASTDeclSpecifier declSpec = null;
        if (declaration instanceof IASTSimpleDeclaration) {
            declSpec = ((IASTSimpleDeclaration) declaration).getDeclSpecifier();
        }

        if (declSpec != null && declSpec instanceof IObjCASTTypedefNameSpecifier) {
            IObjCASTTypedefNameSpecifier nameSpec = (IObjCASTTypedefNameSpecifier) declSpec;
            return (IType) nameSpec.getName().resolveBinding();
        } else if (declSpec != null && declSpec instanceof IASTElaboratedTypeSpecifier) {
            IASTElaboratedTypeSpecifier elabTypeSpec = (IASTElaboratedTypeSpecifier) declSpec;
            return (IType) elabTypeSpec.getName().resolveBinding();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IVariable#isAuto()
     */
    public boolean isAuto() {
        if (declaration instanceof IASTSimpleDeclaration) {
            return ((IASTSimpleDeclaration) declaration).getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_auto;
        }
        return false;
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
        if (declaration instanceof IASTSimpleDeclaration) {
            return ((IASTSimpleDeclaration) declaration).getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_register;
        }
        return false;
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
