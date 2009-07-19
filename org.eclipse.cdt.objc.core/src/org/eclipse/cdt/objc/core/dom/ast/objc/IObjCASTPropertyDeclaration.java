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
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;

public interface IObjCASTPropertyDeclaration extends IASTDeclaration {

    public static final ASTNodeProperty DECL_SPECIFIER = new ASTNodeProperty(
            "IObjCASTPropertyDeclaration.DECL_SPECIFIER - IASTDeclSpecifier for IASTParameterDeclaration"); //$NON-NLS-1$

    public static final ASTNodeProperty DECLARATOR = new ASTNodeProperty(
            "IObjCASTPropertyDeclaration.DECLARATOR - IASTDeclarator for IObjCASTPropertyDeclaration"); //$NON-NLS-1$

    public static final IObjCASTPropertyDeclaration[] EMPTY_PROPERTYDECLARATION_ARRAY = new IObjCASTPropertyDeclaration[0];

    /**
     * @since 5.1
     */
    public IObjCASTPropertyDeclaration copy();

    public IASTDeclarator getDeclarator();

    public IASTDeclSpecifier getDeclSpec();

    public void setDeclarator(IASTDeclarator declarator);

    public void setDeclSpec(IASTDeclSpecifier declSpec);

}
