package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSelectorIdExpression;

public class ObjCASTSelectorIdExpression extends ObjCASTIdExpression implements IObjCASTSelectorIdExpression {

    public ObjCASTSelectorIdExpression(IASTName id) {
        super(id);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        return super.accept(action);
    }

    @Override
    public IType getExpressionType() {
        return ObjCVisitor.getSEL(this);
    }

}
