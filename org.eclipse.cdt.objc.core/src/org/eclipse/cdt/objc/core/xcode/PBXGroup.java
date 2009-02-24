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
 * Represents a group in a XCode 3.x project.
 */
public class PBXGroup extends PBXReference {

    private static final String children = "children"; //$NON-NLS-1$

    /**
     * Creates a new PBXGroup object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXGroup(XCodeProjectItem parent, String id) {
        super(parent, id, true);
    }

    @Override
    public String describe() {
        return getName();
    }

    /**
     * Returns the children of this group. A child can be another
     * <code>PBXGroup</code> or a <code>PBXFileReference</code>.
     * 
     * @return The children of this group.
     */
    @SuppressWarnings("unchecked")
    public List<PBXReference> getChildren() {
        List<PBXReference> childList = (List<PBXReference>) get(children);
        if (childList == null) {
            childList = new ArrayList<PBXReference>();
            put(children, childList);
        }
        return childList;
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            String[] childs = (String[]) values.get(children);
            List<PBXReference> childList = new ArrayList<PBXReference>();
            for (String child : childs) {
                XCodeProjectItem item = XCodeProjectItem.build(this, child, project);
                if (item != null && item instanceof PBXReference) {
                    childList.add((PBXReference) item);
                }
            }
            put(children, childList);
        } catch (Exception e) {
            // Nothing to do now.
        }
        super.load(values, project);
    }
}
