package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTOptionalityLabel;

@SuppressWarnings("restriction")
public class ObjCASTOptionalityLabel extends ASTNode implements IObjCASTOptionalityLabel {

    int optionality;

    public ObjCASTOptionalityLabel(int optionality) {
        setOptionality(optionality);
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

    public ObjCASTOptionalityLabel copy() {
        ObjCASTOptionalityLabel copy = new ObjCASTOptionalityLabel(optionality);
        copy.setOffsetAndLength(this);
        return copy;
    }

    public int getOptionality() {
        return optionality;
    }

    public void setOptionality(int optionality) {
        this.optionality = optionality;
    }

}
