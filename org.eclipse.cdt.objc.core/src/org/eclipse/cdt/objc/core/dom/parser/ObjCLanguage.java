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
package org.eclipse.cdt.objc.core.dom.parser;

import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.objc.core.Activator;

public class ObjCLanguage extends AbstractCLikeLanguage {

    protected static final GCCParserExtensionConfiguration C_GNU_PARSER_EXTENSION = GCCParserExtensionConfiguration
            .getInstance();
    protected static final GCCScannerExtensionConfiguration C_GNU_SCANNER_EXTENSION = GCCScannerExtensionConfiguration
            .getInstance();
    private static final ObjCLanguage DEFAULT_INSTANCE = new ObjCLanguage();

    public static final String ID = Activator.PLUGIN_ID + ".objc"; //$NON-NLS-1$ 

    public static ObjCLanguage getDefault() {
        return DEFAULT_INSTANCE;
    }

    @Override
    protected ISourceCodeParser createParser(IScanner scanner, ParserMode parserMode,
            IParserLogService logService, IIndex index) {
        // TODO Ideally base it of the GCC one
        return null;
    }

    public String getId() {
        return ID;
    }

    public int getLinkageID() {
        // TODO CDT this is a closed set in CDT - how do we extend?
        return 0;
    }

    /**
     * Returns the extension configuration used for creating the parser.
     * 
     * @since 5.1
     */
    protected ICParserExtensionConfiguration getParserExtensionConfiguration() {
        return C_GNU_PARSER_EXTENSION;
    }

    @Override
    protected ParserLanguage getParserLanguage() {
        // TODO CDT this is a closed enum in CDT - how do we extend?
        return null;
    }

    @Override
    protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
        return C_GNU_SCANNER_EXTENSION;
    }

}
