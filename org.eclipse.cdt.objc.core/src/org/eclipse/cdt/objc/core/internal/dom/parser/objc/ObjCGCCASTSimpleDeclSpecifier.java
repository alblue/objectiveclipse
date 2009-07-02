/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Emanuel Graf
 * IFS - Bugfix for #198257
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTSimpleDeclSpecifier;

/**
 * @author aniefer
 * 
 */
@SuppressWarnings("restriction")
public class ObjCGCCASTSimpleDeclSpecifier extends ObjCASTSimpleDeclSpecifier implements
        IObjCGCCASTSimpleDeclSpecifier, IASTAmbiguityParent {

    private IASTExpression typeOfExpression;

    public ObjCGCCASTSimpleDeclSpecifier() {
    }

    public ObjCGCCASTSimpleDeclSpecifier(IASTExpression typeofExpression) {
        setTypeofExpression(typeofExpression);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDeclSpecifiers) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (typeOfExpression != null) {
            if (!typeOfExpression.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitDeclSpecifiers) {
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

    @Override
    public ObjCGCCASTSimpleDeclSpecifier copy() {
        ObjCGCCASTSimpleDeclSpecifier copy = new ObjCGCCASTSimpleDeclSpecifier();
        copySimpleDeclSpec(copy);
        copy.setTypeofExpression(typeOfExpression == null ? null : typeOfExpression.copy());
        return copy;
    }

    public IASTExpression getTypeofExpression() {
        return typeOfExpression;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == typeOfExpression) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            typeOfExpression = (IASTExpression) other;
        }
    }

    public void setTypeofExpression(IASTExpression typeofExpression) {
        typeOfExpression = typeofExpression;
        if (typeofExpression != null) {
            typeofExpression.setParent(this);
            typeofExpression.setPropertyInParent(TYPEOF_EXPRESSION);
        }
    }
}
