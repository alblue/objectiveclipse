package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisibilityLabel;

@SuppressWarnings("restriction")
public class ObjCASTVisibilityLabel extends ASTNode implements IObjCASTVisibilityLabel {

    int visibility;

    public ObjCASTVisibilityLabel(int visibility) {
        setVisibility(visibility);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDeclarations) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (action.shouldVisitDeclarations) {
            switch (action.leave(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        return true;
    }

    public IObjCASTVisibilityLabel copy() {
        ObjCASTVisibilityLabel copy = new ObjCASTVisibilityLabel(visibility);
        copy.setOffsetAndLength(this);
        return copy;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

}
