/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Markus Schorn
 * (Wind River Systems)
 *******************************************************************************/

package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;

/**
 * Interface for ast-internal implementations of function bindings.
 */
public interface IObjCInternalFunction extends IObjCInternalBinding {
    public void setFullyResolved(boolean resolved);

    public void addDeclarator(IASTFunctionDeclarator fnDeclarator);

    /**
     * Returns whether there is a static declaration for this function.
     * 
     * @param resolveAll
     *            checks for names that are not yet resolved to this binding.
     */
    public boolean isStatic(boolean resolveAll);
}
