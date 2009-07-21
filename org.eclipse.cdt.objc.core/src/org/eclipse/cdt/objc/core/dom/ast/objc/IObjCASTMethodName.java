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
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;

/**
 * This interface is a method names in ObjC.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTMethodName extends IASTName, IASTNameOwner {

    /**
     * Each IASTName segment has property being <code>SEGMENT_NAME</code>.
     */
    public static final ASTNodeProperty SELECTOR_NAME = new ASTNodeProperty(
            "IObjCASTMethodName.SELECTOR_NAME - An IASTName segment"); //$NON-NLS-1$

    /**
     * Add a subname.
     * 
     * @param name
     *            <code>IASTName</code>
     */
    public void addSelector(IASTName name);

    /**
     * @since 5.1
     */
    public IObjCASTMethodName copy();

    /**
     * Get all subnames.
     * 
     * @return <code>IASTName []</code>
     */
    public IASTName[] getSelectors();
}
