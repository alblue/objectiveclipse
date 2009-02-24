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
 * Represents the root object of a XCode 3.x project.
 */
public class PBXProject extends XCodeProjectItem {

    private static final String buildConfigurationList = "buildConfigurationList"; //$NON-NLS-1$
    private static final String compatibilityVersion = "compatibilityVersion"; //$NON-NLS-1$
    private static final String hasScannedForEncodings = "hasScannedForEncodings"; //$NON-NLS-1$
    private static final String mainGroup = "mainGroup"; //$NON-NLS-1$
    private static final String projectDirPath = "projectDirPath"; //$NON-NLS-1$
    private static final String projectRoot = "projectRoot"; //$NON-NLS-1$
    private static final String targets = "targets"; //$NON-NLS-1$

    private XCodeProject project;

    /**
     * Creates a new instance of a PBXProject object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXProject(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return "Project object"; //$NON-NLS-1$
    }

    /**
     * Returns the build configuration list of this project.
     * 
     * @return The build configuration list of this project.
     */
    public XCConfigurationList getBuildConfigurationList() {
        return (XCConfigurationList) get(buildConfigurationList);
    }

    /**
     * Returns the compatibility version string of this project.
     * 
     * @return The compatibility version string of this project.
     */
    public String getCompatibilityVersion() {
        return get(compatibilityVersion).toString();
    }

    /**
     * Returns whether this project has been scanned for encodings.
     * 
     * @return <code>true</code> if this project has been scanned for encodings;
     *         otherwise, returns <code>false</code>.
     */
    public boolean getHasScannedForEncodings() {
        return (Boolean) get(hasScannedForEncodings);
    }

    /**
     * Returns the main group of this project.
     * 
     * @return The main group of this project.
     */
    public PBXGroup getMainGroup() {
        return (PBXGroup) get(mainGroup);
    }

    @Override
    public XCodeProject getProject() {
        return project;
    }

    /**
     * Returns the path of this project if not the same where the
     * <code>.xcodeproj</code> bundle is located.
     * 
     * @return The path of this project.
     */
    public String getProjectDirPath() {
        return get(projectDirPath).toString();
    }

    /**
     * Returns the root of this project.
     * 
     * @return The root of this project.
     */
    public String getProjectRoot() {
        return get(projectRoot).toString();
    }

    /**
     * Returns a list of targets of this project.
     * 
     * @return A list of targets of this project.
     */
    @SuppressWarnings("unchecked")
    public List<PBXNativeTarget> getTargets() {
        List<PBXNativeTarget> targetList = (List<PBXNativeTarget>) get(targets);
        if (targetList == null) {
            targetList = new ArrayList<PBXNativeTarget>();
            put(targets, targetList);
        }
        return targetList;
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        this.project = project;
        if (values != null) {
            // Copy simple values
            try {
                put(compatibilityVersion, values.get(compatibilityVersion).toString());
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                put(hasScannedForEncodings, values.get(hasScannedForEncodings).equals("1")); //$NON-NLS-1$
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                put(projectDirPath, values.get(projectDirPath).toString());
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                put(projectRoot, values.get(projectRoot).toString());
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                XCodeProjectItem item = XCodeProjectItem.build(this, values.get(buildConfigurationList)
                        .toString(), project);
                if (item != null && item instanceof XCConfigurationList) {
                    put(buildConfigurationList, item);
                }
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                XCodeProjectItem item = XCodeProjectItem.build(this, values.get(mainGroup).toString(),
                        project);
                if (item != null && item instanceof PBXGroup) {
                    put(mainGroup, item);
                }
            } catch (Exception e) {
                // Nothing to do now.
            }
            try {
                // targets
                String[] targs = (String[]) values.get(targets);
                List<PBXNativeTarget> targetList = new ArrayList<PBXNativeTarget>();
                for (String target : targs) {
                    XCodeProjectItem item = XCodeProjectItem.build(this, target, project);
                    if (item != null && item instanceof PBXNativeTarget) {
                        targetList.add((PBXNativeTarget) item);
                    }
                }
                put(targets, targetList);
            } catch (Exception e) {
                // Nothing to do now.
            }
        }
    }

    /**
     * Sets the build configuration list of this project.
     * 
     * @param list
     *            The new build configuration list of this project
     */
    public void setBuildConfigurationList(XCConfigurationList list) {
        put(buildConfigurationList, list);
    }

    /**
     * Sets the compatibility version string of this project.
     * 
     * @param version
     *            The new compatibility version string of this project
     */
    public void setCompatibilityVersion(String version) {
        put(compatibilityVersion, version);
    }

    /**
     * Sets whether this project has been scanned for encodings.
     * 
     * @param value
     *            Whether this project has been scanned for encodings
     */
    public void setHasScannedForEncodings(boolean value) {
        put(hasScannedForEncodings, value);
    }

    /**
     * Sets the main group of this project.
     * 
     * @param group
     *            The new main group of this project
     */
    public void setMainGroup(PBXGroup group) {
        put(mainGroup, group);
    }

    /**
     * Sets the path of this project if not the same where the
     * <code>.xcodeproj</code> bundle is located.
     * 
     * @param path
     *            The new path of this project.
     */
    public void setProjectDirPath(String path) {
        put(projectDirPath, path);
    }

    /**
     * Sets the root of this project.
     * 
     * @param path
     *            The new root of this project
     */
    public void setProjectRoot(String path) {
        put(projectRoot, path);
    }

}
