/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPointer;

@SuppressWarnings("restriction")
public class ObjCASTPointer extends ASTNode implements IObjCASTPointer {

    private boolean isConst;
    private boolean isRestrict;
    private boolean isVolatile;

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitPointerOperators) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
            }
            if (action.leave(this) == ASTVisitor.PROCESS_ABORT) {
                return false;
            }
        }
        return true;
    }

    public ObjCASTPointer copy() {
        ObjCASTPointer copy = new ObjCASTPointer();
        copy.isRestrict = isRestrict;
        copy.isVolatile = isVolatile;
        copy.isConst = isConst;
        copy.setOffsetAndLength(this);
        return copy;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isRestrict() {
        return isRestrict;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setConst(boolean value) {
        assertNotFrozen();
        isConst = value;
    }

    public void setRestrict(boolean value) {
        assertNotFrozen();
        isRestrict = value;
    }

    public void setVolatile(boolean value) {
        assertNotFrozen();
        isVolatile = value;
    }
}
