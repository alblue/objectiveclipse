/*******************************************************************************
 * Copyright (c) 2009, Ryan Rusaw and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Ryan Rusaw - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

public interface IObjCASTPropertyDeclSpecifier extends IObjCASTDeclSpecifier {

    public static final ASTNodeProperty ATTRIBUTE = new ASTNodeProperty(
            "IObjCASTPropertyDeclaration.ATTRIBUTE - IObjCASTPropertyAttribute for IObjCASTPropertyDeclaration"); //$NON-NLS-1$

    /**
     * Property __strong storage modifier
     */
    public static final int sc__strong = IASTDeclSpecifier.sc_last + 10;

    /**
     * Property __weak storage modifier
     */
    public static final int sc__weak = IASTDeclSpecifier.sc_last + 11;

    /**
     * Property implementation \@dynamic property
     */
    public static final int sc_dynamic = IASTDeclSpecifier.sc_last + 12;

    /**
     * Property IBOutlet storage modifier
     */
    public static final int sc_IBOutlet = IASTDeclSpecifier.sc_last + 13;

    /**
     * Property implementation \@synthesize property
     */
    public static final int sc_synthesized = IASTDeclSpecifier.sc_last + 14;

    public void addAttribute(IObjCASTPropertyAttribute attr);

    public IObjCASTPropertyDeclSpecifier copy();

    public IObjCASTPropertyAttribute[] getAttributes();

}
