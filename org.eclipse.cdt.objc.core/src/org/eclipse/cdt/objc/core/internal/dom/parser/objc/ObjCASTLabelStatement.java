/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTLabelStatement extends ASTNode implements IASTLabelStatement, IASTAmbiguityParent {

    private IASTName name;
    private IASTStatement nestedStatement;

    public ObjCASTLabelStatement() {
    }

    public ObjCASTLabelStatement(IASTName name, IASTStatement nestedStatement) {
        setName(name);
        setNestedStatement(nestedStatement);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitStatements) {
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
        if (nestedStatement != null) {
            if (!nestedStatement.accept(action)) {
                return false;
            }
        }
        if (action.shouldVisitStatements) {
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

    public ObjCASTLabelStatement copy() {
        ObjCASTLabelStatement copy = new ObjCASTLabelStatement();
        copy.setName(name == null ? null : name.copy());
        copy.setNestedStatement(nestedStatement == null ? null : nestedStatement.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTName getName() {
        return name;
    }

    public IASTStatement getNestedStatement() {
        return nestedStatement;
    }

    public int getRoleForName(IASTName n) {
        if (n == name) {
            return r_declaration;
        }
        return r_unclear;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == nestedStatement) {
            other.setParent(this);
            other.setPropertyInParent(child.getPropertyInParent());
            setNestedStatement((IASTStatement) other);
        }
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(NAME);
        }
    }

    public void setNestedStatement(IASTStatement s) {
        assertNotFrozen();
        nestedStatement = s;
        if (s != null) {
            s.setParent(this);
            s.setPropertyInParent(NESTED_STATEMENT);
        }
    }

}
