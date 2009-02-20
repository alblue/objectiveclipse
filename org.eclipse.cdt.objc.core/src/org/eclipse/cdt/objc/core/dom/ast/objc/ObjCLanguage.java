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
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.IContributedModelBuilder;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.core.runtime.CoreException;

public class ObjCLanguage extends AbstractLanguage {

    public ObjCLanguage() {
        // TODO Auto-generated constructor stub
    }

    public IContributedModelBuilder createModelBuilder(ITranslationUnit tu) {
        // TODO Auto-generated method stub
        return null;
    }

    public IASTTranslationUnit getASTTranslationUnit(CodeReader reader, IScannerInfo scanInfo,
            ICodeReaderFactory fileCreator, IIndex index, IParserLogService log) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    public IASTCompletionNode getCompletionNode(CodeReader reader, IScannerInfo scanInfo,
            ICodeReaderFactory fileCreator, IIndex index, IParserLogService log, int offset)
            throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLinkageID() {
        // TODO Auto-generated method stub
        return 0;
    }

    public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start, int length) {
        // TODO Auto-generated method stub
        return null;
    }

}
