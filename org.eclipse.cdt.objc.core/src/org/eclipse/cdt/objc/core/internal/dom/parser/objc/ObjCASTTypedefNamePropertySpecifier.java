/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research) Bryan Wilkinson (QNX) Anton Leherbauer
 * (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPropertyAttribute;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPropertyDeclSpecifier;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTTypedefNamePropertySpecifier extends ObjCASTTypedefNameSpecifier implements
        IObjCASTPropertyDeclSpecifier {

    private IObjCASTPropertyAttribute[] attributes;
    private int attributesPos = -1;
    private IASTName name;

    public ObjCASTTypedefNamePropertySpecifier() {
    }

    public ObjCASTTypedefNamePropertySpecifier(IASTName name) {
        setName(name);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDeclSpecifiers) {
            switch (action.visit(this)) {
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

        IObjCASTPropertyAttribute[] attrs = getAttributes();
        for (int i = 0; i < attrs.length; i++) {
            if (!attrs[i].accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitDeclSpecifiers) {
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

    public void addAttribute(IObjCASTPropertyAttribute attribute) {
        assertNotFrozen();
        if (attribute != null) {
            attribute.setParent(this);
            attribute.setPropertyInParent(ATTRIBUTE);
            attributes = (IObjCASTPropertyAttribute[]) ArrayUtil.append(IObjCASTPropertyAttribute.class,
                    attributes, ++attributesPos, attribute);
        }
    }

    @Override
    public ObjCASTTypedefNamePropertySpecifier copy() {
        ObjCASTTypedefNamePropertySpecifier copy = new ObjCASTTypedefNamePropertySpecifier(
                name == null ? null : name.copy());
        copyBaseDeclSpec(copy);
        return copy;
    }

    public IObjCASTPropertyAttribute[] getAttributes() {
        if (attributes != null) {
            return IObjCASTPropertyAttribute.EMPTY_ATTRIBUTE_ARRAY;
        }
        attributes = (IObjCASTPropertyAttribute[]) ArrayUtil.removeNullsAfter(
                IObjCASTPropertyAttribute.class, attributes, attributesPos);
        return attributes;
    }

}
