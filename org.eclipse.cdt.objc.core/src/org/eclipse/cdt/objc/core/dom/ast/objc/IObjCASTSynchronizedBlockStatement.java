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

public interface IObjCASTSynchronizedBlockStatement extends IASTStatement {

    /**
     * <code>BODY</code> is the body of the try block.
     */
    public static final ASTNodeProperty BODY = new ASTNodeProperty(
            "IObjCASTSynchronizedBlockStatement.BODY - Body of synchronized block"); //$NON-NLS-1$

    /**
     * <code>BODY</code> is the body of the try block.
     */
    public static final ASTNodeProperty OBJECT = new ASTNodeProperty(
            "IObjCASTSynchronizedBlockStatement.OBJECT - Object of synchronized block"); //$NON-NLS-1$

    /**
     * @since 5.1
     */
    public IObjCASTSynchronizedBlockStatement copy();

    /**
     * @return <code>IASTStatement</code>
     */
    public IASTStatement getBody();

    /**
     * @return <code>IASTStatement</code>
     */
    public IASTExpression getSynchronizationObject();

    public void setBody(IASTStatement tryBlock);

    public void setSynchronizationObject(IASTExpression obj);

}
