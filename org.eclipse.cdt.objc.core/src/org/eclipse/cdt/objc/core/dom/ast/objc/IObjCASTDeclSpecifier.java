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
import org.eclipse.cdt.core.dom.ast.IASTName;

/**
 * C extension to IASTDeclSpecifier. (restrict keyword)
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTDeclSpecifier extends IASTDeclSpecifier {

    public IASTName getTypeCheck();

    /**
     * Is bycopy keyword used?
     * 
     * @return boolean
     */
    public boolean isByCopy();

    /**
     * Is byref keyword used?
     * 
     * @return boolean
     */
    public boolean isByRef();

    /**
     * Is in keyword used?
     * 
     * @return boolean
     */
    public boolean isIn();

    /**
     * Is inout keyword used?
     * 
     * @return boolean
     */
    public boolean isInOut();

    /**
     * Is oneway keyword used?
     * 
     * @return boolean
     */
    public boolean isOneWay();

    /**
     * Is out keyword used?
     * 
     * @return boolean
     */
    public boolean isOut();

    /**
     * Is this a virtual method defined in a protocol
     * 
     * @return boolean
     */
    public boolean isProtocol();

    /**
     * Is restrict keyword used?
     * 
     * @return boolean
     */
    public boolean isRestrict();

    /**
     * Set bycopy to value.
     * 
     * @param value
     */
    public void setByCopy(boolean value);

    /**
     * Set byref to value.
     * 
     * @param value
     */
    public void setByRef(boolean value);

    /**
     * Set in to value.
     * 
     * @param value
     */
    public void setIn(boolean value);

    /**
     * Set inout to value.
     * 
     * @param value
     */
    public void setInOut(boolean value);

    /**
     * Set oneway to value.
     * 
     * @param value
     */
    public void setOneWay(boolean value);

    /**
     * Set out to value.
     * 
     * @param value
     */
    public void setOut(boolean value);

    /**
     * Set protocol to value.
     * 
     * @param value
     */
    public void setProtocol(boolean value);

    /**
     * Set restrict to value.
     * 
     * @param value
     */
    public void setRestrict(boolean value);

    public void setTypeCheck(IASTName conformsToProtocol);

}
