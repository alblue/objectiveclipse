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

public interface IObjCASTPropertyImplementationDeclaration extends IASTDeclaration {

    public static final ASTNodeProperty DECL_SPECIFIER = new ASTNodeProperty(
            "IObjCASTPropertyImplementationDeclaration.DECL_SPECIFIER - IASTDeclSpecifier for IObjCASTPropertyImplementationDeclaration"); //$NON-NLS-1$

    public static final ASTNodeProperty DECLARATOR = new ASTNodeProperty(
            "IObjCASTPropertyImplementationDeclaration.DECLARATOR - IASTDeclarator for IObjCASTPropertyImplementationDeclaration"); //$NON-NLS-1$

    public void addDeclarator(IASTDeclarator declarator);

    /**
     * @since 5.1
     */
    public IObjCASTPropertyImplementationDeclaration copy();

    public IASTDeclarator[] getDeclarators();

    public IASTDeclSpecifier getDeclSpecifier();

    public void setDeclSpecifier(IASTDeclSpecifier declSpec);

}
