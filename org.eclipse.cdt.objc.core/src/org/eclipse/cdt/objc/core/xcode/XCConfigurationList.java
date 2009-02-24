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
 * Represents a list of XCode project configurations.
 */
public class XCConfigurationList extends XCodeProjectItem {

    private static final String buildConfigurations = "buildConfigurations"; //$NON-NLS-1$
    private static final String defaultConfigurationIsVisible = "defaultConfigurationIsVisible"; //$NON-NLS-1$
    private static final String defaultConfigurationName = "defaultConfigurationName"; //$NON-NLS-1$

    /**
     * Creates a new instance of a XCConfigurationList object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public XCConfigurationList(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        XCodeProjectItem parent = getParent();
        String name = "?"; //$NON-NLS-1$
        if (parent instanceof PBXProject) {
            XCodeProjectItem item2 = ((PBXProject) parent).getMainGroup();
            name = ((PBXGroup) item2).getName();
        } else {
            name = parent.describe();
        }
        return String.format("Build configuration list for %s \"%s\"", parent //$NON-NLS-1$
                .getClass().getSimpleName(), name);
    }

    /**
     * Returns the list of build configurations in this object.
     * 
     * @return The list of build configurations in this object.
     */
    @SuppressWarnings("unchecked")
    public List<XCBuildConfiguration> getBuildConfigurations() {
        List<XCBuildConfiguration> configList = (List<XCBuildConfiguration>) get(buildConfigurations);
        if (configList == null) {
            configList = new ArrayList<XCBuildConfiguration>();
            put(buildConfigurations, configList);
        }
        return configList;
    }

    /**
     * Returns whether the default configurations is visible.
     * 
     * @return <code>true</code> if the default configuration is visible;
     *         otherwise, returns <code>false</code>.
     */
    public boolean getDefaultConfigurationIsVisible() {
        Boolean value = (Boolean) get(defaultConfigurationIsVisible);
        return value != null && value.booleanValue();
    }

    /**
     * Returns the name of the default configuration.
     * 
     * @return The name of the default configuration.
     */
    public String getDefaultConfigurationName() {
        return get(defaultConfigurationName).toString();
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            put(defaultConfigurationName, values.get(defaultConfigurationName).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(defaultConfigurationIsVisible, values.get(defaultConfigurationIsVisible).equals("1")); //$NON-NLS-1$
        } catch (Exception e) {
            // Nothing to do now.
        }
        // buildConfigurations
        try {
            String[] builds = (String[]) values.get(buildConfigurations);
            List<XCBuildConfiguration> buildList = new ArrayList<XCBuildConfiguration>();
            for (String build : builds) {
                XCodeProjectItem item = XCodeProjectItem.build(this, build, project);
                if (item != null && item instanceof XCBuildConfiguration) {
                    buildList.add((XCBuildConfiguration) item);
                }
            }
            put(buildConfigurations, buildList);
        } catch (Exception e) {
            // Nothing to do now.
        }
    }

    /**
     * Sets whether the default configuration should be visible.
     * 
     * @param value
     *            Whether the default configuration should be visible
     */
    public void setDefaultConfigurationIsVisible(boolean value) {
        put(defaultConfigurationIsVisible, value);
    }

    /**
     * Sets the name of the default configuration.
     * 
     * @param name
     *            The new name of the default configuration
     */
    public void setDefaultConfigurationName(String name) {
        put(defaultConfigurationName, name);
    }

}
