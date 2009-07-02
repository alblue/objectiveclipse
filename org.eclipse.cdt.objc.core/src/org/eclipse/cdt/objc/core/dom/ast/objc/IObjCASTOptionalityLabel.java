package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;

public interface IObjCASTOptionalityLabel extends IASTDeclaration {

    public static final int v_required = 2;

    public static final int v_optional = 1;

    public IObjCASTOptionalityLabel copy();

    public int getOptionality();

    public void setOptionality(int optionality);
}
