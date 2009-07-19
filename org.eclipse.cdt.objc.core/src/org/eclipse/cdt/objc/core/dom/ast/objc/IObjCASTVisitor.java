/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTCategorySpecifier;

/**
 * Interface for visitors to visit c-specific nodes.
 * 
 * @since 5.0
 */
public interface IObjCASTVisitor {

    /**
     * Visits a base class specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int leave(IObjCASTBaseSpecifier baseSpecifier);

    /**
     * Visits a category specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int leave(IObjCASTCategorySpecifier catSpecifier);

    /**
     * Visits a designator.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int leave(IObjCASTDesignator designator);

    /**
     * Visits a base class specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int leave(IObjCASTPropertyAttribute attr);

    /**
     * Visits a base class specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE},
     *         {@link ASTVisitor#PROCESS_SKIP} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int visit(IObjCASTBaseSpecifier baseSpecifier);

    /**
     * Visits a category specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE},
     *         {@link ASTVisitor#PROCESS_SKIP} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int visit(IObjCASTCategorySpecifier categorySpecifier);

    /**
     * Visits a designator.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE},
     *         {@link ASTVisitor#PROCESS_SKIP} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int visit(IObjCASTDesignator designator);

    /**
     * Visits a base class specifier of a composite type.
     * 
     * @return {@link ASTVisitor#PROCESS_CONTINUE},
     *         {@link ASTVisitor#PROCESS_SKIP} or
     *         {@link ASTVisitor#PROCESS_ABORT}
     */
    int visit(IObjCASTPropertyAttribute attr);

}
