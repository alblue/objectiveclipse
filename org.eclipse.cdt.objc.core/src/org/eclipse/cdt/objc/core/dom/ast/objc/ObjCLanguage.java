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
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.IContributedModelBuilder;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.objc.core.ObjCPlugin;
import org.eclipse.cdt.objc.core.dom.parser.objc.ObjCScannerExtensionConfiguration;
import org.eclipse.cdt.objc.core.internal.core.dom.parser.GNUObjCSourceParser;
import org.eclipse.core.runtime.CoreException;

public class ObjCLanguage extends AbstractCLikeLanguage {

    @Override
    public IContributedModelBuilder createModelBuilder(ITranslationUnit tu) {
        // TODO Auto-generated method stub
        // System.err.println("Creating model builder");
        return null;
    }

    @Override
    protected ISourceCodeParser createParser(IScanner scanner, ParserMode parserMode,
            IParserLogService logService, IIndex index) {

        return new GNUObjCSourceParser(scanner, parserMode, logService, GCCParserExtensionConfiguration
                .getInstance(), index);
    }

    @Override
    public IASTTranslationUnit getASTTranslationUnit(CodeReader reader, IScannerInfo scanInfo,
            ICodeReaderFactory fileCreator, IIndex index, IParserLogService log) throws CoreException {
        // TODO Auto-generated method stub
        // System.err.println("Getting translation unit");
        return null;
    }

    @Override
    public IASTCompletionNode getCompletionNode(CodeReader reader, IScannerInfo scanInfo,
            ICodeReaderFactory fileCreator, IIndex index, IParserLogService log, int offset)
            throws CoreException {
        // TODO Auto-generated method stub
        // System.err.println("Getting completion node");
        return null;
    }

    public String getId() {
        return ObjCPlugin.PLUGIN_ID + ".objcLanguage"; //$NON-NLS-1$
    }

    public int getLinkageID() {
        return ILinkage.OBJC_LINKAGE_ID;
    }

    @Override
    protected ParserLanguage getParserLanguage() {
        // FIXME This should really be an ObjC version of same
        return ParserLanguage.C;
    }

    @Override
    protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
        return ObjCScannerExtensionConfiguration.getInstance();
    }

    @Override
    public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start, int length) {
        // System.err.println("In 'get selected names' call");
        // TODO Auto-generated method stub
        return null;
    }
}
