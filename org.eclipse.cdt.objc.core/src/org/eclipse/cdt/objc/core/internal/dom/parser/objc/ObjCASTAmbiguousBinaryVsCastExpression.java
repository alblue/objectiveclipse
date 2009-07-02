/*******************************************************************************
 * Copyright (c) 2008, 2009 Wind River Systems, Inc. and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousBinaryVsCastExpression;

@SuppressWarnings("restriction")
public class ObjCASTAmbiguousBinaryVsCastExpression extends ASTAmbiguousBinaryVsCastExpression {

    public ObjCASTAmbiguousBinaryVsCastExpression(IASTBinaryExpression binaryExpr, IASTCastExpression castExpr) {
        super(binaryExpr, castExpr);
    }
}
