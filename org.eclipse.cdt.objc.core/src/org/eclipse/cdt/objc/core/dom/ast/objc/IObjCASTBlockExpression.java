package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

public interface IObjCASTBlockExpression extends IASTExpression {

    public static final ASTNodeProperty BLOCK_BODY = new ASTNodeProperty(
            "IObjCASTBlockClosureExpression.BLOCK_BODY - Body for IObjCASTBlockClosureExpression"); //$NON-NLS-1$

    public static final ASTNodeProperty BLOCK_PARAMETER = new ASTNodeProperty(
            "IObjCASTBlockClosureExpression.BLOCK_PARAMETER - Parameter for IObjCASTBlockClosureExpression"); //$NON-NLS-1$

    /**
     * Add a parameter.
     * 
     * @param parameter
     *            <code>IASTParameterDeclaration</code>
     */
    public void addParameterDeclaration(IASTParameterDeclaration parameter);

    /**
     * @since 5.1
     */
    public IObjCASTBlockExpression copy();

    /**
     * Get the body of the block.
     */
    public IASTStatement getBody();

    /**
     * Gets the parameter declarations for the block
     * 
     * @return array of IASTParameterDeclaration
     */
    public IASTParameterDeclaration[] getParameters();

    /**
     * Set the body of the block.
     * 
     * @param statement
     */
    public void setBody(IASTStatement statement);

    /**
     * Set whether or not this block takes a variable number or arguments.
     * 
     * @param value
     *            boolean
     */
    public void setVarArgs(boolean value);

    /**
     * Does this block take a variable number of arguments?
     * 
     * @return boolean
     */
    public boolean takesVarArgs();

}
