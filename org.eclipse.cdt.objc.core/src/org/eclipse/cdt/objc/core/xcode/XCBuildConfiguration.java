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
 * Represents a group of build configuration settings for a target.
 */
public class XCBuildConfiguration extends XCodeProjectItem {

    public interface Settings {

        String ARCHS = "ARCHS"; //$NON-NLS-1$
        String COPY_PHASE_STRIP = "COPY_PHASE_STRIP"; //$NON-NLS-1$
        String DEBUG_INFORMATION_FORMAT = "DEBUG_INFORMATION_FORMAT"; //$NON-NLS-1$
        String GCC_DYNAMIC_NO_PIC = "GCC_DYNAMIC_NO_PIC"; //$NON-NLS-1$
        String GCC_ENABLE_FIX_AND_CONTINUE = "GCC_ENABLE_FIX_AND_CONTINUE"; //$NON-NLS-1$
        String GCC_MODEL_TUNING = "GCC_MODEL_TUNING"; //$NON-NLS-1$
        String GCC_OPTIMIZATION_LEVEL = "GCC_OPTIMIZATION_LEVEL"; //$NON-NLS-1$
        String GCC_PRECOMPILE_PREFIX_HEADER = "GCC_PRECOMPILE_PREFIX_HEADER"; //$NON-NLS-1$
        String GCC_PREFIX_HEADER = "GCC_PREFIX_HEADER"; //$NON-NLS-1$
        String GCC_WARN_ABOUT_RETURN_TYPE = "GCC_WARN_ABOUT_RETURN_TYPE"; //$NON-NLS-1$
        String GCC_WARN_UNUSED_VARIABLE = "GCC_WARN_UNUSED_VARIABLE"; //$NON-NLS-1$
        String INSTALL_PATH = "INSTALL_PATH"; //$NON-NLS-1$
        String PREBINDING = "PREBINDING"; //$NON-NLS-1$
        String PRODUCT_NAME = "PRODUCT_NAME"; //$NON-NLS-1$
        String SDKROOT = "SDKROOT"; //$NON-NLS-1$
        String ZERO_LINK = "ZERO_LINK"; //$NON-NLS-1$
    }

    private static final String buildSettings = "buildSettings"; //$NON-NLS-1$

    private static final String name = "name"; //$NON-NLS-1$

    /**
     * Creates a new instance of a XCBuildConfiguration object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The id of this object
     */
    public XCBuildConfiguration(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return getName();
    }

    /**
     * Returns a map of settings for this build configuration.
     * 
     * @return A map of settings for this build configuration.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBuildSettings() {
        Map<String, Object> settings = (Map<String, Object>) get(buildSettings);
        if (settings == null) {
            settings = new HashMap<String, Object>();
            put(buildSettings, settings);
        }
        return settings;
    }

    /**
     * Returns the name of this build configuration.
     * 
     * @return The name of this build configuration.
     */
    public String getName() {
        return get(name).toString();
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
            put(buildSettings, values.get(buildSettings));
        } catch (Exception e) {
            // Nothing to do now.
        }
    }

    /**
     * Sets the name of this build configuration.
     * 
     * @param name
     *            The new name of this build configuration
     */
    public void setName(String name) {
        put(XCBuildConfiguration.name, name);
    }

}
