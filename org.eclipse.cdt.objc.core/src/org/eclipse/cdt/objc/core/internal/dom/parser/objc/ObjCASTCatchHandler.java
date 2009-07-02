/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Markus Schorn (Wind River
 * Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCatchHandler;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTCatchHandler extends ASTNode implements IObjCASTCatchHandler, IASTAmbiguityParent {

    private IASTStatement body;
    private IASTDeclaration declaration;
    private boolean isDefault;
    private IScope scope;

    public ObjCASTCatchHandler() {
    }

    public ObjCASTCatchHandler(IASTDeclaration declaration, IASTStatement body) {
        setCatchBody(body);
        setDeclaration(declaration);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitStatements) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (declaration != null) {
            if (!declaration.accept(action)) {
                return false;
            }
        }
        if (body != null) {
            if (!body.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitStatements) {
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

    public ObjCASTCatchHandler copy() {
        ObjCASTCatchHandler copy = new ObjCASTCatchHandler();
        copy.setDeclaration(declaration == null ? null : declaration.copy());
        copy.setCatchBody(body == null ? null : body.copy());
        copy.setIsDefaultCatch(isDefault);
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTStatement getCatchBody() {
        return body;
    }

    public IASTDeclaration getDeclaration() {
        return declaration;
    }

    public IScope getScope() {
        if (scope == null) {
            scope = new ObjCScope(this, EScopeKind.eLocal);
        }
        return scope;
    }

    public boolean isDefaultCatch() {
        return isDefault;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (body == child) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            body = (IASTStatement) other;
        }
        if (declaration == child) {
            other.setParent(child.getParent());
            other.setPropertyInParent(child.getPropertyInParent());
            declaration = (IASTDeclaration) other;
        }

    }

    public void setCatchBody(IASTStatement compoundStatement) {
        assertNotFrozen();
        body = compoundStatement;
        if (compoundStatement != null) {
            compoundStatement.setParent(this);
            compoundStatement.setPropertyInParent(CATCH_BODY);
        }
    }

    public void setDeclaration(IASTDeclaration decl) {
        assertNotFrozen();
        declaration = decl;
        if (decl != null) {
            decl.setParent(this);
            decl.setPropertyInParent(DECLARATION);
        }
    }

    public void setIsDefaultCatch(boolean isDefault) {
        assertNotFrozen();
        this.isDefault = isDefault;
    }

}
