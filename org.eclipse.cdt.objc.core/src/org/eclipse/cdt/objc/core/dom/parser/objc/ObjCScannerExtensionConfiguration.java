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
import org.eclipse.cdt.core.parser.IToken;

public class ObjCScannerExtensionConfiguration extends GNUScannerExtensionConfiguration {

    private static ObjCScannerExtensionConfiguration DEFAULT = new ObjCScannerExtensionConfiguration();

    public static ObjCScannerExtensionConfiguration getInstance() {
        return DEFAULT;
    }

    @SuppressWarnings("nls")
    public ObjCScannerExtensionConfiguration() {
        // Copying these from somewhere?
        addMacro("__null", "(void *)0");
        addMacro("_Pragma(arg)", "");
        addMacro("__builtin_offsetof(T,m)", "((size_t) &((T *)0)->m)");
        addKeyword("self");
        addKeyword("id");
        // Objective C Types
        addKeyword("SEL");
        addKeyword("BOOL");
        // Objective C keywords
        addKeyword("@end");
        addKeyword("@implementation");
        addKeyword("@interface");
        addKeyword("@protocol");
        addKeyword("@selector");
        // TODO until Eclipse bug 272124 is resolved
        addKeyword("end");
        addKeyword("implementation");
        addKeyword("interface");
        addKeyword("protocol");
        addKeyword("selector");
        // Objective C literals - should these be macros?
        addKeyword("nil");
        addKeyword("YES");
        addKeyword("NO");
    }

    private void addKeyword(String keyword) {
        addKeyword(keyword.toCharArray(), IToken.tIDENTIFIER);
    }

    @Override
    public boolean supportAtSignInIdentifiers() {
        return true;
    }

}
