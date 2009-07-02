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
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTSwitchStatement extends ASTNode implements IASTSwitchStatement, IASTAmbiguityParent {

    private IASTStatement body;
    private IASTExpression controller;

    public ObjCASTSwitchStatement() {
    }

    public ObjCASTSwitchStatement(IASTExpression controller, IASTStatement body) {
        setControllerExpression(controller);
        setBody(body);
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
        if (controller != null) {
            if (!controller.accept(action)) {
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

    public ObjCASTSwitchStatement copy() {
        ObjCASTSwitchStatement copy = new ObjCASTSwitchStatement();
        copy.setControllerExpression(controller == null ? null : controller.copy());
        copy.setBody(body == null ? null : body.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTStatement getBody() {
        return body;
    }

    public IASTExpression getControllerExpression() {
        return controller;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (body == child) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            body = (IASTStatement) other;
        }
        if (child == controller) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            controller = (IASTExpression) other;
        }
    }

    public void setBody(IASTStatement body) {
        assertNotFrozen();
        this.body = body;
        if (body != null) {
            body.setParent(this);
            body.setPropertyInParent(BODY);
        }
    }

    public void setControllerExpression(IASTExpression controller) {
        assertNotFrozen();
        this.controller = controller;
        if (controller != null) {
            controller.setParent(this);
            controller.setPropertyInParent(CONTROLLER_EXP);
        }
    }
}
