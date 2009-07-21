/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodParameterDeclaration;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTMethodParameterDeclaration extends ASTNode implements IObjCASTMethodParameterDeclaration,
        IASTAmbiguityParent {

    private IASTDeclarator declarator;
    private IASTDeclSpecifier declSpec;

    public ObjCASTMethodParameterDeclaration() {
    }

    public ObjCASTMethodParameterDeclaration(IASTDeclSpecifier declSpec, IASTDeclarator declarator) {
        setDeclSpecifier(declSpec);
        setDeclarator(declarator);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitParameterDeclarations) {
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

        if (action.shouldVisitParameterDeclarations) {
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

    public ObjCASTMethodParameterDeclaration copy() {
        ObjCASTMethodParameterDeclaration copy = new ObjCASTMethodParameterDeclaration();
        copy.setDeclSpecifier(declSpec == null ? null : declSpec.copy());
        copy.setDeclarator(declarator == null ? null : declarator.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTDeclarator getDeclarator() {
        return declarator;
    }

    public IASTDeclSpecifier getDeclSpecifier() {
        return declSpec;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == declarator) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            declarator = (IASTDeclarator) other;
        }
    }

    public void setDeclarator(IASTDeclarator declarator) {
        assertNotFrozen();
        this.declarator = declarator;
        if (declarator != null) {
            declarator.setParent(this);
            declarator.setPropertyInParent(DECLARATOR);
        }
    }

    public void setDeclSpecifier(IASTDeclSpecifier declSpec) {
        assertNotFrozen();
        this.declSpec = declSpec;
        if (declSpec != null) {
            declSpec.setParent(this);
            declSpec.setPropertyInParent(DECL_SPECIFIER);
        }
    }

}
