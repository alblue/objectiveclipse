/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTTypeId extends ASTNode implements IASTTypeId {

    private IASTDeclarator declarator;
    private IASTDeclSpecifier declSpecifier;

    public ObjCASTTypeId() {
    }

    public ObjCASTTypeId(IASTDeclSpecifier declSpecifier, IASTDeclarator declarator) {
        setDeclSpecifier(declSpecifier);
        setAbstractDeclarator(declarator);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitTypeIds) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (declSpecifier != null) {
            if (!declSpecifier.accept(action)) {
                return false;
            }
        }
        if (declarator != null) {
            if (!declarator.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitTypeIds) {
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

    public ObjCASTTypeId copy() {
        ObjCASTTypeId copy = new ObjCASTTypeId();
        copy.setDeclSpecifier(declSpecifier == null ? null : declSpecifier.copy());
        copy.setAbstractDeclarator(declarator == null ? null : declarator.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTDeclarator getAbstractDeclarator() {
        return declarator;
    }

    public IASTDeclSpecifier getDeclSpecifier() {
        return declSpecifier;
    }

    public void setAbstractDeclarator(IASTDeclarator abstractDeclarator) {
        assertNotFrozen();
        declarator = abstractDeclarator;
        if (abstractDeclarator != null) {
            abstractDeclarator.setParent(this);
            abstractDeclarator.setPropertyInParent(ABSTRACT_DECLARATOR);
        }

    }

    public void setDeclSpecifier(IASTDeclSpecifier declSpec) {
        assertNotFrozen();
        declSpecifier = declSpec;
        if (declSpec != null) {
            declSpec.setParent(this);
            declSpec.setPropertyInParent(DECL_SPECIFIER);
        }
    }
}
