/*******************************************************************************
 * Copyright (c) 2009, Leonardo Pessoa and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Leonardo Pessoa - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.xcode;

import java.util.Map;

/**
 * Represents a file or a group.
 */
public abstract class PBXReference extends XCodeProjectItem {

    public interface SourceTrees {

        String ABSOLUTE = "<absolute>"; //$NON-NLS-1$
        String BUILT_PRODUCTS_DIR = "BUILT_PRODUCTS_DIR"; //$NON-NLS-1$
        String GROUP = "<group>"; //$NON-NLS-1$
    }

    private static final String name = "name"; //$NON-NLS-1$

    private static final String sourceTree = "sourceTree"; //$NON-NLS-1$

    /**
     * Creates a new instance of a PBXReference object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     * @param breakLines
     *            Whether the contents of this object should break lines between
     *            properties or be layed on a single line.
     */
    protected PBXReference(XCodeProjectItem parent, String id, boolean breakLines) {
        super(parent, id, breakLines);
    }

    /**
     * Returns the name of this reference object.
     * 
     * @return The name of this reference object.
     */
    public String getName() {
        try {
            return get(name).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the ID of this source tree.
     * 
     * @return The ID of this source tree.
     */
    public String getSourceTree() {
        return get(sourceTree).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * br.com.aetheris.objc.internal.core.model.xcode.XCodeProjectItem#load(
     * java.util.Map, java.util.Map)
     */
    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            put(name, values.get(name).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(sourceTree, values.get(sourceTree).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
    }

    /**
     * Sets the name of this reference object.
     * 
     * @param name
     *            The new name of this reference object
     */
    public void setName(String name) {
        put(PBXReference.name, name);
    }

    /**
     * Sets the ID of this source tree.
     * 
     * @param treeId
     *            The new ID of this source tree
     */
    public void setSourceTree(String treeId) {
        put(sourceTree, treeId);
    }

}
