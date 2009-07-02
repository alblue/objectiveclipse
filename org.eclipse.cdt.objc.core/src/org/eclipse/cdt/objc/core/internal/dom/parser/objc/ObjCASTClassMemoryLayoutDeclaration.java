package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTClassMemoryLayoutDeclaration;

@SuppressWarnings("restriction")
public class ObjCASTClassMemoryLayoutDeclaration extends ASTNode implements
        IObjCASTClassMemoryLayoutDeclaration {

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

        if (className != null) {
            if (!className.accept(action)) {
                return false;
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

    public ObjCASTClassMemoryLayoutDeclaration() {
    }

    public ObjCASTClassMemoryLayoutDeclaration(IASTName className) {
        setClassName(className);
    }

    public void setClassName(IASTName className) {
        assertNotFrozen();
        this.className = className;
        if (className != null) {
            className.setParent(this);
            className.setPropertyInParent(CMEMORYLAYOUT);
        }
    }

    @Override
    public void freeze() {
        super.freeze();
    }

    IASTName className;

    public IObjCASTClassMemoryLayoutDeclaration copy() {
        ObjCASTClassMemoryLayoutDeclaration copy = new ObjCASTClassMemoryLayoutDeclaration();
        copy.setClassName(className == null ? null : className.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTName getClassName() {
        return className;
    }

}
