/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Doug Schaefer (IBM) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

/**
 * C extension to IASTDeclSpecifier. (restrict keyword)
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTDeclSpecifier extends IASTDeclSpecifier {

    public boolean isProtocol();

    public void setProtocol(boolean value);

    /**
     * Is restrict keyword used?
     * 
     * @return boolean
     */
    public boolean isRestrict();

    /**
     * Set restrict to value.
     * 
     * @param value
     */
    public void setRestrict(boolean value);

    /**
     * @since 5.1
     */
    public IObjCASTDeclSpecifier copy();

}
