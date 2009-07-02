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
import org.eclipse.cdt.core.dom.ast.IASTInitializer;

/**
 * This interface represents a designated initializer. e.g. struct x y = { .z=4,
 * .t[1] = 3 };
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTDesignatedInitializer extends IASTInitializer {

    /**
     * Constant.
     */
    public static final IObjCASTDesignator[] EMPTY_DESIGNATOR_ARRAY = new IObjCASTDesignator[0];

    /**
     * <code>DESIGNATOR</code> represents the relationship between an
     * <code>IObjCASTDesignatedInitializer</code> and
     * <code>IObjCASTDesignator</code>.
     */
    public static final ASTNodeProperty DESIGNATOR = new ASTNodeProperty(
            "IObjCASTDesignatedInitializer.DESIGNATOR - relationship between IObjCASTDesignatedInitializer and IObjCASTDesignator"); //$NON-NLS-1$

    /**
     * Add a designator to this initializer.
     * 
     * @param designator
     *            <code>IObjCASTDesignator</code>
     */
    public void addDesignator(IObjCASTDesignator designator);

    /**
     * Get all of the designators.
     * 
     * @return <code>IObjCASTDesignator []</code>
     */
    public IObjCASTDesignator[] getDesignators();

    /**
     * <code>OPERAND</code> represents the relationship between
     * <code>IObjCASTDesignatedInitializer</code> and its
     * <code>IASTInitializer</code>.
     */
    public static final ASTNodeProperty OPERAND = new ASTNodeProperty(
            "IObjCASTDesignatedInitializer.OPERAND - RHS IASTInitializer for IObjCASTDesignatedInitializer"); //$NON-NLS-1$

    /**
     * Get the nested initializer.
     * 
     * @return <code>IASTInitializer</code>
     */
    public IASTInitializer getOperandInitializer();

    /**
     * Set the nested initializer.
     * 
     * @param rhs
     *            <code>IASTInitializer</code>
     */
    public void setOperandInitializer(IASTInitializer rhs);

    /**
     * @since 5.1
     */
    public IObjCASTDesignatedInitializer copy();
}
