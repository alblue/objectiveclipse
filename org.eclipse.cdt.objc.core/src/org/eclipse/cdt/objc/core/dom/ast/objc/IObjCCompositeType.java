/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Doug Schaefer (IBM) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IField;

/**
 * Represents a ObjC class, category, or protocol
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjCCompositeType extends ICompositeType, IBinding {
    public static final IObjCCompositeType[] EMPTY_CLASS_ARRAY = new IObjCCompositeType[0];

    public static final int k_category = IObjCASTCompositeTypeSpecifier.k_category;
    public static final int k_class = IObjCASTCompositeTypeSpecifier.k_class;
    public static final int k_protocol = IObjCASTCompositeTypeSpecifier.k_protocol;

    /**
     * findField is restated here to point out that this method looks through
     * the inheritance tree of this class while looking for a field with the
     * given name If no field is found, null is returned, if the name is found
     * to be ambiguous a IProblemBinding is returned.
     * 
     * @param name
     */
    public IField findField(String name) throws DOMException;

    /**
     * Returns a list of ICPPMethod objects representing all method explicitly
     * declared by this class and inherited from base classes.
     * 
     * @return List of ICPPMethod
     */
    public IObjCMethod[] getAllMethods() throws DOMException;

    /**
     * Returns a list of base class relationships. The list is empty if there
     * are none.
     * 
     * @return List of ICPPBase
     */
    public IObjCBase getBaseClass() throws DOMException;

    /**
     * return an array of bindings for those caegories declared for this class.
     * 
     * @throws DOMException
     */
    public IBinding[] getCategories() throws DOMException;

    /**
     * Returns a list of ICPPField objects representing fields declared in this
     * class. It does not include fields inherited from base classes.
     * 
     * @return List of ICPPField
     */
    public IObjCField[] getDeclaredFields() throws DOMException;

    /**
     * Get fields is restated here just to point out that this method returns a
     * list of ICPPField objects representing all fields, declared or inherited.
     */
    public IField[] getFields() throws DOMException;

    /**
     * Returns a list of ICPPMethod objects representing all methods explicitly
     * declared by this class. It does not include inherited methods or
     * automatically generated methods.
     * 
     * @return List of ICPPMethod
     */
    public IObjCMethod[] getMethods() throws DOMException;

    /**
     * Returns a list of base class relationships. The list is empty if there
     * are none.
     * 
     * @return List of ICPPBase
     */
    public IObjCBase[] getProtocols() throws DOMException;

}
