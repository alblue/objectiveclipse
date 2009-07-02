/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Anton Leherbauer (Wind
 * River Systems) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;

/**
 * A K&R C function declarator.
 * 
 * @author dsteffle
 */
@SuppressWarnings("restriction")
public class ObjCASTKnRFunctionDeclarator extends ObjCASTDeclarator implements IObjCASTKnRFunctionDeclarator {

    IASTDeclaration[] parameterDeclarations = IASTDeclaration.EMPTY_DECLARATION_ARRAY;
    IASTName[] parameterNames = IASTName.EMPTY_NAME_ARRAY;

    public ObjCASTKnRFunctionDeclarator() {
    }

    public ObjCASTKnRFunctionDeclarator(IASTName[] parameterNames, IASTDeclaration[] parameterDeclarations) {
        setParameterNames(parameterNames);
        setParameterDeclarations(parameterDeclarations);
    }

    @Override
    public ObjCASTKnRFunctionDeclarator copy() {
        ObjCASTKnRFunctionDeclarator copy = new ObjCASTKnRFunctionDeclarator();
        copyBaseDeclarator(copy);

        copy.parameterNames = new IASTName[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i] != null) {
                copy.parameterNames[i] = parameterNames[i].copy();
                copy.parameterNames[i].setParent(copy);
                copy.parameterNames[i].setPropertyInParent(PARAMETER_NAME);
            }
        }

        copy.parameterDeclarations = new IASTDeclaration[parameterDeclarations.length];
        for (int i = 0; i < parameterDeclarations.length; i++) {
            if (parameterDeclarations[i] != null) {
                copy.parameterDeclarations[i] = parameterDeclarations[i].copy();
                copy.parameterDeclarations[i].setParent(copy);
                copy.parameterDeclarations[i].setPropertyInParent(FUNCTION_PARAMETER);
            }
        }

        return copy;
    }

    public IASTDeclarator getDeclaratorForParameterName(IASTName name) {
        boolean found = false;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i] == name) {
                found = true;
            }
        }
        if (!found) {
            return null;
        }

        for (int i = 0; i < parameterDeclarations.length; i++) {
            if (parameterDeclarations[i] instanceof IASTSimpleDeclaration) {
                IASTDeclarator[] decltors = ((IASTSimpleDeclaration) parameterDeclarations[i])
                        .getDeclarators();
                for (int j = 0; j < decltors.length; j++) {
                    if (decltors[j].getName().toString().equals(name.toString())) {
                        return decltors[j];
                    }
                }
            }
        }

        return null;
    }

    public IASTDeclaration[] getParameterDeclarations() {
        return parameterDeclarations;
    }

    public IASTName[] getParameterNames() {
        return parameterNames;
    }

    @Override
    public int getRoleForName(IASTName name) {
        IASTName[] n = getParameterNames();
        for (int i = 0; i < n.length; ++i) {
            if (n[i] == name) {
                return r_unclear;
            }
        }
        return super.getRoleForName(name);
    }

    @Override
    protected boolean postAccept(ASTVisitor action) {
        IASTName[] ns = getParameterNames();
        for (int i = 0; i < ns.length; i++) {
            if (!ns[i].accept(action)) {
                return false;
            }
        }

        IASTDeclaration[] params = getParameterDeclarations();
        for (int i = 0; i < params.length; i++) {
            if (!params[i].accept(action)) {
                return false;
            }
        }

        return super.postAccept(action);
    }

    public void setParameterDeclarations(IASTDeclaration[] decls) {
        assertNotFrozen();
        parameterDeclarations = decls;
        if (decls != null) {
            for (IASTDeclaration decl : decls) {
                if (decl != null) {
                    decl.setParent(this);
                    decl.setPropertyInParent(FUNCTION_PARAMETER);
                }
            }
        }
    }

    public void setParameterNames(IASTName[] names) {
        assertNotFrozen();
        parameterNames = names;
        if (names != null) {
            for (IASTName name : names) {
                if (name != null) {
                    name.setParent(this);
                    name.setPropertyInParent(PARAMETER_NAME);
                }
            }
        }
    }
}
