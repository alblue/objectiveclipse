/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Wind River Systems, Inc. and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguousSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.IASTInternalScope;

/**
 * Handles ambiguities for parameter declarations. <br>
 * void function(const D*); // is D a type?
 * 
 * @since 5.0.1
 */
@SuppressWarnings("restriction")
public class ObjCASTAmbiguousSimpleDeclaration extends ASTAmbiguousNode implements
        IASTAmbiguousSimpleDeclaration {

    private final IASTDeclSpecifier fAltDeclSpec;
    private final IASTDeclarator fAltDtor;
    private final IASTSimpleDeclaration fSimpleDecl;

    public ObjCASTAmbiguousSimpleDeclaration(IASTSimpleDeclaration decl, IASTDeclSpecifier declSpec,
            IASTDeclarator dtor) {
        fSimpleDecl = decl;
        fAltDeclSpec = declSpec;
        fAltDtor = dtor;
    }

    public void addDeclarator(IASTDeclarator declarator) {
        fSimpleDecl.addDeclarator(declarator);
    }

    @Override
    protected void beforeResolution() {
        // populate containing scope, so that it will not be affected by the
        // alternative branches.
        IScope scope = ObjCVisitor.getContainingScope(this);
        if (scope instanceof IASTInternalScope) {
            ((IASTInternalScope) scope).populateCache();
        }
    }

    public IASTSimpleDeclaration copy() {
        throw new UnsupportedOperationException();
    }

    public IASTDeclarator[] getDeclarators() {
        return fSimpleDecl.getDeclarators();
    }

    public IASTDeclSpecifier getDeclSpecifier() {
        return fSimpleDecl.getDeclSpecifier();
    }

    @Override
    public IASTNode[] getNodes() {
        return new IASTNode[] { fSimpleDecl, fAltDeclSpec, fAltDtor };
    }

    @Override
    public final IASTNode resolveAmbiguity(ASTVisitor visitor) {
        final IASTAmbiguityParent owner = (IASTAmbiguityParent) getParent();
        IASTNode nodeToReplace = this;

        // handle nested ambiguities first
        owner.replace(nodeToReplace, fSimpleDecl);
        IASTDeclSpecifier declSpec = fSimpleDecl.getDeclSpecifier();
        declSpec.accept(visitor);

        // find nested names
        final NameCollector nameCollector = new NameCollector();
        declSpec.accept(nameCollector);
        final IASTName[] names = nameCollector.getNames();

        // resolve names
        boolean hasIssue = false;
        for (IASTName name : names) {
            try {
                IBinding b = name.resolveBinding();
                if (b instanceof IProblemBinding) {
                    hasIssue = true;
                    break;
                }
            } catch (Exception t) {
                hasIssue = true;
                break;
            }
        }
        if (hasIssue) {
            // use the alternate version
            final IASTAmbiguityParent parent = (IASTAmbiguityParent) fSimpleDecl;
            parent.replace(declSpec, fAltDeclSpec);
            parent.replace(fSimpleDecl.getDeclarators()[0], fAltDtor);
        }

        // resolve further nested ambiguities
        fSimpleDecl.accept(visitor);
        return fSimpleDecl;
    }

    public void setDeclSpecifier(IASTDeclSpecifier declSpec) {
        fSimpleDecl.setDeclSpecifier(declSpec);
    }
}
