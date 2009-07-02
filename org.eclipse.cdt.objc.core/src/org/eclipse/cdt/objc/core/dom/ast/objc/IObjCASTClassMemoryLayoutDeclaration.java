package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;

public interface IObjCASTClassMemoryLayoutDeclaration extends IASTDeclaration {

    public static final ASTNodeProperty CMEMORYLAYOUT = new ASTNodeProperty(
            "IObjCASTClassMemoryLayoutDeclaration.CMEMORYLAYOUT - relationship between IASTName and IObjCASTClassMemoryLayoutDeclaration"); //$NON-NLS-1$

    IASTName getClassName();

    IObjCASTClassMemoryLayoutDeclaration copy();

}
