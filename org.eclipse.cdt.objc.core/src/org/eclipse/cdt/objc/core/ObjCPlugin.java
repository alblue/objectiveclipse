/*******************************************************************************
 * Copyright (c) 2009, Alex Blewitt and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Alex Blewitt - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ObjCPlugin extends Plugin {

    // The shared instance
    private static ObjCPlugin plugin;

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.cdt.objc.core"; //$NON-NLS-1$

    private static ResourceBundle resourceBundle;

    static {
        try {
            resourceBundle = ResourceBundle
                    .getBundle("org.eclipse.cdt.objc.core.internal.ObjCPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ObjCPlugin getDefault() {
        return plugin;
    }

    public static String getResourceString(String string) {
        if (resourceBundle == null) {
            return string;
        } else {
            return resourceBundle.getString(string);
        }
    }

    /**
     * The constructor
     */
    public ObjCPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
}
