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
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisitor;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTCategorySpecifier;

@SuppressWarnings("restriction")
public class ObjCASTCategorySpecifier extends ASTNode implements IASTCompletionContext,
        IObjCASTCategorySpecifier {

    private IASTName fName;

    public ObjCASTCategorySpecifier() {
    }

    public ObjCASTCategorySpecifier(IASTName name) {
        setName(name);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitBaseSpecifiers && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (!fName.accept(action)) {
            return false;
        }

        if (action.shouldVisitBaseSpecifiers && action instanceof IObjCASTVisitor) {
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

    public IObjCASTCategorySpecifier copy() {
        ObjCASTCategorySpecifier copy = new ObjCASTCategorySpecifier();
        copy.setName(fName == null ? null : fName.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IBinding[] findBindings(IASTName n, boolean isPrefix) {
        return null;
    }

    public IASTName getName() {
        return fName;
    }

    public int getRoleForName(IASTName name) {
        if (name == fName) {
            return r_reference;
        }
        return r_unclear;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        fName = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(NAME);
        }
    }

}
