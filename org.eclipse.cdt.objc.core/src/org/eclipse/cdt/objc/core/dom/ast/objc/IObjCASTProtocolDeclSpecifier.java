/**
 * 
 */
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;

public interface IObjCASTProtocolDeclSpecifier extends IASTNode, IASTNameOwner {

    /**
     * Is bycopy keyword used?
     * 
     * @return boolean
     */
    public boolean isByCopy();

    /**
     * Is byref keyword used?
     * 
     * @return boolean
     */
    public boolean isByRef();

    /**
     * Is in keyword used?
     * 
     * @return boolean
     */
    public boolean isIn();

    /**
     * Is inout keyword used?
     * 
     * @return boolean
     */
    public boolean isInOut();

    /**
     * Is oneway keyword used?
     * 
     * @return boolean
     */
    public boolean isOneWay();

    /**
     * Is out keyword used?
     * 
     * @return boolean
     */
    public boolean isOut();

    /**
     * Set bycopy to value.
     * 
     * @param value
     */
    public void setByCopy(boolean value);

    /**
     * Set byref to value.
     * 
     * @param value
     */
    public void setByRef(boolean value);

    /**
     * Set in to value.
     * 
     * @param value
     */
    public void setIn(boolean value);

    /**
     * Set inout to value.
     * 
     * @param value
     */
    public void setInOut(boolean value);

    /**
     * Set oneway to value.
     * 
     * @param value
     */
    public void setOneWay(boolean value);

    /**
     * Set out to value.
     * 
     * @param value
     */
    public void setOut(boolean value);

}