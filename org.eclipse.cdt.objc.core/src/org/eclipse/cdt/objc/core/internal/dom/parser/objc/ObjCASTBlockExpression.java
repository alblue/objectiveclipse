package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.c.ICFunctionScope;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTBlockExpression;

@SuppressWarnings("restriction")
public class ObjCASTBlockExpression extends ASTNode implements IObjCASTBlockExpression,
        IASTAmbiguityParent {

    private IASTStatement bodyStatement;
    private IASTParameterDeclaration[] parameters = null;
    private int parametersPos = -1;
    private ICFunctionScope scope;
    private boolean varArgs;

    public ObjCASTBlockExpression() {
        super();
    }

    public void addParameterDeclaration(IASTParameterDeclaration parameter) {
        assertNotFrozen();
        if (parameter != null) {
            parameter.setParent(this);
            parameter.setPropertyInParent(BLOCK_PARAMETER);
            parameters = (IASTParameterDeclaration[]) ArrayUtil.append(IASTParameterDeclaration.class,
                    parameters, ++parametersPos, parameter);
        }
    }

    public IObjCASTBlockExpression copy() {
        ObjCASTBlockExpression copy = new ObjCASTBlockExpression();
        copy.varArgs = varArgs;
        for (IASTParameterDeclaration param : getParameters()) {
            copy.addParameterDeclaration(param == null ? null : param.copy());
        }
        copy.setBody(bodyStatement == null ? null : bodyStatement.copy());
        return copy;
    }

    public IASTStatement getBody() {
        return bodyStatement;
    }

    public IType getExpressionType() {
        return null;
    }

    public IASTParameterDeclaration[] getParameters() {
        if (parameters == null) {
            return IASTParameterDeclaration.EMPTY_PARAMETERDECLARATION_ARRAY;
        }
        parameters = (IASTParameterDeclaration[]) ArrayUtil.removeNullsAfter(IASTParameterDeclaration.class,
                parameters, parametersPos);
        return parameters;
    }

    public IScope getScope() {
        if (scope == null) {
            scope = new ObjCBlockScope(this);
        }
        return scope;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                if (child == parameters[i]) {
                    other.setPropertyInParent(child.getPropertyInParent());
                    other.setParent(child.getParent());
                    parameters[i] = (IASTParameterDeclaration) other;
                    return;
                }
            }
        }
        if (bodyStatement == child) {
            other.setPropertyInParent(bodyStatement.getPropertyInParent());
            other.setParent(bodyStatement.getParent());
            bodyStatement = (IASTStatement) other;
        }
    }

    public void setBody(IASTStatement statement) {
        assertNotFrozen();
        bodyStatement = statement;
        if (statement != null) {
            statement.setParent(this);
            statement.setPropertyInParent(BLOCK_BODY);
        }
    }

    public void setVarArgs(boolean value) {
        assertNotFrozen();
        varArgs = value;
    }

    public boolean takesVarArgs() {
        return varArgs;
    }

}
