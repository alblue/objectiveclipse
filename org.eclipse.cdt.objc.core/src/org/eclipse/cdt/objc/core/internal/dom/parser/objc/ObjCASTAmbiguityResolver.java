/*******************************************************************************
 * Copyright (c) 2009 Wind River Systems, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.c.ICCompositeTypeScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;

/**
 * Visitor to resolve ast ambiguities in the right order, which is simply a
 * depth first traversal.
 */
@SuppressWarnings("restriction")
public final class ObjCASTAmbiguityResolver extends ASTVisitor {
    public ObjCASTAmbiguityResolver() {
        super(false);
        includeInactiveNodes = true;
        shouldVisitAmbiguousNodes = true;
    }

    private void repopulateScope(IASTNode node) {
        IScope scope = ObjCVisitor.getContainingScope(node);
        if (scope instanceof ObjCScope) {
            ObjCScope cscope = (ObjCScope) scope;
            if (scope instanceof ICCompositeTypeScope) {
                cscope.markAsUncached();
            } else {
                cscope.collectNames(node);
            }
        }
    }

    @Override
    public int visit(ASTAmbiguousNode astAmbiguousNode) {
        IASTNode node = astAmbiguousNode.resolveAmbiguity(this);
        if (node instanceof IASTDeclarator || node instanceof IASTParameterDeclaration
                || node instanceof IASTDeclaration) {
            while (node != null) {
                if (node instanceof IASTDeclaration || node instanceof IASTParameterDeclaration) {
                    repopulateScope(node);
                    break;
                }
                if (node instanceof IASTExpression) {
                    break;
                }
                node = node.getParent();
            }
        } else if (node instanceof IASTDeclarationStatement) {
            repopulateScope(((IASTDeclarationStatement) node).getDeclaration());
        }
        return PROCESS_SKIP;
    }
}
