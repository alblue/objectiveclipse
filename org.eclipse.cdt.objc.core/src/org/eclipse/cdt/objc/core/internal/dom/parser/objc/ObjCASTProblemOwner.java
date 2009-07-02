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
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemHolder;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
abstract class ObjCASTProblemOwner extends ASTNode implements IASTProblemHolder {

    private IASTProblem problem;

    public ObjCASTProblemOwner() {
    }

    public ObjCASTProblemOwner(IASTProblem problem) {
        setProblem(problem);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitProblems) {
            switch (action.visit(getProblem())) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
            switch (action.leave(getProblem())) {
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

    protected void copyBaseProblem(ObjCASTProblemOwner copy) {
        copy.setProblem(problem == null ? null : problem.copy());
        copy.setOffsetAndLength(this);
    }

    public IASTProblem getProblem() {
        return problem;
    }

    public void setProblem(IASTProblem p) {
        assertNotFrozen();
        problem = p;
        if (p != null) {
            p.setParent(this);
            p.setPropertyInParent(PROBLEM);
        }
    }
}
