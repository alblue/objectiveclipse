package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;

public interface IObjCASTLiteralExpression extends IASTLiteralExpression {

    /**
     * <code>lk_false</code> represents the 'NO' keyword.
     */
    public static final int lk_NO = IASTLiteralExpression.lk_false;

    /**
     * <code>lk_this</code> represents the 'self' keyword.
     */
    public static final int lk_self = IASTLiteralExpression.lk_this;

    /**
     * <code>lk_true</code> represents the 'YES' keyword.
     */
    public static final int lk_YES = IASTLiteralExpression.lk_true;

    /**
     * @since 5.1
     */
    public IObjCASTLiteralExpression copy();

}
