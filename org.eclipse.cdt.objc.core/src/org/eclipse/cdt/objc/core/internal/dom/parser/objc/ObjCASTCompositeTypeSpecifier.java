/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
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
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;

/**
 * Implementation for C composite specifiers.
 */
@SuppressWarnings("restriction")
public class ObjCASTCompositeTypeSpecifier extends ObjCASTBaseDeclSpecifier implements
        IObjCASTCompositeTypeSpecifier, IASTAmbiguityParent {

    private IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier[] baseSpecs = null;
    private int baseSpecsPos = -1;
    private IASTDeclaration[] fActiveDeclarations = null;
    private IASTDeclaration[] fAllDeclarations = null;
    private int fDeclarationsPos = -1;
    private int fKey;
    private IASTName fName;
    private IScope fScope = null;

    public ObjCASTCompositeTypeSpecifier() {
    }

    public ObjCASTCompositeTypeSpecifier(int key, IASTName name) {
        fKey = key;
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
        if (fName != null && !fName.accept(action)) {
            return false;
        }

        IObjCASTBaseSpecifier[] bases = getBaseSpecifiers();
        for (int i = 0; i < bases.length; i++) {
            if (!bases[i].accept(action)) {
                return false;
            }
        }

        IASTDeclaration[] decls = getDeclarations(action.includeInactiveNodes);
        for (int i = 0; i < decls.length; i++) {
            if (!decls[i].accept(action)) {
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

    public void addBaseSpecifier(IObjCASTBaseSpecifier baseSpec) {
        assertNotFrozen();
        if (baseSpec != null) {
            baseSpec.setParent(this);
            baseSpec.setPropertyInParent(BASE_SPECIFIER);
            baseSpecs = (IObjCASTBaseSpecifier[]) ArrayUtil.append(IObjCASTBaseSpecifier.class, baseSpecs,
                    ++baseSpecsPos, baseSpec);
        }
    }

    public void addDeclaration(IASTDeclaration declaration) {
        addMemberDeclaration(declaration);
    }

    public void addMemberDeclaration(IASTDeclaration declaration) {
        assertNotFrozen();
        if (declaration != null) {
            declaration.setParent(this);
            declaration.setPropertyInParent(MEMBER_DECLARATION);
            fAllDeclarations = (IASTDeclaration[]) ArrayUtil.append(IASTDeclaration.class, fAllDeclarations,
                    ++fDeclarationsPos, declaration);
            fActiveDeclarations = null;
        }
    }

    public ObjCASTCompositeTypeSpecifier copy() {
        ObjCASTCompositeTypeSpecifier copy = new ObjCASTCompositeTypeSpecifier();
        copyCompositeTypeSpecifier(copy);
        return copy;
    }

    protected void copyCompositeTypeSpecifier(ObjCASTCompositeTypeSpecifier copy) {
        copyBaseDeclSpec(copy);
        copy.setKey(fKey);
        copy.setName(fName == null ? null : fName.copy());
        for (IASTDeclaration member : getMembers()) {
            copy.addMemberDeclaration(member == null ? null : member.copy());
        }
        for (IObjCASTBaseSpecifier baseSpecifier : getBaseSpecifiers()) {
            copy.addBaseSpecifier(baseSpecifier == null ? null : baseSpecifier.copy());
        }
    }

    public IObjCASTBaseSpecifier[] getBaseSpecifiers() {
        if (baseSpecs == null) {
            return IObjCASTBaseSpecifier.EMPTY_BASESPECIFIER_ARRAY;
        }
        baseSpecs = (IObjCASTBaseSpecifier[]) ArrayUtil.removeNullsAfter(IObjCASTBaseSpecifier.class,
                baseSpecs, baseSpecsPos);
        return baseSpecs;
    }

    public final IASTDeclaration[] getDeclarations(boolean includeInactive) {
        if (includeInactive) {
            fAllDeclarations = (IASTDeclaration[]) ArrayUtil.removeNullsAfter(IASTDeclaration.class,
                    fAllDeclarations, fDeclarationsPos);
            return fAllDeclarations;
        }
        return getMembers();
    }

    public int getKey() {
        return fKey;
    }

    public IASTDeclaration[] getMembers() {
        IASTDeclaration[] active = fActiveDeclarations;
        if (active == null) {
            active = ASTQueries.extractActiveDeclarations(fAllDeclarations, fDeclarationsPos + 1);
            fActiveDeclarations = active;
        }
        return active;
    }

    public IASTName getName() {
        return fName;
    }

    public int getRoleForName(IASTName n) {
        if (n == fName) {
            return r_definition;
        }
        return r_unclear;
    }

    public IScope getScope() {
        if (fScope == null) {
            fScope = new ObjCCompositeTypeScope(this);
        }
        return fScope;
    }

    public void replace(IASTNode child, IASTNode other) {
        assert child.isActive() == other.isActive();
        for (int i = 0; i <= fDeclarationsPos; ++i) {
            if (fAllDeclarations[i] == child) {
                other.setParent(child.getParent());
                other.setPropertyInParent(child.getPropertyInParent());
                fAllDeclarations[i] = (IASTDeclaration) other;
                fActiveDeclarations = null;
                return;
            }
        }
    }

    public void setKey(int key) {
        assertNotFrozen();
        fKey = key;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        fName = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(TYPE_NAME);
        }
    }

}
