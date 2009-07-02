package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;

public interface IObjCASTVisibilityLabel extends IASTDeclaration {

    public static final int v_private = 3;

    public static final int v_protected = 2;

    public static final int v_public = 1;

    public IObjCASTVisibilityLabel copy();

    public int getVisibility();

    public void setVisibility(int visibility);
}
