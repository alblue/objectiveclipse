/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;

/**
 * Used to represent built-in variables that exist on the translation unit but
 * are not actually part of the physical AST created by CDT.
 * 
 * An example is the built-in variable __func__.
 */
public class ObjCBuiltinVariable extends ObjCVariable {
    private char[] name = null;
    private IScope scope = null;
    private IType type = null;

    public ObjCBuiltinVariable(IType type, char[] name, IScope scope) {
        super(null);
        this.type = type;
        this.name = name;
        this.scope = scope;
    }

    /**
     * returns null
     */
    @Override
    public IASTNode[] getDeclarations() {
        return null;
    }

    /**
     * returns null
     */
    @Override
    public IASTNode getDefinition() {
        return null;
    }

    @Override
    public String getName() {
        return String.valueOf(name);
    }

    @Override
    public char[] getNameCharArray() {
        return name;
    }

    @Override
    public IBinding getOwner() throws DOMException {
        return null;
    }

    @Override
    public IScope getScope() {
        return scope;
    }

    @Override
    public IType getType() {
        return type;
    }
}
