/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Anton Leherbauer (Wind River Systems) - initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.parser.objc;

import org.eclipse.cdt.core.dom.parser.IBuiltinBindingsProvider;
import org.eclipse.cdt.objc.core.internal.dom.parser.ObjCBuiltinSymbolProvider;

/**
 * Abstract C parser extension configuration to help model C dialects.
 * 
 * @since 4.0
 */
public abstract class AbstractObjCParserExtensionConfiguration implements IObjCParserExtensionConfiguration {

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * getBuiltinSymbolProvider()
     */
    public IBuiltinBindingsProvider getBuiltinBindingsProvider() {
        return new ObjCBuiltinSymbolProvider();
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportAlignOfUnaryExpression()
     */
    public boolean supportAlignOfUnaryExpression() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportAttributeSpecifiers()
     */
    public boolean supportAttributeSpecifiers() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportDeclspecSpecifiers()
     */
    public boolean supportDeclspecSpecifiers() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 5.1
     */
    public boolean supportExtendedSizeofOperator() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 5.1
     */
    public boolean supportFunctionStyleAssembler() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportGCCOtherBuiltinSymbols()
     */
    public boolean supportGCCOtherBuiltinSymbols() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportGCCStyleDesignators()
     */
    public boolean supportGCCStyleDesignators() {
        return false;
    }

    /*
     * @see
     * org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#supportKnRC
     * ()
     */
    public boolean supportKnRC() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 5.1
     */
    public boolean supportParameterInfoBlock() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportStatementsInExpressions()
     */
    public boolean supportStatementsInExpressions() {
        return false;
    }

    /*
     * @seeorg.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration#
     * supportTypeofUnaryExpressions()
     */
    public boolean supportTypeofUnaryExpressions() {
        return false;
    }
}
