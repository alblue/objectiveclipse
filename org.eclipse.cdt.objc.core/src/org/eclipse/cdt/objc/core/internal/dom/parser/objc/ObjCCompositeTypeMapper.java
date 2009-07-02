/*******************************************************************************
 * Copyright (c) 2009 Wind River Systems, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.parser.util.CharArrayMap;

/**
 * Utility to map index bindings to ast bindings.
 */
public class ObjCCompositeTypeMapper {
    private class Visitor extends ASTVisitor {
        Visitor() {
            shouldVisitDeclarations = true;
        }

        @Override
        public int visit(IASTDeclaration declaration) {
            if (declaration instanceof IASTSimpleDeclaration) {
                IASTDeclSpecifier declspec = ((IASTSimpleDeclaration) declaration).getDeclSpecifier();
                if (declspec instanceof IASTCompositeTypeSpecifier) {
                    IASTCompositeTypeSpecifier cts = (IASTCompositeTypeSpecifier) declspec;
                    final IASTName name = cts.getName();
                    final char[] nameChars = name.toCharArray();
                    if (nameChars.length > 0) {
                        fStructs.put(nameChars, name);
                    }
                    return PROCESS_CONTINUE;
                }
            }
            return PROCESS_SKIP;
        }
    }

    protected CharArrayMap<IASTName> fStructs;
    private final IASTTranslationUnit fTranslationUnit;

    public ObjCCompositeTypeMapper(IASTTranslationUnit tu) {
        fTranslationUnit = tu;
    }

    public IType mapToAST(ICompositeType type) {
        if (fStructs == null) {
            fStructs = new CharArrayMap<IASTName>();
            fTranslationUnit.accept(new Visitor());
        }
        IASTName name = fStructs.get(type.getNameCharArray());
        if (name != null) {
            IBinding b = name.resolveBinding();
            if (b instanceof ICompositeType) {
                final ICompositeType mapped = (ICompositeType) b;
                if (mapped.isSameType(type)) {
                    return mapped;
                }
            }
        }
        return type;
    }
}
