/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind
 * River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.parser.scanner.ILocationResolver;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCCompositeType;
import org.eclipse.cdt.objc.core.dom.parser.ObjCKeywords;

/**
 * Node for elaborated type specifiers (examples: struct S; union U; enum E;)
 */
@SuppressWarnings("restriction")
public class ObjCASTElaboratedTypeSpecifier extends ObjCASTBaseDeclSpecifier implements
        IObjCASTElaboratedTypeSpecifier, IASTCompletionContext {

    private int kind;
    private IASTName name;

    public ObjCASTElaboratedTypeSpecifier() {
    }

    public ObjCASTElaboratedTypeSpecifier(int kind, IASTName name) {
        this.kind = kind;
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

    public ObjCASTElaboratedTypeSpecifier copy() {
        ObjCASTElaboratedTypeSpecifier copy = new ObjCASTElaboratedTypeSpecifier(kind, name == null ? null
                : name.copy());
        copyBaseDeclSpec(copy);
        return copy;
    }

    public IBinding[] findBindings(IASTName n, boolean isPrefix) {
        IBinding[] result = ObjCVisitor.findBindingsForContentAssist(n, isPrefix);
        int nextPos = 0;
        for (int i = 0; i < result.length; i++) {
            IBinding b = result[i];
            if (b instanceof ICompositeType) {
                ICompositeType ct = (ICompositeType) b;
                try {
                    switch (ct.getKey()) {
                        case ICompositeType.k_struct:
                            if (getKind() != k_struct) {
                                b = null;
                            }
                            break;
                        case ICompositeType.k_union:
                            if (getKind() != k_union) {
                                b = null;
                            }
                            break;
                        case IObjCCompositeType.k_class:
                            if (getKind() != k_union) {
                                b = null;
                            }
                            break;
                    }
                } catch (DOMException e) {
                    // ignore and propose binding
                }
            } else if (b instanceof IEnumeration) {
                if (getKind() != k_enum) {
                    b = null;
                }
            }
            if (b != null) {
                result[nextPos++] = b;
            }
        }
        if (nextPos != result.length) {
            IBinding[] copy = new IBinding[nextPos];
            System.arraycopy(result, 0, copy, 0, nextPos);
            return copy;
        }
        return result;
    }

    public int getKind() {
        return kind;
    }

    public IASTName getName() {
        return name;
    }

    @Override
    @SuppressWarnings( { "nls" })
    protected char[] getRawSignatureChars() {
        final IASTFileLocation floc = getFileLocation();
        final IASTTranslationUnit ast = getTranslationUnit();
        if (floc != null && ast != null) {
            ILocationResolver lr = (ILocationResolver) ast.getAdapter(ILocationResolver.class);
            if (lr != null) {
                /*
                 * Prepend '@class' to all signatures if they do not already
                 * have it, as '@class x,y,z;' creates 3
                 * ObjCASTElaboratedTypeSpecifier but only the first also has
                 * 
                 * @class in the fileLocation
                 */
                char[] signature = lr.getUnpreprocessedSignature(getFileLocation());
                String sigString = new String(signature);
                if (!sigString.startsWith(new String(ObjCKeywords.cp_AtClass))) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(ObjCKeywords.cp_AtClass);
                    builder.append(" ");
                    builder.append(sigString);
                    signature = builder.toString().toCharArray();
                }
                return signature;
            }
        }
        return CharArrayUtils.EMPTY;
    }

    public int getRoleForName(IASTName n) {
        if (n != name) {
            return r_unclear;
        }

        IASTNode parent = getParent();
        if (!(parent instanceof IASTDeclaration)) {
            return r_reference;
        }

        if (parent instanceof IASTSimpleDeclaration) {
            IASTDeclarator[] dtors = ((IASTSimpleDeclaration) parent).getDeclarators();
            if (dtors.length == 0) {
                return r_declaration;
            }
        }

        // can't tell, resolve the binding
        IBinding binding = name.resolveBinding();
        if (binding instanceof IObjCInternalBinding) {
            IASTNode node = ((IObjCInternalBinding) binding).getPhysicalNode();
            if (node == name) {
                return r_declaration;
            }
        }
        return r_reference;
    }

    public void setKind(int value) {
        assertNotFrozen();
        kind = value;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(TYPE_NAME);
        }
    }
}
