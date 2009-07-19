/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Niefer (IBM Corporation) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IBasicType;

/**
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCBasicType extends IBasicType {

    // Extra types in ObjC
    public static final int t_Bool = IObjCASTSimpleDeclSpecifier.t_Bool;

    public static final int t_BOOL = IObjCASTSimpleDeclSpecifier.t_BOOL;

    public static final int t_id = IObjCASTSimpleDeclSpecifier.t_id;

    public static final int t_IMP = IObjCASTSimpleDeclSpecifier.t_IMP;

    public static final int t_SEL = IObjCASTSimpleDeclSpecifier.t_SEL;

    /**
     * Is complex number? e.g. _Complex t;
     * 
     * @return true if it is a complex number, false otherwise
     */
    public boolean isComplex();

    /**
     * Is imaginary number? e.g. _Imaginr
     * 
     * @return true if it is an imaginary number, false otherwise
     */
    public boolean isImaginary();

    public boolean isLongLong() throws DOMException;
}
