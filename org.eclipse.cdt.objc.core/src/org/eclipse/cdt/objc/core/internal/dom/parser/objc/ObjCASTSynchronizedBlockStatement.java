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
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSynchronizedBlockStatement;

@SuppressWarnings("restriction")
public class ObjCASTSynchronizedBlockStatement extends ASTNode implements IObjCASTSynchronizedBlockStatement,
        IASTAmbiguityParent {

    private IASTStatement body;
    private IASTExpression syncObj;

    public ObjCASTSynchronizedBlockStatement() {
    }

    public ObjCASTSynchronizedBlockStatement(IASTExpression obj, IASTStatement stmt) {
        setBody(stmt);
        setSynchronizationObject(obj);
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

        if (syncObj != null) {
            if (!syncObj.accept(action)) {
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

    public IObjCASTSynchronizedBlockStatement copy() {
        ObjCASTSynchronizedBlockStatement copy = new ObjCASTSynchronizedBlockStatement();
        copy.setSynchronizationObject(syncObj == null ? null : syncObj.copy());
        copy.setBody(body == null ? null : body.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTStatement getBody() {
        return body;
    }

    public IASTExpression getSynchronizationObject() {
        return syncObj;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (body == child) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            body = (IASTStatement) other;
        }
    }

    public void setBody(IASTStatement block) {
        assertNotFrozen();
        body = block;
        if (block != null) {
            block.setParent(this);
            block.setPropertyInParent(BODY);
        }
    }

    public void setSynchronizationObject(IASTExpression obj) {
        assertNotFrozen();
        syncObj = obj;
        if (obj != null) {
            obj.setParent(this);
            obj.setPropertyInParent(OBJECT);
        }
    }

}
