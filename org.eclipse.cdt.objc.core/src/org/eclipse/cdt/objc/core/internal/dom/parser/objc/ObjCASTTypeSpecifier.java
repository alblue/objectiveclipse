package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

// 

public class ObjCASTTypeSpecifier extends ASTNode implements IASTCompositeTypeSpecifier {

    private IASTName name;

    public ObjCASTTypeSpecifier(IASTName id) {
        // TODO Auto-generated constructor stub
    }

    public void addDeclaration(IASTDeclaration declaration) {
        // TODO Auto-generated method stub

    }

    public void addMemberDeclaration(IASTDeclaration declaration) {
        // TODO Auto-generated method stub

    }

    public ObjCASTTypeSpecifier copy() {
        throw new UnsupportedOperationException("ObjCASTTypeSpecificer:copy");
    }

    public IASTDeclaration[] getDeclarations(boolean includeInactive) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getKey() {
        // TODO Auto-generated method stub
        return 0;
    }

    public IASTDeclaration[] getMembers() {
        // TODO Auto-generated method stub
        return null;
    }

    public IASTName getName() {
        return name;
    }

    public int getRoleForName(IASTName name) {
        // TODO Auto-generated method stub
        return 0;
    }

    public IScope getScope() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getStorageClass() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isConst() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isInline() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isVolatile() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setConst(boolean value) {
        // TODO Auto-generated method stub

    }

    public void setInline(boolean value) {
        // TODO Auto-generated method stub

    }

    public void setKey(int key) {
        // TODO Auto-generated method stub

    }

    public void setName(IASTName name) {
        // TODO Auto-generated method stub

    }

    public void setStorageClass(int storageClass) {
        // TODO Auto-generated method stub

    }

    public void setVolatile(boolean value) {
        // TODO Auto-generated method stub

    }

}
