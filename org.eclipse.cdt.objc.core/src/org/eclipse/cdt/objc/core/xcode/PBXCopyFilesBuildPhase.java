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
 * Represents a build phase where files should be copied to a specific location.
 */
public class PBXCopyFilesBuildPhase extends PBXBuildPhase {

    private static final String dstPath = "dstPath"; //$NON-NLS-1$
    private static final String dstSubfolderSpec = "dstSubfolderSpec"; //$NON-NLS-1$

    /**
     * Creates a new instance of a PBXCopyFilesBuildPhase object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXCopyFilesBuildPhase(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return "CopyFiles"; //$NON-NLS-1$
    }

    /**
     * Returns the destination path for the files to be copied.
     * 
     * @return The destination path for the files to be copied.
     */
    public String getDstPath() {
        return get(dstPath).toString();
    }

    /**
     * Returns the subfolder specification for the destination path.
     * 
     * @return The subfolder specification for the destination path.
     */
    public int getDstSubfolderSpec() {
        return (Integer) get(dstSubfolderSpec);
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            put(dstPath, values.get(dstPath).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(dstSubfolderSpec, Integer.valueOf(values.get(dstSubfolderSpec).toString()));
        } catch (Exception e) {
            // Nothing to do now.
        }
        super.load(values, project);
    }

    /**
     * Sets the destination path for the files to be copied.
     * 
     * @param path
     *            The new destination path for the files to be copied.
     */
    public void setDstPath(String path) {
        put(dstPath, path);
    }

    /**
     * Sets the subfolder specification for the destination path.
     * 
     * @param spec
     *            The new subfolder specification for the destination path.
     */
    public void setDstSubfolderSpec(int spec) {
        put(dstSubfolderSpec, spec);
    }
}
