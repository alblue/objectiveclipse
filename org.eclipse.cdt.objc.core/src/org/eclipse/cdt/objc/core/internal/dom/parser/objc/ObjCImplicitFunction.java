/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IScope;

/**
 * The CImplicitFunction is used to represent implicit functions that exist on
 * the translation unit but are not actually part of the physical AST created by
 * CDT.
 * 
 * An example is GCC built-in functions.
 * 
 * @author dsteffle
 */
public class ObjCImplicitFunction extends ObjCExternalFunction {

    private char[] name = null;
    private IParameter[] parms = null;
    private IScope scope = null;
    private boolean takesVarArgs = false;

    public ObjCImplicitFunction(char[] name, IScope scope, IFunctionType type, IParameter[] parms,
            boolean takesVarArgs) {
        super(null, null);
        this.name = name;
        this.scope = scope;
        this.type = type;
        this.parms = parms;
        this.takesVarArgs = takesVarArgs;
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
    public IParameter[] getParameters() {
        return parms;
    }

    @Override
    public IScope getScope() {
        return scope;
    }

    @Override
    public IFunctionType getType() {
        return type;
    }

    @Override
    public boolean takesVarArgs() {
        return takesVarArgs;
    }

}
