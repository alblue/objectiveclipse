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
import org.eclipse.cdt.objc.core.dom.parser.IObjCToken;
import org.eclipse.cdt.objc.core.dom.parser.ObjCKeywords;

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

        // Objective C Types
        // <objc/objc.h> defines these typedefs */
        // addKeyword(ObjCKeywords.cp_SEL, IObjCToken.t_SEL);
        // addKeyword(ObjCKeywords.cp_BOOL, IObjCToken.t_BOOL);
        // addKeyword(ObjCKeywords.cp_id, IObjCToken.t_id);

        // Objective C keywords
        // <Foundation/NSObject.h> defines these as methods */
        // addKeyword(ObjCKeywords.cp_self, IObjCToken.t_self);
        // addKeyword(ObjCKeywords.cp_super, IObjCToken.t_super);

        addKeyword(ObjCKeywords.cp_AtTry, IObjCToken.t_AtTry);
        addKeyword(ObjCKeywords.cp_AtCatch, IObjCToken.t_AtCatch);
        addKeyword(ObjCKeywords.cp_AtFinally, IObjCToken.t_AtFinally);
        addKeyword(ObjCKeywords.cp_AtThrow, IObjCToken.t_AtThrow);
        addKeyword(ObjCKeywords.cp_AtRequired, IObjCToken.t_AtRequired);
        addKeyword(ObjCKeywords.cp_AtOptional, IObjCToken.t_AtOptional);
        addKeyword(ObjCKeywords.cp_AtDynamic, IObjCToken.t_AtDynamic);
        addKeyword(ObjCKeywords.cp_AtSynthesize, IObjCToken.t_AtSynthesize);
        addKeyword(ObjCKeywords.cp_AtSynchronized, IObjCToken.t_AtSynchronized);
        addKeyword(ObjCKeywords.cp_AtClass, IObjCToken.t_AtClass);
        addKeyword(ObjCKeywords.cp_AtDefs, IObjCToken.t_AtDefs);
        addKeyword(ObjCKeywords.cp_AtEncode, IObjCToken.t_AtEncode);
        addKeyword(ObjCKeywords.cp_AtEnd, IObjCToken.t_AtEnd);
        addKeyword(ObjCKeywords.cp_AtInterface, IObjCToken.t_AtInterface);
        addKeyword(ObjCKeywords.cp_AtImplementation, IObjCToken.t_AtImplementation);
        addKeyword(ObjCKeywords.cp_AtPrivate, IObjCToken.t_AtPrivate);
        addKeyword(ObjCKeywords.cp_AtProtected, IObjCToken.t_AtProtected);
        addKeyword(ObjCKeywords.cp_AtProtocol, IObjCToken.t_AtProtocol);
        addKeyword(ObjCKeywords.cp_AtPublic, IObjCToken.t_AtPublic);
        addKeyword(ObjCKeywords.cp_AtSelector, IObjCToken.t_AtSelector);

        addKeyword(ObjCKeywords.cp_in, IObjCToken.t_in);
        addKeyword(ObjCKeywords.cp_out, IObjCToken.t_out);
        addKeyword(ObjCKeywords.cp_inout, IObjCToken.t_inout);
        addKeyword(ObjCKeywords.cp_bycopy, IObjCToken.t_bycopy);
        addKeyword(ObjCKeywords.cp_byref, IObjCToken.t_byref);
        addKeyword(ObjCKeywords.cp_oneway, IObjCToken.t_oneway);

        // Objective C literals - should these be macros?
        // <objc/objc.h> defines these macros */
        // addKeyword(ObjCKeywords.cp_nil, IObjCToken.t_nil);
        // addKeyword(ObjCKeywords.cp_Nil, IObjCToken.t_nil);
        // addKeyword(ObjCKeywords.cp_YES, IObjCToken.t_YES);
        // addKeyword(ObjCKeywords.cp_NO, IObjCToken.t_NO);
    }

    @Override
    public boolean supportAtSignInIdentifiers() {
        return true;
    }

}
