package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTProtocolIdExpression;

public class ObjCASTProtocolIdExpression extends ObjCASTIdExpression implements IObjCASTProtocolIdExpression {
    static final IASTName protocolClassName = new ObjCASTName("Protocol".toCharArray()); //$NON-NLS-1$

    public ObjCASTProtocolIdExpression(IASTName id) {
        super(id);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        return super.accept(action);
    }

    @Override
    public IType getExpressionType() {
        return ObjCVisitor.getProtocol(protocolClassName);
    }

}
