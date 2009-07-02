/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Niefer (IBM Corporation) - initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTInternal;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodDeclarator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisibilityLabel;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCCompositeType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCCompositeTypeScope;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCMethod;

/**
 * The binding for a method.
 */
@SuppressWarnings("restriction")
public class ObjCMethod extends ObjCFunction implements IObjCMethod {
    public static class ObjCMethodProblem extends ObjCFunctionProblem implements IObjCMethod {
        /**
         * @param id
         * @param arg
         */
        public ObjCMethodProblem(IASTNode node, int id, char[] arg) {
            super(node, id, arg);
        }

        public IObjCCompositeType getClassOwner() throws DOMException {
            throw new DOMException(this);
        }

        public int getVisibility() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isClassMethod() throws DOMException {
            throw new DOMException(this);
        }

        public boolean isProtocolMethod() throws DOMException {
            throw new DOMException(this);
        }

    }

    public ObjCMethod(IObjCASTMethodDeclarator declarator) {
        super(declarator);
    }

    public IObjCCompositeType getClassOwner() throws DOMException {
        IObjCCompositeTypeScope scope = (IObjCCompositeTypeScope) getScope();
        return (IObjCCompositeType) scope.getCompositeType();
    }

    protected IObjCASTDeclSpecifier getDeclSpec(IASTDeclaration decl) {
        IObjCASTDeclSpecifier declSpec = null;
        if (decl instanceof IASTSimpleDeclaration) {
            declSpec = (IObjCASTDeclSpecifier) ((IASTSimpleDeclaration) decl).getDeclSpecifier();
        } else if (decl instanceof IASTFunctionDefinition) {
            declSpec = (IObjCASTDeclSpecifier) ((IASTFunctionDefinition) decl).getDeclSpecifier();
        }
        return declSpec;
    }

    public IASTDeclaration getPrimaryDeclaration() throws DOMException {
        // first check if we already know it
        if (declarators != null) {
            for (IASTDeclarator dtor : declarators) {
                if (dtor == null) {
                    break;
                }
                dtor = ObjCVisitor.findOutermostDeclarator(dtor);
                IASTDeclaration decl = (IASTDeclaration) dtor.getParent();
                if (decl.getParent() instanceof IObjCASTCompositeTypeSpecifier) {
                    return decl;
                }
            }
        }

        final char[] myName = getASTName().getLookupKey();
        IObjCCompositeTypeScope scope = (IObjCCompositeTypeScope) getScope();
        IObjCASTCompositeTypeSpecifier compSpec = (IObjCASTCompositeTypeSpecifier) ASTInternal
                .getPhysicalNodeOfScope(scope);
        if (compSpec != null) {
            IASTDeclaration[] members = compSpec.getMembers();
            for (IASTDeclaration member : members) {
                if (member instanceof IASTSimpleDeclaration) {
                    IASTDeclarator[] dtors = ((IASTSimpleDeclaration) member).getDeclarators();
                    for (IASTDeclarator dtor : dtors) {
                        IASTName name = ObjCVisitor.findInnermostDeclarator(dtor).getName();
                        if (CharArrayUtils.equals(name.getLookupKey(), myName)
                                && name.resolveBinding() == this) {
                            return member;
                        }
                    }
                } else if (member instanceof IASTFunctionDefinition) {
                    final IASTFunctionDeclarator declarator = ((IASTFunctionDefinition) member)
                            .getDeclarator();
                    IASTName name = ObjCVisitor.findInnermostDeclarator(declarator).getName();
                    if (CharArrayUtils.equals(name.getLookupKey(), myName) && name.resolveBinding() == this) {
                        return member;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IScope getScope() {
        return ObjCVisitor.getContainingScope(getASTName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.ObjC.IObjCMember#getVisibility()
     */
    public int getVisibility() throws DOMException {
        IASTDeclaration decl = getPrimaryDeclaration();
        if (decl == null) {
            IScope scope = getScope();
            if (scope instanceof IObjCCompositeTypeScope) {
                IObjCCompositeType cls = (IObjCCompositeType) ((IObjCCompositeTypeScope) scope)
                        .getCompositeType();
                if (cls != null) {
                    return (cls.getKey() == IObjCCompositeType.k_class) ? IObjCASTVisibilityLabel.v_private
                            : IObjCASTVisibilityLabel.v_public;
                }
            }
            return IObjCASTVisibilityLabel.v_private;
        }

        IASTCompositeTypeSpecifier cls = (IASTCompositeTypeSpecifier) decl.getParent();
        IASTDeclaration[] members = cls.getMembers();
        IObjCASTVisibilityLabel vis = null;
        for (IASTDeclaration member : members) {
            if (member instanceof IObjCASTVisibilityLabel) {
                vis = (IObjCASTVisibilityLabel) member;
            } else if (member == decl) {
                break;
            }
        }
        if (vis != null) {
            return vis.getVisibility();
        } else if (cls.getKey() == IObjCASTCompositeTypeSpecifier.k_class) {
            return IObjCASTVisibilityLabel.v_private;
        }
        return IObjCASTVisibilityLabel.v_public;
    }

    public boolean isClassMethod() throws DOMException {
        return isStatic();
    }

    public boolean isProtocolMethod() throws DOMException {
        IASTDeclaration decl = getPrimaryDeclaration();
        if (decl != null) {
            IObjCASTDeclSpecifier declSpec = getDeclSpec(decl);
            if (declSpec != null) {
                return declSpec.isProtocol();
            }
        }
        return false;
    }

}
