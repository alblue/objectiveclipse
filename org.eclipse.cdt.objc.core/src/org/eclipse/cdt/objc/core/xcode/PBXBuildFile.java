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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a file required to build the XCode project.
 */
public class PBXBuildFile extends XCodeProjectItem {

    private static final String fileRef = "fileRef"; //$NON-NLS-1$
    private static final String settings = "settings"; //$NON-NLS-1$

    /**
     * Creates a new instance of a PBXBuildFile object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXBuildFile(XCodeProjectItem parent, String id) {
        super(parent, id, false);
    }

    @Override
    public String describe() {
        return getFileRef().describe() + " in " + getParent().describe(); //$NON-NLS-1$
    }

    /**
     * Returns the path to this file relative to the root of the project.
     * 
     * @return The path to this file.
     */
    public PBXFileReference getFileRef() {
        return (PBXFileReference) get(fileRef);
    }

    /**
     * Returns a group of settings of this file.
     * 
     * @return A group of settings of this file.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSettings() {
        Map<String, Object> sets = (Map<String, Object>) get(settings);
        if (sets == null) {
            sets = new HashMap<String, Object>();
            put(settings, sets);
        }
        return sets;
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            XCodeProjectItem item = XCodeProjectItem.build(this, values.get(fileRef).toString(), project);
            if (item != null && item instanceof PBXFileReference) {
                put(fileRef, item);
            }
        } catch (Exception e) {
            // Nothing to do now.
        }
        // settings - WHAT TO DO?
    }

    /**
     * Sets the path to this file to the given path.
     * 
     * @param ref
     *            The new path to this file.
     */
    public void setFileRef(PBXFileReference ref) {
        put(fileRef, ref);
    }

}
