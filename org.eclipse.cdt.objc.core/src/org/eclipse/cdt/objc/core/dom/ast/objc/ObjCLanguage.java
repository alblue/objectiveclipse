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
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.pdom.dom.IPDOMLinkageFactory;
import org.eclipse.cdt.internal.core.pdom.dom.c.PDOMCLinkageFactory;
import org.eclipse.cdt.objc.core.ObjCPlugin;
import org.eclipse.cdt.objc.core.dom.parser.objc.IObjCParserExtensionConfiguration;
import org.eclipse.cdt.objc.core.dom.parser.objc.ObjCParserExtensionConfiguration;
import org.eclipse.cdt.objc.core.dom.parser.objc.ObjCScannerExtensionConfiguration;
import org.eclipse.cdt.objc.core.internal.dom.parser.objc.GNUObjCSourceParser;
import org.eclipse.cdt.objc.core.internal.parser.scanner.ObjCPreprocessor;

@SuppressWarnings("restriction")
public class ObjCLanguage extends AbstractCLikeLanguage {

    private static final ObjCLanguage DEFAULT_INSTANCE = new ObjCLanguage();

    public static final String ID = ObjCPlugin.PLUGIN_ID + ".objcLanguage"; //$NON-NLS-1$ 
    protected static final ObjCParserExtensionConfiguration OBJC_PARSER_EXTENSION = ObjCParserExtensionConfiguration
            .getInstance();

    protected static final ObjCScannerExtensionConfiguration OBJC_SCANNER_EXTENSION = ObjCScannerExtensionConfiguration
            .getInstance();

    public static ObjCLanguage getDefault() {
        return DEFAULT_INSTANCE;
    }

    @Override
    protected ISourceCodeParser createParser(IScanner scanner, ParserMode parserMode,
            IParserLogService logService, IIndex index) {

        return new GNUObjCSourceParser(scanner, parserMode, logService, GCCParserExtensionConfiguration
                .getInstance(), index);
    }

    @Override
    protected IScanner createScanner(CodeReader reader, IScannerInfo scanInfo,
            ICodeReaderFactory fileCreator, IParserLogService log) {
        return new ObjCPreprocessor(reader, scanInfo, getParserLanguage(), log,
                getScannerExtensionConfiguration(), fileCreator);
    }

    @Override
    @SuppressWarnings( { "unchecked" })
    public Object getAdapter(Class adapter) {
        if (adapter == IPDOMLinkageFactory.class) {
            return new PDOMCLinkageFactory();
        }
        return super.getAdapter(adapter);
    }

    public String getId() {
        return ID;
    }

    public int getLinkageID() {
        return ILinkage.OBJC_LINKAGE_ID;
    }

    /**
     * Returns the extension configuration used for creating the parser.
     * 
     * @since 5.1
     */
    protected IObjCParserExtensionConfiguration getParserExtensionConfiguration() {
        return OBJC_PARSER_EXTENSION;
    }

    @Override
    protected ParserLanguage getParserLanguage() {
        return ParserLanguage.C;
    }

    @Override
    protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
        return OBJC_SCANNER_EXTENSION;
    }
}
