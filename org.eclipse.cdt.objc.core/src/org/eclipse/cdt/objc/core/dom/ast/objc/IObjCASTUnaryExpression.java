/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM) - Initial API and implementation Mike Kucera
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTUnaryExpression;

/**
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCASTUnaryExpression extends IGNUASTUnaryExpression, IASTImplicitNameOwner {

    /**
     * <code>op_AtThrow</code> \@throw exception
     */
    public static final int op_AtThrow = IASTUnaryExpression.op_last + 1;

    /**
     * <code>op_AtProtocol</code> = \@protocol( protocol_name )
     */
    public static final int op_AtProtocol = IASTUnaryExpression.op_last + 2;

    /**
     * <code>op_AtEncode</code> = \@encode( type_spec )
     */
    public static final int op_AtEncode = IASTUnaryExpression.op_last + 3;

    /**
     * <code>op_AtSelector</code> = \@selector( class_name )
     */
    public static final int op_AtSelector = IASTUnaryExpression.op_last + 4;

    /**
     * @deprecated all constants to be defined in {@link IASTUnaryExpression}
     */
    @Deprecated
    public static final int op_last = IObjCASTUnaryExpression.op_AtSelector;

    /**
     * @since 5.1
     */
    public IObjCASTUnaryExpression copy();
}
