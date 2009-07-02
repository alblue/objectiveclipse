/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationListOwner;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.c.ICASTCompositeTypeSpecifier;

/**
 * Structs and Unions in C can be qualified w/restrict keyword.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTCompositeTypeSpecifier extends IASTCompositeTypeSpecifier, IObjCASTDeclSpecifier,
        IASTDeclarationListOwner {

    /**
     * Base Specifiers are where a class or category expresses from whom it
     * inherits from (classes & protocols).
     * 
     * @author jcamelon
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public static interface IObjCASTBaseSpecifier extends IASTNode, IASTNameOwner {

        /**
         * Constant.
         */
        public static final IObjCASTBaseSpecifier[] EMPTY_BASESPECIFIER_ARRAY = new IObjCASTBaseSpecifier[0];

        /**
         * <code>NAME</code> is the name of the base class.
         */
        public static final ASTNodeProperty NAME = new ASTNodeProperty(
                "IObjCASTBaseSpecifier.NAME - Name of base class or protocol"); //$NON-NLS-1$

        /**
         * @since 5.1
         */
        public IObjCASTBaseSpecifier copy();

        /**
         * Get the name.
         * 
         * @return <code>IASTName</code>
         */
        public IASTName getName();

        /**
         * @return boolean
         */
        public boolean isProtocol();

        /**
         * Set the name.
         * 
         * @param name
         *            <code>IASTName</code>
         */
        public void setName(IASTName name);

        /**
         * @param value
         *            boolean
         */
        public void setProtocol(boolean value);

    }

    /**
     * <code>BASE_SPECIFIER</code> is the base.
     */
    public static final ASTNodeProperty BASE_SPECIFIER = new ASTNodeProperty(
            "IObjCASTCompositeTypeSpecifier.BASE_SPECIFIER - Expresses the subclass role"); //$NON-NLS-1$

    /**
     * <code>k_class</code> obj-c introduces the category concept for composite
     * types.
     */
    public static final int k_category = IASTCompositeTypeSpecifier.k_last + 1;

    /**
     * <code>k_class</code> obj-c introduces the class concept for composite
     * types.
     */
    public static final int k_class = IASTCompositeTypeSpecifier.k_last + 2;

    /**
     * <code>k_protocol</code> obj-c introduces the protocol concept for
     * composite types.
     */
    public static final int k_protocol = ICASTCompositeTypeSpecifier.k_last + 3;

    /**
     * <code>k_last</code> allows for subinterfaces to extend the kind type.
     */
    public static final int k_last = k_protocol;

    public void addBaseSpecifier(IObjCASTBaseSpecifier baseSpec);

    /**
     * @since 5.1
     */
    public IObjCASTCompositeTypeSpecifier copy();

    public IObjCASTBaseSpecifier[] getBaseSpecifiers();

}
