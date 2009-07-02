/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 * Created on Jan 24, 2005
 */
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * @author aniefer
 */
public interface IObjCInternalBinding {
    // methods needed by CVisitor but not meant for public interface
    public IASTNode getPhysicalNode();

    /**
     * Returns the declarations for this binding.
     * 
     * @since 5.0
     */
    public IASTNode[] getDeclarations();

    /**
     * Returns the definitions for this binding.
     * 
     * @since 5.0
     */
    public IASTNode getDefinition();
}
