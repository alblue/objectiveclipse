/*******************************************************************************
 * Copyright (c) 2009, Ryan Rusaw and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Ryan Rusaw  - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;

public interface IObjCASTPropertyAttribute extends IASTDeclaration {

    public static final int attr_getter = 0;

    public static final int attr_setter = 1;
    
    public static final int attr_readwrite = 2;
    
    public static final int attr_readonly = 3;
    
    public static final int attr_assign = 4;
    
    public static final int attr_retain = 5;
    
    public static final int attr_copy = 6;
    
    public static final int attr_nonatomic = 7;
    
    public static final int attr_last = attr_nonatomic;
    
    /**
     * @since 5.1
     */
    public IObjCASTPropertyAttribute copy();
    
    public static final ASTNodeProperty ATTRIBUTE_METHOD_NAME = new ASTNodeProperty(
    "IObjCASTPropertyAttribute.ATTRIBUTE_METHOD_NAME - IObjCASTPropertyAttribute Method Name"); //$NON-NLS-1$
    
    public IASTName getMethodName();
    
    public void setMethodName(IASTName name);
    
    public int getType();
    
    public static final IObjCASTPropertyAttribute [] EMPTY_ATTRIBUTE_ARRAY = new IObjCASTPropertyAttribute[0];

}
