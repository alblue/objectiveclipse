/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTASMDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayModifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCatchHandler;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTClassMemoryLayoutDeclaration;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDesignatedInitializer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTElaboratedTypeSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTEnumerationSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFieldDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMessageExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMessageSelectorExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodDeclarator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodParameterDeclaration;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTOptionalityLabel;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTPointer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTProtocolIdExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSelectorIdExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTryBlockStatement;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypeIdInitializerExpression;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypedefNameSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisibilityLabel;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCNodeFactory;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCASTKnRFunctionDeclarator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTArrayRangeDesignator;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTSimpleDeclSpecifier;

/**
 * Abstract factory implementation that creates AST nodes for C99. These can be
 * overridden in subclasses to change the implementations of the nodes.
 * 
 * @author Mike Kucera
 */
@SuppressWarnings("restriction")
public class ObjCNodeFactory implements IObjCNodeFactory {

    private static final ObjCNodeFactory DEFAULT_INSTANCE = new ObjCNodeFactory();

    public static ObjCNodeFactory getDefault() {
        return DEFAULT_INSTANCE;
    }

    public IASTArrayDeclarator newArrayDeclarator(IASTName name) {
        return new ObjCASTArrayDeclarator(name);
    }

    public IObjCASTArrayDesignator newArrayDesignator(IASTExpression exp) {
        return new ObjCASTArrayDesignator(exp);
    }

    public IObjCASTArrayModifier newArrayModifier(IASTExpression expr) {
        return new ObjCASTArrayModifier(expr);
    }

    public IObjCGCCASTArrayRangeDesignator newArrayRangeDesignatorGCC(IASTExpression floor,
            IASTExpression ceiling) {
        return new ObjCASTArrayRangeDesignator(floor, ceiling);
    }

    public IASTArraySubscriptExpression newArraySubscriptExpression(IASTExpression arrayExpr,
            IASTExpression subscript) {
        return new ObjCASTArraySubscriptExpression(arrayExpr, subscript);
    }

    public IASTASMDeclaration newASMDeclaration(String assembly) {
        return new ObjCASTASMDeclaration(assembly);
    }

    public IObjCASTBaseSpecifier newBaseSpecifier(IASTName name, boolean isProtocol) {
        return new ObjCASTBaseSpecifier(name, isProtocol);
    }

    public IASTBinaryExpression newBinaryExpression(int op, IASTExpression expr1, IASTExpression expr2) {
        return new ObjCASTBinaryExpression(op, expr1, expr2);
    }

    public IASTBreakStatement newBreakStatement() {
        return new ObjCASTBreakStatement();
    }

    public IASTCaseStatement newCaseStatement(IASTExpression expression) {
        return new ObjCASTCaseStatement(expression);
    }

    /**
     * @param operator
     */
    public IASTCastExpression newCastExpression(int operator, IASTTypeId typeId, IASTExpression operand) {
        return new ObjCASTCastExpression(typeId, operand);
    }

    public IObjCASTCatchHandler newCatchHandler(IASTDeclaration decl, IASTStatement body) {
        return new ObjCASTCatchHandler(decl, body);
    }

    public IObjCASTCompositeTypeSpecifier newCompositeTypeSpecifier(int key, IASTName name) {
        return new ObjCASTCompositeTypeSpecifier(key, name);
    }

    public IASTCompoundStatement newCompoundStatement() {
        return new ObjCASTCompoundStatement();
    }

    public IASTConditionalExpression newConditionalExpession(IASTExpression expr1, IASTExpression expr2,
            IASTExpression expr3) {
        return new ObjCASTConditionalExpression(expr1, expr2, expr3);
    }

    public IASTContinueStatement newContinueStatement() {
        return new ObjCASTContinueStatement();
    }

    public IASTDeclarationStatement newDeclarationStatement(IASTDeclaration declaration) {
        return new ObjCASTDeclarationStatement(declaration);
    }

    public IASTDeclarator newDeclarator(IASTName name) {
        return new ObjCASTDeclarator(name);
    }

    public IASTDefaultStatement newDefaultStatement() {
        return new ObjCASTDefaultStatement();
    }

    public IObjCASTDesignatedInitializer newDesignatedInitializer(IASTInitializer operandInitializer) {
        return new ObjCASTDesignatedInitializer(operandInitializer);
    }

