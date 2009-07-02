package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisitor;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTCompositeTypeSpecifier.IObjCASTBaseSpecifier;

@SuppressWarnings("restriction")
public class ObjCASTBaseSpecifier extends ASTNode implements IASTCompletionContext, IObjCASTBaseSpecifier {

    private IASTName fName;
    private boolean fProtocol;

    public ObjCASTBaseSpecifier() {
        fProtocol = false;
    }

    public ObjCASTBaseSpecifier(IASTName name, boolean isProtocol) {
        fProtocol = isProtocol;
        setName(name);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitBaseSpecifiers && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }

        if (!fName.accept(action)) {
            return false;
        }

        if (action.shouldVisitBaseSpecifiers && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).leave(this)) {
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

    public IObjCASTBaseSpecifier copy() {
        ObjCASTBaseSpecifier copy = new ObjCASTBaseSpecifier();
        copy.setName(fName == null ? null : fName.copy());
        copy.fProtocol = fProtocol;
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IBinding[] findBindings(IASTName n, boolean isPrefix) {
        return null;
    }

    public IASTName getName() {
        return fName;
    }

    public int getRoleForName(IASTName name) {
        if (name == fName) {
            return r_reference;
        }
        return r_unclear;
    }

    public boolean isProtocol() {
        return fProtocol;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        fName = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(NAME);
        }
    }

    public void setProtocol(boolean proto) {
        fProtocol = proto;
    }

}
