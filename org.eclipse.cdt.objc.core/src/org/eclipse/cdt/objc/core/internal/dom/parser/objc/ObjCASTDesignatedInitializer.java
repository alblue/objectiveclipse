/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Yuan Zhang / Beth Tibbitts
 * (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDesignatedInitializer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDesignator;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTDesignatedInitializer extends ASTNode implements IObjCASTDesignatedInitializer {

    private IObjCASTDesignator[] designators = null;

    int designatorsPos = -1;

    private IASTInitializer rhs;

    public ObjCASTDesignatedInitializer() {
    }

    public ObjCASTDesignatedInitializer(IASTInitializer operandInitializer) {
        setOperandInitializer(operandInitializer);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitInitializers) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        IObjCASTDesignator[] ds = getDesignators();
        for (int i = 0; i < ds.length; i++) {
            if (!ds[i].accept(action)) {
                return false;
            }
        }
        if (rhs != null) {
            if (!rhs.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitInitializers) {
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

    public void addDesignator(IObjCASTDesignator designator) {
        assertNotFrozen();
        if (designator != null) {
            designator.setParent(this);
            designator.setPropertyInParent(DESIGNATOR);
            designators = (IObjCASTDesignator[]) ArrayUtil.append(IObjCASTDesignator.class, designators,
                    ++designatorsPos, designator);
        }
    }

    public ObjCASTDesignatedInitializer copy() {
        ObjCASTDesignatedInitializer copy = new ObjCASTDesignatedInitializer(rhs == null ? null : rhs.copy());
        for (IObjCASTDesignator designator : getDesignators()) {
            copy.addDesignator(designator == null ? null : designator.copy());
        }
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IObjCASTDesignator[] getDesignators() {
        if (designators == null) {
            return IObjCASTDesignatedInitializer.EMPTY_DESIGNATOR_ARRAY;
        }
        designators = (IObjCASTDesignator[]) ArrayUtil.removeNullsAfter(IObjCASTDesignator.class,
                designators, designatorsPos);
        return designators;
    }

    public IASTInitializer getOperandInitializer() {
        return rhs;
    }

    public void setOperandInitializer(IASTInitializer rhs) {
        assertNotFrozen();
        this.rhs = rhs;
        if (rhs != null) {
            rhs.setParent(this);
            rhs.setPropertyInParent(OPERAND);
        }
    }

}
