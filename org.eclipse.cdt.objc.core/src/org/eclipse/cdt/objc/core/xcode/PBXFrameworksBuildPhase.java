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

/**
 * Represents the phase where the executable is listed to the selected
 * frameworks. Framework build phases do not have extra properties.
 */
public class PBXFrameworksBuildPhase extends PBXBuildPhase {

    /**
     * Creates a new instance of a PBXFrameworksBuildPhase.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXFrameworksBuildPhase(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return "Frameworks"; //$NON-NLS-1$
    }

}
