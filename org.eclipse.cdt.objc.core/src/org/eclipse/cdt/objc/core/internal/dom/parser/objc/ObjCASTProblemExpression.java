/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Yuan Zhang / Beth Tibbitts
 * (IBM Research) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IType;

/**
 * @author jcamelon
 */
public class ObjCASTProblemExpression extends ObjCASTProblemOwner implements IASTProblemExpression {

    public ObjCASTProblemExpression() {
        super();
    }

    public ObjCASTProblemExpression(IASTProblem problem) {
        super(problem);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitExpressions) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        super.accept(action); // visits the problem
        if (action.shouldVisitExpressions) {
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

    public ObjCASTProblemExpression copy() {
        ObjCASTProblemExpression copy = new ObjCASTProblemExpression();
        copyBaseProblem(copy);
        return copy;
    }

    public IType getExpressionType() {
        return null;
    }

}
