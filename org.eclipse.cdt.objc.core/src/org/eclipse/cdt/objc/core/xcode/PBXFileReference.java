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
 * Represents a file reference in a XCode project.
 */
public class PBXFileReference extends PBXReference {

    /**
     * Contains a list of known XCode 3.x file types. These file type are found
     * on the <code>lastKnownFileType</code> or <code>explicitFileType</code>
     * properties.
     */
    public interface KnownTypes {

        String EXECUTABLE = "compiled.mach-o.executable"; //$NON-NLS-1$
        String FRAMEWORK = "wrapper.framework"; //$NON-NLS-1$
        String MAN = "text.man"; //$NON-NLS-1$
        String OBJC_HEADER = "sourcecode.c.h"; //$NON-NLS-1$
        String OBJC_SOURCE = "sourcecode.c.objc"; //$NON-NLS-1$
    }

    private static final String explicitFileType = "explicitFileType"; //$NON-NLS-1$
    private static final String fileEncoding = "fileEncoding"; //$NON-NLS-1$
    private static final String includedInIndex = "includedInIndex"; //$NON-NLS-1$
    private static final String lastKnownFileType = "lastKnownFileType"; //$NON-NLS-1$

    private static final String path = "path"; //$NON-NLS-1$

    /**
     * Creates a new PBXFileReference object.
     * 
     * @param parent
     *            The parent of this object
     * @param id
     *            The ID of this object
     */
    public PBXFileReference(XCodeProjectItem parent, String id) {
        super(parent, id, false);
    }

    @Override
    public String describe() {
        if (getName() != null) {
            return getName();
        }
        return getPath();
    }

    /**
     * Returns the explicit file type of the referenced file.
     * 
     * @return The explicit file type of the referenced file.
     */
    public String getExplicitFileType() {
        return get(explicitFileType).toString();
    }

    /**
     * Returns the encoding of the referenced file.
     * 
     * @return The encoding of the referenced file.
     */
    public int getFileEncoding() {
        return (Integer) get(fileEncoding);
    }

    /**
     * Returns whether this reference should be included in the index.
     * 
     * @return <code>true</code> if this reference should be included in the
     *         index; otherwise, returns <code>false</code>.
     */
    public boolean getIncludedInIndex() {
        Boolean value = (Boolean) get(includedInIndex);
        return value == null || value.booleanValue();
    }

    /**
     * Returns the last known type of the referenced file.
     * 
     * @return The last known type of the referenced file.
     */
    public String getLastKnownFileType() {
        return get(lastKnownFileType).toString();
    }

    /**
     * Returns the path to the referenced file.
     * 
     * @return The path to the referenced file.
     */
    public String getPath() {
        return get(path).toString();
    }

    @Override
    protected void load(Map<String, Object> values, XCodeProject project) {
        try {
            put(fileEncoding, Integer.valueOf(values.get(fileEncoding).toString()));
        } catch (Exception e) {
            // Nothing do to now.
        }
        try {
            put(lastKnownFileType, values.get(lastKnownFileType).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(path, values.get(path).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(explicitFileType, values.get(explicitFileType).toString());
        } catch (Exception e) {
            // Nothing to do now.
        }
        try {
            put(includedInIndex, values.get(includedInIndex).equals("1")); //$NON-NLS-1$
        } catch (Exception e) {
            // Nothing do to now.
        }
        super.load(values, project);
    }

    /**
     * Sets the explicit file type of the referenced file. By setting the file
     * type as explicit, the <code>lastKnownFileType</code> property is removed.
     * 
     * @param fileType
     *            The new explicit file type of the referenced file
     */
    public void setExplicitFileType(String fileType) {
        put(explicitFileType, fileType);
        put(lastKnownFileType, null);
    }

    /**
     * Sets the encoding of the referenced file.
     * 
     * @param encoding
     *            The new encoding of the referenced file.
     */
    public void setFileEncoding(int encoding) {
        put(fileEncoding, encoding);
    }

    /**
     * Sets whether this reference should be included in the index.
     * 
     * @param value
     *            Whether this reference should be included in the index.
     */
    public void setIncludedInIndex(boolean value) {
        if (!value) {
            put(includedInIndex, false);
        } else {
            put(includedInIndex, null);
        }
    }

    /**
     * Sets the last known type of the referenced file. By setting the file type
     * as the last known, the <code>explicitFileType</code> property is removed.
     * 
     * @param fileType
     *            The new known type of the referenced file.
     */
    public void setLastKnownFileType(String fileType) {
        put(lastKnownFileType, fileType);
        put(explicitFileType, null);
    }

    /**
     * Sets the path to the referenced file.
     * 
     * @param path
     *            The new path to the referenced file
     */
    public void setPath(String path) {
        put(PBXFileReference.path, path);
    }
}
