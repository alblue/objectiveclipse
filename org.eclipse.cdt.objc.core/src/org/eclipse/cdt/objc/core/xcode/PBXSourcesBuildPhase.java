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
 * Represents a group of source files which should be compiled. Source build
 * phases do not have extra properties.
 */
public class PBXSourcesBuildPhase extends PBXBuildPhase {

    /**
     * Creates a new instance of a PBXSourcesBuildPhase object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXSourcesBuildPhase(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * br.com.aetheris.objc.internal.core.model.xcode.XCodeProjectItem#describe
     * ()
     */
    @Override
    public String describe() {
        return "Sources"; //$NON-NLS-1$
    }

}
