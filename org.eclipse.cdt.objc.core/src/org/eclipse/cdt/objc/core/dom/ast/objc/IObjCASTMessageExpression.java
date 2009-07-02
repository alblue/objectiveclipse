package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

public interface IObjCASTMessageExpression extends IASTExpression {

    /**
     * <code>OBJ</code> represents the relationship between a
     * <code>IObjCASTMethodCallExpression</code> and its
     * <code>IASTExpression</code> (message receiver).
     */
    public static final ASTNodeProperty RECEIVER = new ASTNodeProperty(
            "IObjCASTMethodCallExpression.RECEIVER - IASTExpression (name) for IObjCASTMethodCallExpression"); //$NON-NLS-1$

    /**
     * Set the receiver expression.
     * 
     * @param expression
     *            <code>IASTExpression</code> representing the receiver
     */
    public void setReceiverExpression(IASTExpression expression);

    /**
     * Get the receiver expression.
     * 
     * @return <code>IASTExpression</code> representing the receiver
     */
    public IASTExpression getReceiverExpression();

    /**
     * <code>OBJ</code> represents the relationship between a
     * <code>IObjCASTMethodCallExpression</code> and its
     * <code>IASTExpression</code> (message receiver).
     */
    public static final ASTNodeProperty SELECTOR = new ASTNodeProperty(
            "IObjCASTMethodCallExpression.RECEIVER - IASTExpression (name) for IObjCASTMethodCallExpression"); //$NON-NLS-1$

    /**
     * Set the selector expression.
     * 
     * @param expression
     *            <code>IASTExpression</code> representing the receiver
     */
    public void setSelectorExpression(IASTExpression expression);

    /**
     * Get the selector expression.
     * 
     * @return <code>IASTExpression</code> representing the receiver
     */
    public IASTExpression getSelectorExpression();

}
