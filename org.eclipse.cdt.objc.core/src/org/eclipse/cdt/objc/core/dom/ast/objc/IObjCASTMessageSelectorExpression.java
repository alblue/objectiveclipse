package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;

public interface IObjCASTMessageSelectorExpression extends IASTFunctionCallExpression {

    /**
     * @since 5.1
     */
    public IObjCASTMessageSelectorExpression copy();

}
