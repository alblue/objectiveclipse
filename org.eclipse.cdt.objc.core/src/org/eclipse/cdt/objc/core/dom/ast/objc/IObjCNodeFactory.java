/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTArrayRangeDesignator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTSimpleDeclSpecifier;

/**
 * Factory for AST nodes for the C programming language.
 * 
 * @author Mike Kucera
 * @since 5.1
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCNodeFactory extends INodeFactory {

    public IObjCASTTryBlockStatement newTryBlockStatement(IASTStatement body);

    public IObjCASTCatchHandler newCatchHandler(IASTDeclaration decl, IASTStatement body);

    public IObjCASTArrayDesignator newArrayDesignator(IASTExpression exp);

    public IObjCASTArrayModifier newArrayModifier(IASTExpression expr);

    public IObjCGCCASTArrayRangeDesignator newArrayRangeDesignatorGCC(IASTExpression floor,
            IASTExpression ceiling);

    public IObjCASTBaseSpecifier newBaseSpecifier(IASTName name, boolean isProtocol);

    public IObjCASTCompositeTypeSpecifier newCompositeTypeSpecifier(int key, IASTName name);

    public IObjCASTDesignatedInitializer newDesignatedInitializer(IASTInitializer rhs);

    public IObjCASTElaboratedTypeSpecifier newElaboratedTypeSpecifier(int kind, IASTName name);

    public IObjCASTEnumerationSpecifier newEnumerationSpecifier(IASTName name);

    public IObjCASTFieldDesignator newFieldDesignator(IASTName name);

    public IObjCASTKnRFunctionDeclarator newKnRFunctionDeclarator(IASTName[] parameterNames,
            IASTDeclaration[] parameterDeclarations);

    public IObjCASTMessageExpression newMessageExpression(IASTExpression objExpr, IASTExpression selectorExpr);

    public IObjCASTMessageSelectorExpression newMessageSelectorExpression(IASTExpression selectorId,
            IASTExpression parameters);

    public IObjCASTPointer newPointer();

    public IObjCASTProtocolIdExpression newProtocolExpression(IASTName name);

    public IObjCASTSelectorIdExpression newSelectorExpression(IASTName id);

    public IObjCASTSimpleDeclSpecifier newSimpleDeclSpecifier();

    public IObjCGCCASTSimpleDeclSpecifier newSimpleDeclSpecifierGCC(IASTExpression typeofExpression);

    public IObjCASTTypedefNameSpecifier newTypedefNameSpecifier(IASTName name);

    public IObjCASTTypeIdInitializerExpression newTypeIdInitializerExpression(IASTTypeId typeId,
            IASTInitializer initializer);

    public IObjCASTVisibilityLabel newVisibilityLabel(int visibility);

    public IObjCASTOptionalityLabel newOptionalityLabel(int optionality);

    public IObjCASTMethodDeclarator newMethodDeclarator(IASTName name);

    public IObjCASTMethodParameterDeclaration newMethodParameterDeclaration(IASTName selector,
            IASTDeclSpecifier declSpec, IASTDeclarator declarator);

    public IObjCASTClassMemoryLayoutDeclaration newMemoryLayoutDeclaration(IASTName clsName);

}