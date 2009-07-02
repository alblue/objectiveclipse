/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Markus Schorn (Wind River
 * Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IBinding;

/**
 * Represents the relationship between a class and one of its base classes.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCBase extends Cloneable {
    public static final IObjCBase[] EMPTY_BASE_ARRAY = new IObjCBase[0];

    /**
     * @since 5.1
     */
    public IObjCBase clone();

    /**
     * The base class. Generally a ICPPClassType, but may be a
     * ICPPTemplateParameter. In the case of typedefs, the target type will be
     * returned instead of the typedef itself.
     */
    public IBinding getBase() throws DOMException;

    /**
     * Returns the name that specifies the base class.
     * 
     * @since 4.0
     */
    public IName getBaseSpecifierName();

    /**
     * Whether this is a protocol.
     */
    public boolean isProtocol() throws DOMException;

    /**
     * Used internally to change cloned bases.
     * 
     * @noreference This method is not intended to be referenced by clients.
     */
    public void setBase(IBinding base);
}
