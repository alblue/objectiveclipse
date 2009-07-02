/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems) Yuan Zhang / Beth Tibbitts
 * (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.IASTInternalEnumerationSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTEnumerationSpecifier;

/**
 * AST node for enumeration specifiers.
 */
@SuppressWarnings("restriction")
public class ObjCASTEnumerationSpecifier extends ObjCASTBaseDeclSpecifier implements
        IASTInternalEnumerationSpecifier, IObjCASTEnumerationSpecifier {

    private IASTEnumerator[] enumerators = null;
    private int enumeratorsPos = -1;

    private IASTName name;

    private boolean valuesComputed = false;

    public ObjCASTEnumerationSpecifier() {
    }

    public ObjCASTEnumerationSpecifier(IASTName name) {
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
        IASTEnumerator[] etors = getEnumerators();
        for (int i = 0; i < etors.length; i++) {
            if (!etors[i].accept(action)) {
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

    public void addEnumerator(IASTEnumerator enumerator) {
        assertNotFrozen();
        if (enumerator != null) {
            enumerator.setParent(this);
            enumerator.setPropertyInParent(ENUMERATOR);
            enumerators = (IASTEnumerator[]) ArrayUtil.append(IASTEnumerator.class, enumerators,
                    ++enumeratorsPos, enumerator);
        }
    }

    public ObjCASTEnumerationSpecifier copy() {
        ObjCASTEnumerationSpecifier copy = new ObjCASTEnumerationSpecifier();
        copyEnumerationSpecifier(copy);
        return copy;
    }

    protected void copyEnumerationSpecifier(ObjCASTEnumerationSpecifier copy) {
        copyBaseDeclSpec(copy);
        copy.setName(name == null ? null : name.copy());
        for (IASTEnumerator enumerator : getEnumerators()) {
            copy.addEnumerator(enumerator == null ? null : enumerator.copy());
        }
    }

    public IASTEnumerator[] getEnumerators() {
        if (enumerators == null) {
            return IASTEnumerator.EMPTY_ENUMERATOR_ARRAY;
        }
        enumerators = (IASTEnumerator[]) ArrayUtil.removeNullsAfter(IASTEnumerator.class, enumerators,
                enumeratorsPos);
        return enumerators;
    }

    public IASTName getName() {
        return name;
    }

    public int getRoleForName(IASTName n) {
        if (name == n) {
            return r_definition;
        }
        return r_unclear;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(ENUMERATION_NAME);
        }
    }

    public boolean startValueComputation() {
        if (valuesComputed) {
            return false;
        }

        valuesComputed = true;
        return true;
    }

}
