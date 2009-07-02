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

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMessageSelectorExpression;

/**
 * Function call expression in C.
 */
@SuppressWarnings("restriction")
public class ObjCASTMessageSelectorExpression extends ObjCASTFunctionCallExpression implements
        IObjCASTMessageSelectorExpression {

    public ObjCASTMessageSelectorExpression() {
        super();
    }

    public ObjCASTMessageSelectorExpression(IASTExpression functionName, IASTExpression parameter) {
        super(functionName, parameter);
    }

    @Override
    public ObjCASTMessageSelectorExpression copy() {
        ObjCASTMessageSelectorExpression copy = new ObjCASTMessageSelectorExpression();
        copy.setFunctionNameExpression(getFunctionNameExpression() == null ? null
                : getFunctionNameExpression().copy());
        copy
                .setParameterExpression(getParameterExpression() == null ? null : getParameterExpression()
                        .copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

}
