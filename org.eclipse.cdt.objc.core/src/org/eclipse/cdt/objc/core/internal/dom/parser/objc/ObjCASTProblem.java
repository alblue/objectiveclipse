/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Yuan Zhang / Beth Tibbitts
 * (IBM Research) Anton Leherbauer (Wind River Systems) Markus Schorn (Wind
 * River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.internal.core.dom.parser.ASTProblem;

/**
 * C-specific implementation of ASTProblem, allows an action to visit a problem.
 */
@SuppressWarnings("restriction")
public class ObjCASTProblem extends ASTProblem {

    public ObjCASTProblem(int id, char[] arg, boolean isError) {
        super(id, arg, isError);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitProblems) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (action.shouldVisitProblems) {
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
    public ObjCASTProblem copy() {
        char[] arg = getArgument();
        ObjCASTProblem problem = new ObjCASTProblem(getID(), arg == null ? null : arg.clone(), isError());
        problem.setOffsetAndLength(this);
        return problem;
    }
}
