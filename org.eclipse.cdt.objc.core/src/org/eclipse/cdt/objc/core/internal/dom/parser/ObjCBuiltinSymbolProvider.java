/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation Markus Schorn (Wind River
 * Systems) Anton Leherbauer (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser;

import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.internal.core.dom.parser.GCCBuiltinSymbolProvider;

/**
 * This is the IBuiltinBindingsProvider used to implement the standard builtin
 * bindings:
 */
@SuppressWarnings("restriction")
public class ObjCBuiltinSymbolProvider extends GCCBuiltinSymbolProvider {

    public ObjCBuiltinSymbolProvider() {
        super(ParserLanguage.C);
    }

}
