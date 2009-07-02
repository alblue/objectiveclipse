/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Doug Schaefer (IBM) - Initial API and implementation Markus
 * Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IType;

/**
 * Represents a member of a class. Adds in the visibility attribute.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCMember extends IBinding {

    public static final int v_private = IObjCASTVisibilityLabel.v_private;
    public static final int v_protected = IObjCASTVisibilityLabel.v_protected;
    public static final int v_public = IObjCASTVisibilityLabel.v_public;

    /**
     * Same as {@link #getOwner()}.
     */
    public ICompositeType getClassOwner() throws DOMException;

    /**
     * Returns the type of the member (function type or type of field)
     * 
     * @since 5.1
     */
    public IType getType() throws DOMException;

    /**
     * Returns the accessibility of the member.
     */
    public int getVisibility() throws DOMException;

}
