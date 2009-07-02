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
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCatchHandler;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTryBlockStatement;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTTryBlockStatement extends ASTNode implements IObjCASTTryBlockStatement,
        IASTAmbiguityParent {

    private IObjCASTCatchHandler[] catchHandlers = null;

    private int catchHandlersPos = -1;

    private IASTStatement finallyBody;

    private IASTStatement tryBody;

    public ObjCASTTryBlockStatement() {
    }

    public ObjCASTTryBlockStatement(IASTStatement tryBody) {
        setTryBody(tryBody);
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
        if (tryBody != null) {
            if (!tryBody.accept(action)) {
                return false;
            }
        }

        IObjCASTCatchHandler[] handlers = getCatchHandlers();
        for (int i = 0; i < handlers.length; i++) {
            if (!handlers[i].accept(action)) {
                return false;
            }
        }

        if (finallyBody != null) {
            if (!finallyBody.accept(action)) {
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

    public void addCatchHandler(IObjCASTCatchHandler statement) {
        assertNotFrozen();
        if (statement != null) {
            catchHandlers = (IObjCASTCatchHandler[]) ArrayUtil.append(IObjCASTCatchHandler.class,
                    catchHandlers, ++catchHandlersPos, statement);
            statement.setParent(this);
            statement.setPropertyInParent(CATCH_HANDLER);
        }
    }

    public ObjCASTTryBlockStatement copy() {
        ObjCASTTryBlockStatement copy = new ObjCASTTryBlockStatement(tryBody == null ? null : tryBody.copy());
        for (IObjCASTCatchHandler handler : getCatchHandlers()) {
            copy.addCatchHandler(handler == null ? null : handler.copy());
        }
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IObjCASTCatchHandler[] getCatchHandlers() {
        if (catchHandlers == null) {
            return IObjCASTCatchHandler.EMPTY_CATCHHANDLER_ARRAY;
        }
        catchHandlers = (IObjCASTCatchHandler[]) ArrayUtil.removeNullsAfter(IObjCASTCatchHandler.class,
                catchHandlers, catchHandlersPos);
        return catchHandlers;
    }

    public IASTStatement getFinallyBlock() {
        return finallyBody;
    }

    public IASTStatement getTryBody() {
        return tryBody;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (tryBody == child) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            tryBody = (IASTStatement) other;
        }
    }

    public void setFinallyBlock(IASTStatement finallyBlock) {
        assertNotFrozen();
        finallyBody = finallyBlock;
        if (finallyBlock != null) {
            finallyBlock.setParent(this);
            finallyBlock.setPropertyInParent(FINALLY);
        }
    }

    public void setTryBody(IASTStatement tryBlock) {
        assertNotFrozen();
        tryBody = tryBlock;
        if (tryBlock != null) {
            tryBlock.setParent(this);
            tryBlock.setPropertyInParent(BODY);
        }
    }
}
