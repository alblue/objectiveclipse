/*******************************************************************************
 * Copyright (c) 2009, Leonardo Pessoa and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Leonardo Pessoa - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.model;

public class ObjCModelException extends Exception {

    public ObjCModelException() {
        super();
    }

    public ObjCModelException(String message) {
        super(message);
    }

    public ObjCModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjCModelException(Throwable cause) {
        super(cause);
    }

}
