/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Markus
 * Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.c.ICFunctionScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTFunctionDefinition extends ASTNode implements IASTFunctionDefinition, IASTAmbiguityParent {

    private IASTStatement bodyStatement;
    private IASTFunctionDeclarator declarator;
    private IASTDeclSpecifier declSpecifier;
    private ICFunctionScope scope;

    public ObjCASTFunctionDefinition() {
    }

    public ObjCASTFunctionDefinition(IASTDeclSpecifier declSpecifier, IASTFunctionDeclarator declarator,
            IASTStatement bodyStatement) {
        setDeclSpecifier(declSpecifier);
        setDeclarator(declarator);
        setBody(bodyStatement);
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
        final IASTDeclarator outerDtor = ObjCVisitor.findOutermostDeclarator(declarator);
        if (outerDtor != null) {
            if (!outerDtor.accept(action)) {
                return false;
            }
        }
        if (bodyStatement != null) {
            if (!bodyStatement.accept(action)) {
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

    public ObjCASTFunctionDefinition copy() {
        ObjCASTFunctionDefinition copy = new ObjCASTFunctionDefinition();
        copy.setDeclSpecifier(declSpecifier == null ? null : declSpecifier.copy());

        if (declarator != null) {
            IASTDeclarator outer = ObjCVisitor.findOutermostDeclarator(declarator);
            outer = outer.copy();
            copy.setDeclarator((IASTFunctionDeclarator) ObjCVisitor.findTypeRelevantDeclarator(outer));
        }

        copy.setBody(bodyStatement == null ? null : bodyStatement.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTStatement getBody() {
        return bodyStatement;
    }

    public IASTFunctionDeclarator getDeclarator() {
        return declarator;
    }

    public IASTDeclSpecifier getDeclSpecifier() {
        return declSpecifier;
    }

    public IScope getScope() {
        if (scope == null) {
            scope = new ObjCFunctionScope(this);
        }
        return scope;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (bodyStatement == child) {
            other.setPropertyInParent(bodyStatement.getPropertyInParent());
            other.setParent(bodyStatement.getParent());
            bodyStatement = (IASTStatement) other;
        }
    }

    public void setBody(IASTStatement statement) {
        assertNotFrozen();
        bodyStatement = statement;
        if (statement != null) {
            statement.setParent(this);
            statement.setPropertyInParent(FUNCTION_BODY);
        }
    }

    public void setDeclarator(IASTFunctionDeclarator declarator) {
        assertNotFrozen();
        this.declarator = declarator;
        if (declarator != null) {
            IASTDeclarator outerDtor = ObjCVisitor.findOutermostDeclarator(declarator);
            outerDtor.setParent(this);
            outerDtor.setPropertyInParent(DECLARATOR);
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
