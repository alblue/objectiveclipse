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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstracts some properties of every build phase possible in a XCode 3.x
 * project file. Final phases can contain other properties.
 * 
 * @see PBXCopyFilesBuildPhase
 * @see PBXFrameworksBuildPhase
 * @see PBXSourcesBuildPhase
 */
public abstract class PBXBuildPhase extends XCodeProjectItem {

    private static final String buildActionMask = "buildActionMask"; //$NON-NLS-1$
    private static final String files = "files"; //$NON-NLS-1$
    private static final String runOnlyForDeploymentPostprocessing = "runOnlyForDeploymentPostprocessing"; //$NON-NLS-1$

    /**
     * Creates a new instance of a PBXBuildPhase.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     * @param breakLines
     *            Whether the contents of this object should break lines between
     *            each property or be layed on a single line.
     */
    public PBXBuildPhase(XCodeProjectItem parent, String id, boolean breakLines) {
        super(parent, id, breakLines);
    }

    /**
     * Returns the build action mask for this build phase.
     * 
     * @return The build action mask for this build phase.
     */
    public int getBuildActionMask() {
        return (Integer) get(buildActionMask);
    }

    /**
     * Returns the list of files referenced by this build phase.
     * 
     * @return The list of files referenced by this build phase.
     */
    @SuppressWarnings("unchecked")
    public List<PBXBuildFile> getFiles() {
        List<PBXBuildFile> fileList = (List<PBXBuildFile>) get(files);
        if (fileList == null) {
            fileList = new ArrayList<PBXBuildFile>();
            put(files, fileList);
        }
        return fileList;
    }

    /**
     * Returns whether this build phase runs only for deployment
     * post-processing.
     * 
     * @return <code>true</code> if this phase runs only for deployment
     *         post-processing; otherwise, returns <code>false</code>.
     */
    public boolean getRunOnlyForDeploymentPostprocessing() {
        return (Boolean) get(runOnlyForDeploymentPostprocessing);
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            put(buildActionMask, Integer.valueOf(values.get(buildActionMask).toString()));
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(runOnlyForDeploymentPostprocessing, values.get(runOnlyForDeploymentPostprocessing)
                    .equals("1")); //$NON-NLS-1$
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            String[] fileArray = (String[]) values.get(files);
            List<PBXBuildFile> fileList = new ArrayList<PBXBuildFile>();
            for (String file : fileArray) {
                XCodeProjectItem item = project.getItemById(file);
                if (item != null && item instanceof PBXBuildFile) {
                    fileList.add((PBXBuildFile) item);
                }
            }
            put(files, fileList);
        } catch (Exception e) {
            // Nothing to do now.
        }
    }

    /**
     * Sets the build action mask for this build phase.
     * 
     * @param mask
     *            The new build action mask for this build phase
     */
    public void setBuildActionMask(int mask) {
        put(buildActionMask, mask);
    }

    /**
     * Sets whether this build phase runs only for deployment post-processing.
     * 
     * @param value
     *            Whether this build phase runs only for deployment
     *            post-processing
     */
    public void setRunOnlyForDeploymentPostprocessing(boolean value) {
        put(runOnlyForDeploymentPostprocessing, value);
    }

}
