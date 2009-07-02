/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Markus
 * Schorn (Wind River Systems) Yuan Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTInitializerList extends ASTNode implements IASTInitializerList {

    private IASTInitializer[] initializers = null;

    private int initializersPos = -1;

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
        IASTInitializer[] list = getInitializers();
        for (int i = 0; i < list.length; i++) {
            if (!list[i].accept(action)) {
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

    public void addInitializer(IASTInitializer d) {
        assertNotFrozen();
        if (d != null) {
            initializers = (IASTInitializer[]) ArrayUtil.append(IASTInitializer.class, initializers,
                    ++initializersPos, d);
            d.setParent(this);
            d.setPropertyInParent(NESTED_INITIALIZER);
        }
    }

    public ObjCASTInitializerList copy() {
        ObjCASTInitializerList copy = new ObjCASTInitializerList();
        for (IASTInitializer initializer : getInitializers()) {
            copy.addInitializer(initializer == null ? null : initializer.copy());
        }
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTInitializer[] getInitializers() {
        if (initializers == null) {
            return IASTInitializer.EMPTY_INITIALIZER_ARRAY;
        }
        initializers = (IASTInitializer[]) ArrayUtil.removeNullsAfter(IASTInitializer.class, initializers,
                initializersPos);
        return initializers;
    }

}
