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
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPropertyImplementationDeclaration;

@SuppressWarnings("restriction")
public class ObjCASTPropertyImplementationDeclaration extends ASTNode implements
        IObjCASTPropertyImplementationDeclaration, IASTAmbiguityParent {

    private IASTDeclarator[] declarators;
    private int declaratorsPos = -1;
    private IASTDeclSpecifier declSpecifier;

    public ObjCASTPropertyImplementationDeclaration() {
    }

    public ObjCASTPropertyImplementationDeclaration(IASTDeclSpecifier declSpecifier) {
        setDeclSpecifier(declSpecifier);
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

        if (declSpecifier != null) {
            if (!declSpecifier.accept(action)) {
                return false;
            }
        }
        IASTDeclarator[] dtors = getDeclarators();
        for (int i = 0; i < dtors.length; i++) {
            if (!dtors[i].accept(action)) {
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

    public void addDeclarator(IASTDeclarator d) {
        assertNotFrozen();
        if (d != null) {
            d.setParent(this);
            d.setPropertyInParent(DECLARATOR);
            declarators = (IASTDeclarator[]) ArrayUtil.append(IASTDeclarator.class, declarators,
                    ++declaratorsPos, d);
        }
    }

    public ObjCASTPropertyImplementationDeclaration copy() {
        ObjCASTPropertyImplementationDeclaration copy = new ObjCASTPropertyImplementationDeclaration();
        copy.setDeclSpecifier(declSpecifier == null ? null : declSpecifier.copy());

        for (IASTDeclarator declarator : getDeclarators()) {
            copy.addDeclarator(declarator == null ? null : declarator.copy());
        }

        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTDeclarator[] getDeclarators() {
        if (declarators == null) {
            return IASTDeclarator.EMPTY_DECLARATOR_ARRAY;
        }
        declarators = (IASTDeclarator[]) ArrayUtil.removeNullsAfter(IASTDeclarator.class, declarators,
                declaratorsPos);
        return declarators;
    }

    public IASTDeclSpecifier getDeclSpecifier() {
        return declSpecifier;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (declSpecifier == child) {
            other.setParent(child.getParent());
            other.setPropertyInParent(child.getPropertyInParent());
            declSpecifier = (IASTDeclSpecifier) other;
        } else {
            IASTDeclarator[] declarators = getDeclarators();
            for (int i = 0; i < declarators.length; i++) {
                if (declarators[i] == child) {
                    declarators[i] = (IASTDeclarator) other;
                    other.setParent(child.getParent());
                    other.setPropertyInParent(child.getPropertyInParent());
                    break;
                }
            }
        }
    }

    public void setDeclSpecifier(IASTDeclSpecifier declSpecifier) {
        assertNotFrozen();
        this.declSpecifier = declSpecifier;
        if (declSpecifier != null) {
            declSpecifier.setParent(this);
            declSpecifier.setPropertyInParent(DECL_SPECIFIER);
        }
    }
}
