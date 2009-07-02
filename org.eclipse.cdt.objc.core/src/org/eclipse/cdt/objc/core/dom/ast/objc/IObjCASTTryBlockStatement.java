/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

/**
 * This interface represents the try block statement. try { //body } catch( Exc
 * e ) { // handler } catch( ... ) { }
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTTryBlockStatement extends IASTStatement {

    /**
     * <code>BODY</code> is the body of the try block.
     */
    public static final ASTNodeProperty BODY = new ASTNodeProperty(
            "IObjCASTTryBlockStatement.BODY - Body of try block"); //$NON-NLS-1$

    /**
     * <code>FINALLY</code> is the body of the finally block of the try block.
     */
    public static final ASTNodeProperty FINALLY = new ASTNodeProperty(
            "IObjCASTTryBlockStatement.FINALLY - Body of finally block"); //$NON-NLS-1$

    /**
     * Set finally block.
     * 
     * @param tryBlock
     *            <code>IASTStatement</code>
     */
    public void setFinallyBlock(IASTStatement tryBlock);

    /**
     * Get finally block.
     * 
     * @return <code>IASTStatement</code>
     */
    public IASTStatement getFinallyBlock();

    /**
     * Set try body.
     * 
     * @param tryBlock
     *            <code>IASTStatement</code>
     */
    public void setTryBody(IASTStatement tryBlock);

    /**
     * Get try body.
     * 
     * @return <code>IASTStatement</code>
     */
    public IASTStatement getTryBody();

    /**
     * <code>CATCH_HANDLER</code> are the exception catching handlers.
     */
    public static final ASTNodeProperty CATCH_HANDLER = new ASTNodeProperty(
            "IObjCASTTryBlockStatement.CATCH_HANDLER - Exception catching handlers"); //$NON-NLS-1$

    /**
     * Add catch handler.
     * 
     * @param handler
     *            <code>IObjCASTCatchHandler</code>
     */
    public void addCatchHandler(IObjCASTCatchHandler handler);

    /**
     * Get the catch handlers.
     * 
     * @return <code>IObjCASTCatchHandler []</code>
     */
    public IObjCASTCatchHandler[] getCatchHandlers();

    /**
     * @since 5.1
     */
    public IObjCASTTryBlockStatement copy();

}
