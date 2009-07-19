/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Yuan Zhang /
 * Beth Tibbitts (IBM Research) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTCategorySpecifier;

/**
 * This subclass of ASTVisitor that allows for better control in traversing C.
 */
public abstract class ObjCASTVisitor extends ASTVisitor implements IObjCASTVisitor {

    public boolean shouldVisitAttributes = false;

    public int leave(IObjCASTBaseSpecifier baseSpecifier) {
        return PROCESS_CONTINUE;
    }

    public int leave(IObjCASTCategorySpecifier catSpecifier) {
        return PROCESS_CONTINUE;
    }

    public int leave(IObjCASTDesignator designator) {
        return PROCESS_CONTINUE;
    }

    public int leave(IObjCASTPropertyAttribute attr) {
        return PROCESS_CONTINUE;
    }

    public int visit(IObjCASTBaseSpecifier baseSpecifier) {
        return PROCESS_CONTINUE;
    }

    public int visit(IObjCASTCategorySpecifier categorySpecifier) {
        return PROCESS_CONTINUE;
    }

    public int visit(IObjCASTDesignator designator) {
        return PROCESS_CONTINUE;
    }

    public int visit(IObjCASTPropertyAttribute attr) {
        return PROCESS_CONTINUE;
    }

}
