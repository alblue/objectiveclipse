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
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPropertyAttribute;
import org.eclipse.cdt.objc.core.dom.ast.objc.ObjCASTVisitor;

@SuppressWarnings("restriction")
public class ObjCASTPropertyAttribute extends ASTNode implements IObjCASTPropertyAttribute,
        IASTAmbiguityParent {

    IASTName attributeMethodName;
    int attributeType;

    public ObjCASTPropertyAttribute() {
    }

    public ObjCASTPropertyAttribute(int attr) {
        attributeType = attr;
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action instanceof ObjCASTVisitor && ((ObjCASTVisitor) action).shouldVisitAttributes) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (attributeMethodName != null) {
            if (!attributeMethodName.accept(action)) {
                return false;
            }
        }

        if (action instanceof ObjCASTVisitor && ((ObjCASTVisitor) action).shouldVisitAttributes) {
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

    public IObjCASTPropertyAttribute copy() {
        ObjCASTPropertyAttribute copy = new ObjCASTPropertyAttribute();
        copy.setType(attributeType);
        copy.setMethodName(attributeMethodName == null ? null : attributeMethodName.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTName getMethodName() {
        return attributeMethodName;
    }

    public int getType() {
        return attributeType;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (attributeMethodName == child) {
            other.setPropertyInParent(attributeMethodName.getPropertyInParent());
            other.setParent(attributeMethodName.getParent());
            attributeMethodName = (IASTName) other;
        }
    }

    public void setMethodName(IASTName value) {
        assertNotFrozen();
        if (value != null) {
            value.setParent(this);
            value.setPropertyInParent(ATTRIBUTE_METHOD_NAME);
            attributeMethodName = value;
        }
    }

    public void setType(int type) {
        assertNotFrozen();
        attributeType = type;
    }

}
