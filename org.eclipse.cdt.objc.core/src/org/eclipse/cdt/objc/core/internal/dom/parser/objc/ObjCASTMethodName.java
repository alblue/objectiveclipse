/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM) - Initial API and implementation Bryan
 * Wilkinson (QNX) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.IASTInternalNameOwner;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodName;

/**
 * Qualified name, which can contain any other name (unqualified, operator-name,
 * conversion name, template id).
 */

@SuppressWarnings("restriction")
public class ObjCASTMethodName extends ObjCASTName implements IObjCASTMethodName {

    private IASTName[] names = null;
    private int namesPos = -1;
    private char[] signature;

    public ObjCASTMethodName() {
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitNames) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        for (int i = 0; i <= namesPos; i++) {
            final IASTName name = names[i];
            if (i == namesPos) {
                if (name.getLookupKey().length > 0 && !name.accept(action)) {
                    return false;
                }
            } else if (!name.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitNames) {
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

    public void addSelector(IASTName name) {
        assertNotFrozen();
        assert !(name instanceof ICPPASTQualifiedName);
        if (name != null) {
            names = (IASTName[]) ArrayUtil.append(IASTName.class, names, ++namesPos, name);
            name.setParent(this);
            name.setPropertyInParent(SELECTOR_NAME);
        }
    }

    @Override
    public ObjCASTMethodName copy() {
        ObjCASTMethodName copy = new ObjCASTMethodName();
        for (IASTName name : getSelectors()) {
            copy.addSelector(name == null ? null : name.copy());
        }
        copy.setOffsetAndLength(this);
        return copy;
    }

    @Override
    public IBinding getBinding() {
        return getLastName().getBinding();
    }

    @Override
    public IASTName getLastName() {
        if (namesPos < 0) {
            return null;
        }

        return names[namesPos];
    }

    @Override
    public char[] getLookupKey() {
        return names[namesPos].getLookupKey();
    }

    @Override
    public final IBinding getPreBinding() {
        return getLastName().getPreBinding();
    }

    public int getRoleForName(IASTName n) {
        for (int i = 0; i < namesPos; ++i) {
            if (names[i] == n) {
                return r_reference;
            }
        }
        if (getLastName() == n) {
            IASTNode p = getParent();
            if (p instanceof IASTNameOwner) {
                return ((IASTNameOwner) p).getRoleForName(this);
            }
        }
        return r_unclear;
    }

    @Override
    public int getRoleOfName(boolean allowResolution) {
        IASTNode parent = getParent();
        if (parent instanceof IASTInternalNameOwner) {
            return ((IASTInternalNameOwner) parent).getRoleForName(this, allowResolution);
        }
        if (parent instanceof IASTNameOwner) {
            return ((IASTNameOwner) parent).getRoleForName(this);
        }
        return IASTNameOwner.r_unclear;
    }

    public IASTName[] getSelectors() {
        if (namesPos < 0) {
            return IASTName.EMPTY_NAME_ARRAY;
        }

        names = (IASTName[]) ArrayUtil.removeNullsAfter(IASTName.class, names, namesPos);
        return names;
    }

    @Override
    public char[] getSimpleID() {
        return toCharArray();
    }

    @Override
    public IBinding resolveBinding() {
        IASTName lastName = getLastName();
        return lastName == null ? null : lastName.resolveBinding();
    }

    @Override
    public final IBinding resolvePreBinding() {
        return getLastName().resolvePreBinding();
    }

    @Override
    public void setBinding(IBinding binding) {
        getLastName().setBinding(binding);
    }

    /**
     * @deprecated there is no need to set the signature, it will be computed
     *             lazily.
     */
    @Deprecated
    public void setSignature(String signature) {
    }

    @Override
    public char[] toCharArray() {
        if (signature == null) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i <= namesPos; i++) {
                buf.append(names[i].toCharArray());
                buf.append(Keywords.cpCOLON);
            }
            final int len = buf.length();
            signature = new char[len];
            buf.getChars(0, len, signature, 0);
        }
        return signature;
    }

    @Override
    public String toString() {
        return new String(toCharArray());
    }
}
