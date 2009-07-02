/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Doug Schaefer (IBM) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;

/**
 * This class represents a parameter declaration
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTMethodParameterDeclaration extends IASTParameterDeclaration {
    /**
     * Constant/sentinel.
     */
    public static final IObjCASTMethodParameterDeclaration[] EMPTY_METHODPARAMETERDECLARATION_ARRAY = new IObjCASTMethodParameterDeclaration[0];

    /**
     * <code>DECLARATOR</code> represents the relationship between an
     * <code>IASTParameterDeclaration</code> and its nested
     * <code>IASTDeclarator</code>.
     */
    public static final ASTNodeProperty SELECTOR = new ASTNodeProperty(
            "IObjCASTMethodParameterDeclaration.SELECTOR - IASTName for IObjCASTMethodParameterDeclaration"); //$NON-NLS-1$

    /**
     * Get the selector.
     * 
     * @return <code>IASTName</code>
     */
    public IASTName getSelector();

}
