/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 * Created on Jan 26, 2005
 */
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCExternalBinding;

/**
 * @author aniefer
 */
public class ObjCExternalFunction extends ObjCFunction implements IObjCExternalBinding {
    private IASTName name = null;
    private IASTTranslationUnit tu = null;

    public ObjCExternalFunction(IASTTranslationUnit tu, IASTName name) {
        super(null);
        this.name = name;
        this.tu = tu;
    }

    @Override
    public String getName() {
        return name.toString();
    }

    @Override
    public char[] getNameCharArray() {
        return name.toCharArray();
    }

    @Override
    public IScope getScope() {
        return tu.getScope();
    }

    @Override
    protected IASTTranslationUnit getTranslationUnit() {
        return tu;
    }

    @Override
    public IFunctionType getType() {
        IFunctionType t = super.getType();
        if (t == null) {
            type = new ObjCFunctionType(new ObjCBasicType(IBasicType.t_void, 0), IType.EMPTY_TYPE_ARRAY);
        }
        return type;
    }

    @Override
    public boolean isExtern() {
        return true;
    }
}