    public IASTDoStatement newDoStatement(IASTStatement body, IASTExpression condition) {
        return new ObjCASTDoStatement(body, condition);
    }

    public IObjCASTElaboratedTypeSpecifier newElaboratedTypeSpecifier(int kind, IASTName name) {
        return new ObjCASTElaboratedTypeSpecifier(kind, name);
    }

    public IObjCASTEnumerationSpecifier newEnumerationSpecifier(IASTName name) {
        return new ObjCASTEnumerationSpecifier(name);
    }

    public IASTEnumerator newEnumerator(IASTName name, IASTExpression value) {
        return new ObjCASTEnumerator(name, value);
    }

    public IASTExpressionList newExpressionList() {
        return new ObjCASTExpressionList();
    }

    public IASTExpressionStatement newExpressionStatement(IASTExpression expr) {
        return new ObjCASTExpressionStatement(expr);
    }

    public IASTFieldDeclarator newFieldDeclarator(IASTName name, IASTExpression bitFieldSize) {
        return new ObjCASTFieldDeclarator(name, bitFieldSize);
    }

    public IObjCASTFieldDesignator newFieldDesignator(IASTName name) {
        return new ObjCASTFieldDesignator(name);
    }

    public IASTFieldReference newFieldReference(IASTName name, IASTExpression owner) {
        return new ObjCASTFieldReference(name, owner);
    }

    public IASTForStatement newForStatement(IASTStatement init, IASTExpression condition,
            IASTExpression iterationExpression, IASTStatement body) {
        return new ObjCASTForStatement(init, condition, iterationExpression, body);
    }

