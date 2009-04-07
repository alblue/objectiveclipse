/*******************************************************************************
 * Copyright (c) 2009, Alex Blewitt and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Alex Blewitt - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.parser.objc;

import org.eclipse.cdt.core.dom.parser.GNUScannerExtensionConfiguration;

public class ObjCScannerExtensionConfiguration extends GNUScannerExtensionConfiguration {

    private static ObjCScannerExtensionConfiguration DEFAULT = new ObjCScannerExtensionConfiguration();

    public static ObjCScannerExtensionConfiguration getInstance() {
        return DEFAULT;
    }

    @SuppressWarnings("nls")
    public ObjCScannerExtensionConfiguration() {
        addMacro("__null", "(void *)0");
        addMacro("_Pragma(arg)", "");
        addMacro("__builtin_offsetof(T,m)", "((size_t) &((T *)0)->m)");
    }

    @Override
    public boolean supportAtSignInIdentifiers() {
        return true;
    }

}
