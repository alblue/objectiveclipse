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
 * Represents a native target of a XCode project. A target is a compilation
 * product of the project.
 */
public class PBXNativeTarget extends XCodeProjectItem {

    private static final String buildConfigurationList = "buildConfigurationList"; //$NON-NLS-1$
    private static final String buildPhases = "buildPhases"; //$NON-NLS-1$
    private static final String buildRules = "buildRules"; //$NON-NLS-1$
    private static final String dependencies = "dependencies"; //$NON-NLS-1$
    private static final String name = "name"; //$NON-NLS-1$
    private static final String productInstallPath = "productInstallPath"; //$NON-NLS-1$
    private static final String productName = "productName"; //$NON-NLS-1$
    private static final String productReference = "productReference"; //$NON-NLS-1$
    private static final String productType = "productType"; //$NON-NLS-1$

    /**
     * Creates a new PBXNativeTarget object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object.
     */
    public PBXNativeTarget(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return getName();
    }

    /**
     * Returns the build configuration list of this target.
     * 
     * @return The build configuration list of this target.
     */
    public XCConfigurationList getBuildConfigurationList() {
        return (XCConfigurationList) get(buildConfigurationList);
    }

    /**
     * Returns the list of build phases of this target.
     * 
     * @return The list of build phases of this target.
     */
    @SuppressWarnings("unchecked")
    public List<PBXBuildPhase> getBuildPhases() {
        List<PBXBuildPhase> phaseList = (List<PBXBuildPhase>) get(buildPhases);
        if (phaseList == null) {
            phaseList = new ArrayList<PBXBuildPhase>();
            put(buildPhases, phaseList);
        }
        return phaseList;
    }

    /**
     * Returns the list of build rules for this target.
     * 
     * @return The list of build rules for this target.
     */
    @SuppressWarnings("unchecked")
    public List<String> getBuildRules() {
        List<String> ruleList = (List<String>) get(buildRules);
        if (ruleList == null) {
            ruleList = new ArrayList<String>();
            put(buildRules, ruleList);
        }
        return ruleList;
    }

    /**
     * Returns the list of dependencies of this target.
     * 
     * @return The list of dependencies of this target.
     */
    @SuppressWarnings("unchecked")
    public List<String> getDependencies() {
        List<String> depList = (List<String>) get(dependencies);
        if (depList == null) {
            depList = new ArrayList<String>();
            put(dependencies, depList);
        }
        return depList;
    }

    /**
     * Returns the name of this native target.
     * 
     * @return The name of this native target.
     */
    public String getName() {
        return get(name).toString();
    }

    /**
     * Returns the path where this product should be installed to.
     * 
     * @return The path where this product should be installed to.
     */
    public String getProductInstallPath() {
        return get(productInstallPath).toString();
    }

    /**
     * Returns the product name.
     * 
     * @return The product name.
     */
    public String getProductName() {
        return get(productName).toString();
    }

    /**
     * Returns the product file reference for this target.
     * 
     * @return The product file reference for this target.
     */
    public PBXFileReference getProductReference() {
        return (PBXFileReference) get(productReference);
    }

    /**
     * Returns the product type ID of this target.
     * 
     * @return The product type ID of this target.
     */
    public String getProductType() {
        return get(productType).toString();
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        // buildConfigurationList
        try {
            XCodeProjectItem item = XCodeProjectItem.build(this, values.get(buildConfigurationList)
                    .toString(), project);
            if (item != null && item instanceof XCConfigurationList) {
                put(buildConfigurationList, item);
            }
        } catch (Exception e) {
            // Nothing to do now.
        }
        // buildPhases
        try {
            String[] builds = (String[]) values.get(buildPhases);
            List<PBXBuildPhase> buildList = new ArrayList<PBXBuildPhase>();
            for (String build : builds) {
                XCodeProjectItem item = XCodeProjectItem.build(this, build, project);
                if (item != null && item instanceof PBXBuildPhase) {
                    buildList.add((PBXBuildPhase) item);
                }
            }
            put(buildPhases, buildList);
        } catch (Exception e) {
            // Nothing to do now.
        }
        // buildRules - NO REFERENCES
        // dependencies - NO REFERENCES
        try {
            XCodeProjectItem item = project.getItemById(values.get(productReference).toString());
            if (item != null && item instanceof PBXFileReference) {
                put(productReference, item);
            }
        } catch (Exception e) {
            // Nothing to do now.
        }
        // Other simple values
        try {
            put(name, values.get(name).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(productInstallPath, values.get(productInstallPath).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(productName, values.get(productName).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(productType, values.get(productType).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
    }

    /**
     * Sets the build configuration list of this target.
     * 
     * @param list
     *            The new build configuration list of this target
     */
    public void setBuildConfigurationList(XCConfigurationList list) {
        put(buildConfigurationList, list);
    }

    /**
     * Sets the name of this native target.
     * 
     * @param name
     *            The new name of this native target
     */
    public void setName(String name) {
        put(PBXNativeTarget.name, name);
    }

    /**
     * Sets the path where this product should be installed to.
     * 
     * @param path
     *            The new path where this product should be installed to
     */
    public void setProductInstallPath(String path) {
        put(productInstallPath, path);
    }

    /**
     * Sets the product name.
     * 
     * @param name
     *            The new product name
     */
    public void setProductName(String name) {
        put(productName, name);
    }

    /**
     * Sets the product file reference for this target.
     * 
     * @param ref
     *            The new product file reference for this target
     */
    public void setProductReference(PBXFileReference ref) {
        put(productReference, ref);
    }

    /**
     * Sets the product type ID of this target.
     * 
     * @param type
     *            The new product type ID of this target
     */
    public void setProductType(String type) {
        put(productType, type);
    }
}
