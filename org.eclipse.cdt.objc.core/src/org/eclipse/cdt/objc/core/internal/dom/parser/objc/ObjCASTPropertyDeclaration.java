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

package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPropertyDeclaration;

@SuppressWarnings("restriction")
public class ObjCASTPropertyDeclaration extends ASTNode implements IObjCASTPropertyDeclaration {

    private IASTDeclarator declarator;
    private IASTDeclSpecifier declSpec;

    public ObjCASTPropertyDeclaration() {
    }

    public ObjCASTPropertyDeclaration(IASTDeclSpecifier declSpec, IASTDeclarator declarator) {
        setDeclSpec(declSpec);
        setDeclarator(declarator);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDeclarations) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (declSpec != null) {
            if (!declSpec.accept(action)) {
                return false;
            }
        }

        if (declarator != null) {
            if (!declarator.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitDeclarations) {
            switch (action.leave(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        return true;
    }

    public IObjCASTPropertyDeclaration copy() {
        ObjCASTPropertyDeclaration copy = new ObjCASTPropertyDeclaration();
        copy.setDeclarator(declarator == null ? null : declarator.copy());
        copy.setDeclSpec(declSpec == null ? null : declSpec.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTDeclarator getDeclarator() {
        return declarator;
    }

    public IASTDeclSpecifier getDeclSpec() {
        return declSpec;
    }

    public void setDeclarator(IASTDeclarator declarator) {
        assertNotFrozen();
        this.declarator = declarator;
        if (declarator != null) {
            declarator.setParent(this);
            declarator.setPropertyInParent(DECLARATOR);
        }
    }

    public void setDeclSpec(IASTDeclSpecifier declSpec) {
        assertNotFrozen();
        this.declSpec = declSpec;
        if (declSpec != null) {
            declSpec.setParent(this);
            declSpec.setPropertyInParent(DECL_SPECIFIER);
        }
    }

}
