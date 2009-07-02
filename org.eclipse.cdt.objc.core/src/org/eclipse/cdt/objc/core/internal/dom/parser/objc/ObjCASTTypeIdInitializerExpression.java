/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind
 * River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTTypeIdInitializerExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypeIdInitializerExpression;

/**
 * C-specific implementation adds nothing but the c-specific interface.
 */
@SuppressWarnings("restriction")
public class ObjCASTTypeIdInitializerExpression extends ASTTypeIdInitializerExpression implements
        IObjCASTTypeIdInitializerExpression {

    private ObjCASTTypeIdInitializerExpression() {
        super();
    }

    public ObjCASTTypeIdInitializerExpression(IASTTypeId typeId, IASTInitializer initializer) {
        super(typeId, initializer);
    }

    public ObjCASTTypeIdInitializerExpression copy() {
        ObjCASTTypeIdInitializerExpression copy = new ObjCASTTypeIdInitializerExpression();
        initializeCopy(copy);
        return copy;
    }

    public IType getExpressionType() {
        return ObjCVisitor.createType(getTypeId().getAbstractDeclarator());
    }
}