    public IASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr, IASTExpression argList) {
        return new ObjCASTFunctionCallExpression(idExpr, argList);
    }

    public IASTStandardFunctionDeclarator newFunctionDeclarator(IASTName name) {
        return new ObjCASTFunctionDeclarator(name);
    }

    public IASTFunctionDefinition newFunctionDefinition(IASTDeclSpecifier declSpecifier,
            IASTFunctionDeclarator declarator, IASTStatement bodyStatement) {
        return new ObjCASTFunctionDefinition(declSpecifier, declarator, bodyStatement);
    }

    public IGNUASTCompoundStatementExpression newGNUCompoundStatementExpression(
            IASTCompoundStatement compoundStatement) {
        return new ObjCASTCompoundStatementExpression(compoundStatement);
    }

    public IASTGotoStatement newGotoStatement(IASTName name) {
        return new ObjCASTGotoStatement(name);
    }

    public IASTIdExpression newIdExpression(IASTName name) {
        return new ObjCASTIdExpression(name);
    }

    public IASTIfStatement newIfStatement(IASTExpression expr, IASTStatement thenStat,
            IASTStatement elseClause) {
        return new ObjCASTIfStatement(expr, thenStat, elseClause);
    }

    public IASTInitializerExpression newInitializerExpression(IASTExpression expression) {
        return new ObjCASTInitializerExpression(expression);
    }

    public IASTInitializerList newInitializerList() {
        return new ObjCASTInitializerList();
    }

    public IObjCASTKnRFunctionDeclarator newKnRFunctionDeclarator(IASTName[] parameterNames,
            IASTDeclaration[] parameterDeclarations) {
        return new ObjCASTKnRFunctionDeclarator(parameterNames, parameterDeclarations);
    }

    public IASTLabelStatement newLabelStatement(IASTName name, IASTStatement nestedStatement) {
        return new ObjCASTLabelStatement(name, nestedStatement);
    }

    public IASTLiteralExpression newLiteralExpression(int kind, String rep) {
        return new ObjCASTLiteralExpression(kind, rep.toCharArray());
    }

    public IObjCASTClassMemoryLayoutDeclaration newMemoryLayoutDeclaration(IASTName clsName) {
        return new ObjCASTClassMemoryLayoutDeclaration(clsName);
    }

    public IObjCASTMessageExpression newMessageExpression(IASTExpression objExpr, IASTExpression selectorExpr) {
        return new ObjCASTMessageExpression(objExpr, selectorExpr);
    }

    public IObjCASTMessageSelectorExpression newMessageSelectorExpression(IASTExpression selectorId,
            IASTExpression parameters) {
        return new ObjCASTMessageSelectorExpression(selectorId, parameters);
    }

    public IObjCASTMethodDeclarator newMethodDeclarator(IASTName name) {
        return new ObjCASTMethodDeclarator(name);
    }

    public IObjCASTMethodParameterDeclaration newMethodParameterDeclaration(IASTName selector,
            IASTDeclSpecifier declSpec, IASTDeclarator declarator) {
        return new ObjCASTMethodParameterDeclaration(selector, declSpec, declarator);
    }

    public IASTName newName() {
        return new ObjCASTName();
    }

    public IASTName newName(char[] name) {
        return new ObjCASTName(name);
    }

    public IASTNullStatement newNullStatement() {
        return new ObjCASTNullStatement();
    }

    public IObjCASTOptionalityLabel newOptionalityLabel(int optionality) {
        return new ObjCASTOptionalityLabel(optionality);
    }

    public IASTParameterDeclaration newParameterDeclaration(IASTDeclSpecifier declSpec,
            IASTDeclarator declarator) {
        return new ObjCASTParameterDeclaration(declSpec, declarator);
    }

    public IObjCASTPointer newPointer() {
        return new ObjCASTPointer();
    }

    public IASTProblem newProblem(int id, char[] arg, boolean error) {
        return new ObjCASTProblem(id, arg, error);
    }

    public IASTProblemDeclaration newProblemDeclaration(IASTProblem problem) {
        return new ObjCASTProblemDeclaration(problem);
    }

    public IASTProblemExpression newProblemExpression(IASTProblem problem) {
        return new ObjCASTProblemExpression(problem);
    }

    public IASTProblemStatement newProblemStatement(IASTProblem problem) {
        return new ObjCASTProblemStatement(problem);
    }

    public IObjCASTProtocolIdExpression newProtocolExpression(IASTName name) {
        return new ObjCASTProtocolIdExpression(name);
    }

    public IASTReturnStatement newReturnStatement(IASTExpression retValue) {
        return new ObjCASTReturnStatement(retValue);
    }

    public IObjCASTSelectorIdExpression newSelectorExpression(IASTName id) {
        return new ObjCASTSelectorIdExpression(id);
    }

    public IASTSimpleDeclaration newSimpleDeclaration(IASTDeclSpecifier declSpecifier) {
        return new ObjCASTSimpleDeclaration(declSpecifier);
    }

    public IObjCASTSimpleDeclSpecifier newSimpleDeclSpecifier() {
        return new ObjCASTSimpleDeclSpecifier();
    }

    public IObjCGCCASTSimpleDeclSpecifier newSimpleDeclSpecifierGCC(IASTExpression typeofExpression) {
        return new ObjCGCCASTSimpleDeclSpecifier(typeofExpression);
    }

    public IASTSwitchStatement newSwitchStatement(IASTExpression controller, IASTStatement body) {
        return new ObjCASTSwitchStatement(controller, body);
    }

    public IASTTranslationUnit newTranslationUnit() {
        ObjCASTTranslationUnit tu = new ObjCASTTranslationUnit();
        tu.setASTNodeFactory(this);
        return tu;
    }

    public IObjCASTTryBlockStatement newTryBlockStatement(IASTStatement body) {
        return new ObjCASTTryBlockStatement(body);
    }

    public IObjCASTTypedefNameSpecifier newTypedefNameSpecifier(IASTName name) {
        return new ObjCASTTypedefNameSpecifier(name);
    }

    public IASTTypeId newTypeId(IASTDeclSpecifier declSpecifier, IASTDeclarator declarator) {
        return new ObjCASTTypeId(declSpecifier, declarator);
    }

    public IASTTypeIdExpression newTypeIdExpression(int operator, IASTTypeId typeId) {
        return new ObjCASTTypeIdExpression(operator, typeId);
    }

    public IObjCASTTypeIdInitializerExpression newTypeIdInitializerExpression(IASTTypeId typeId,
            IASTInitializer initializer) {
        return new ObjCASTTypeIdInitializerExpression(typeId, initializer);
    }

    public IASTUnaryExpression newUnaryExpression(int operator, IASTExpression operand) {
        return new ObjCASTUnaryExpression(operator, operand);
    }

    public IObjCASTVisibilityLabel newVisibilityLabel(int visibility) {
        return new ObjCASTVisibilityLabel(visibility);
    }

    public IASTWhileStatement newWhileStatement(IASTExpression condition, IASTStatement body) {
        return new ObjCASTWhileStatement(condition, body);
    }
}
