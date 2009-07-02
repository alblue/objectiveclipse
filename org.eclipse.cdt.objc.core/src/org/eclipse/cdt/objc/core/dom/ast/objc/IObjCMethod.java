/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IFunction;

/**
 * Base interface for methods, also used for constructors.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCMethod extends IFunction, IObjCMember {
    public static final IObjCMethod[] EMPTY_OBJCMETHOD_ARRAY = new IObjCMethod[0];

    /**
     * Returns whether this is a class method or not.
     * 
     * @since 5.1
     */
    public boolean isClassMethod() throws DOMException;

    /**
     * @throws DOMException
     */
    public boolean isProtocolMethod() throws DOMException;

}
