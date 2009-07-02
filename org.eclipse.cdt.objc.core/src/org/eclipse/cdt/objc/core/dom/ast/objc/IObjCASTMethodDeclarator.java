package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;

public interface IObjCASTMethodDeclarator extends IASTStandardFunctionDeclarator {

    public boolean isProtocolMethod();

    public void setProtocolMethod(boolean isProtocol);

}
