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

import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;

/**
 * C's elaborated type specifier. (same as IASTElaboratedTypeSpecifier, except
 * for the addition of the restrict keyword.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTElaboratedTypeSpecifier extends IASTElaboratedTypeSpecifier, IObjCASTDeclSpecifier {

    public static final int k_class = IASTElaboratedTypeSpecifier.k_last + 1;

    public static final int k_protocol = IASTElaboratedTypeSpecifier.k_last + 2;

    /**
     * @since 5.1
     */
    public IObjCASTElaboratedTypeSpecifier copy();
}
