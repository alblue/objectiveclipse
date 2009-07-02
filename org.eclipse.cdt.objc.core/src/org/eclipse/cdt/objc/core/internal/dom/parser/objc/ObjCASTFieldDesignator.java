/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFieldDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisitor;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTFieldDesignator extends ASTNode implements IObjCASTFieldDesignator {

    private IASTName name;

    public ObjCASTFieldDesignator() {
    }

    public ObjCASTFieldDesignator(IASTName name) {
        setName(name);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDesignators && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (name != null) {
            if (!name.accept(action)) {
                return false;
            }
        }
        if (action.shouldVisitDesignators && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).leave(this)) {
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

    public ObjCASTFieldDesignator copy() {
        ObjCASTFieldDesignator copy = new ObjCASTFieldDesignator(name == null ? null : name.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTName getName() {
        return name;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(FIELD_NAME);
        }
    }
}
