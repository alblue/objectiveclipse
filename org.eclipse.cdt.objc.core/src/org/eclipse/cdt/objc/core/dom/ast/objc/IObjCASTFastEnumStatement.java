/*******************************************************************************
 * Copyright (c) 2009, Ryan Rusaw and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Ryan Rusaw - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

public interface IObjCASTFastEnumStatement extends IASTStatement {

    public static final ASTNodeProperty EXPRESSION = new ASTNodeProperty(
            "IObjCASTFastEnumStatement.Expression - IASTExpression (operand) for IObjCASTFastEnumStatement"); //$NON-NLS-1$

    public static final ASTNodeProperty STATEMENT = new ASTNodeProperty(
            "IObjCASTFastEnumStatement.Declaration - IASTStatement (operand) for IObjCASTFastEnumStatement"); //$NON-NLS-1$

    /**
     * @since 5.1
     */
    public IObjCASTFastEnumStatement copy();

    IASTExpression getExpression();

    IASTStatement getStatement();

    void setExpression(IASTExpression e);

    void setStatement(IASTStatement s);
}
