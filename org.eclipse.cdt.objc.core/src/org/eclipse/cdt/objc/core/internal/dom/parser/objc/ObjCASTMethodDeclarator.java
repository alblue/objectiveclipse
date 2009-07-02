/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodDeclarator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodParameterDeclaration;

/**
 * Models function declarators for plain c.
 */
@SuppressWarnings("restriction")
public class ObjCASTMethodDeclarator extends ObjCASTDeclarator implements IObjCASTMethodDeclarator {

    private boolean isProto = false;
    private IASTParameterDeclaration[] parameters = null;
    private int parametersPos = -1;
    private IScope scope;
    private boolean varArgs;

    public ObjCASTMethodDeclarator() {
    }

    public ObjCASTMethodDeclarator(IASTName name) {
        super(name);
    }

    public void addParameterDeclaration(IASTParameterDeclaration parameter) {
        assertNotFrozen();
        if (parameter != null) {
            parameter.setParent(this);
            parameter.setPropertyInParent(FUNCTION_PARAMETER);
            parameters = (IASTParameterDeclaration[]) ArrayUtil.append(IASTParameterDeclaration.class,
                    parameters, ++parametersPos, parameter);
        }
    }

    @Override
    public ObjCASTMethodDeclarator copy() {
        ObjCASTMethodDeclarator copy = new ObjCASTMethodDeclarator();
        copyBaseDeclarator(copy);
        copy.varArgs = varArgs;

        for (IASTParameterDeclaration param : getParameters()) {
            copy.addParameterDeclaration(param == null ? null : param.copy());
        }

        return copy;
    }

    public IScope getFunctionScope() {
        if (scope != null) {
            return scope;
        }

        // introduce a scope for function declarations and definitions, only.
        IASTNode node = getParent();
        while (!(node instanceof IASTDeclaration)) {
            if (node == null) {
                return null;
            }
            node = node.getParent();
        }
        if (node instanceof IASTParameterDeclaration) {
            return null;
        }

        if (node instanceof IASTFunctionDefinition) {
            scope = ((IASTFunctionDefinition) node).getScope();
        } else if (ASTQueries.findTypeRelevantDeclarator(this) == this) {
            scope = new ObjCScope(this, EScopeKind.eLocal);
        }
        return scope;
    }

    @Override
    public IASTName getName() {
        int startOff = -1;
        int endOff = -1;
        StringBuilder builder = new StringBuilder();
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; ++i) {
                if (parameters[i] instanceof IObjCASTMethodParameterDeclaration) {
                    IASTName selector = ((IObjCASTMethodParameterDeclaration) parameters[i]).getSelector();
                    if (startOff < 0) {
                        startOff = ((ASTNode) selector).getOffset();
                    }
                    endOff = ((ASTNode) selector).getOffset() + ((ASTNode) selector).getLength();
                    builder.append(selector.getSimpleID());
                    builder.append(":"); //$NON-NLS-1$
                }
            }
        } else {
            builder.append(super.getName().toCharArray());
        }
        String s = builder.toString();
        IASTName name = new ObjCASTName(s.toCharArray());
        ((ASTNode) name).setOffsetAndLength(startOff, endOff - startOff);
        return name;
    }

    public IASTParameterDeclaration[] getParameters() {
        if (parameters == null) {
            return IASTParameterDeclaration.EMPTY_PARAMETERDECLARATION_ARRAY;
        }
        parameters = (IASTParameterDeclaration[]) ArrayUtil.removeNullsAfter(IASTParameterDeclaration.class,
                parameters, parametersPos);
        return parameters;
    }

    public boolean isProtocolMethod() {
        return isProto;
    }

    @Override
    protected boolean postAccept(ASTVisitor action) {
        IASTParameterDeclaration[] params = getParameters();
        for (int i = 0; i < params.length; i++) {
            if (!params[i].accept(action)) {
                return false;
            }
        }
        return super.postAccept(action);
    }

    @Override
    public void replace(IASTNode child, IASTNode other) {
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                if (child == parameters[i]) {
                    other.setPropertyInParent(child.getPropertyInParent());
                    other.setParent(child.getParent());
                    parameters[i] = (IASTParameterDeclaration) other;
                    return;
                }
            }
        }
        super.replace(child, other);
    }

    public void setProtocolMethod(boolean isProtocol) {
        isProto = isProtocol;
    }

    public void setVarArgs(boolean value) {
        assertNotFrozen();
        varArgs = value;
    }

    public boolean takesVarArgs() {
        return varArgs;
    }
}
