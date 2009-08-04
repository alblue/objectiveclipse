/*******************************************************************************
 * Copyright (c) 2008, 2009 Symbian Software Systems and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Ferguson (Symbian) - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.ui.text.doctools.headerdoc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.doctools.generic.GenericDocTag;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Makes available information for HeaderDoc support.
 * 
 * @since 5.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class HeaderDocHelper extends AbstractPreferenceInitializer {
    private static GenericDocTag[] fTags;
    public static final String HEADERDOC_MULTI_TOKEN = "org.eclipse.cdt.internal.ui.text.doctools.headerdoc.multi"; //$NON-NLS-1$

    public static final String HEADERDOC_SINGLE_TOKEN = "org.eclipse.cdt.internal.ui.text.doctools.headerdoc.single"; //$NON-NLS-1$
    public static final String HEADERDOC_TAG_RECOGNIZED = "org.eclipse.cdt.internal.ui.text.doctools.headerdoc.recognizedTag"; //$NON-NLS-1$

    // TODO Put this file in
    // private static final IPath TAGS_CSV = new Path("headerdocTags.csv"); //$NON-NLS-1$

    /**
     * @return The tags which are understood by default by the HeaderDoc tool.
     */
    public static GenericDocTag[] getHeaderDocTags() {
        if (fTags == null) {
            try {
                List<GenericDocTag> temp = new ArrayList<GenericDocTag>();
                /*
                 * InputStream is =
                 * FileLocator.openStream(CUIPlugin.getDefault().getBundle(),
                 * TAGS_CSV, false); BufferedReader br = new BufferedReader(new
                 * InputStreamReader(is)); StringBuffer content = new
                 * StringBuffer(); for (String line = br.readLine(); line !=
                 * null; line = br.readLine()) { content.append(line + "\n");
                 * //$NON-NLS-1$ } String[] values =
                 * content.toString().split("(\\s)*,(\\s)*"); //$NON-NLS-1$
                 * 
                 * for (int i = 0; i + 1 < values.length; i += 2) { temp.add(new
                 * GenericDocTag(values[i], values[i + 1])); }
                 */
                // TODO Externalise
                temp.add(new GenericDocTag("abstract", "Short description of element")); //$NON-NLS-1$ //$NON-NLS-2$
                temp.add(new GenericDocTag("description", "Long description of element")); //$NON-NLS-1$ //$NON-NLS-2$
                temp.add(new GenericDocTag("class", "Class name")); //$NON-NLS-1$ //$NON-NLS-2$
                fTags = temp.toArray(new GenericDocTag[temp.size()]);
            } catch (Exception ioe) {
                fTags = new GenericDocTag[0];
                CUIPlugin.log(ioe);
            }
        }
        return fTags;
    }

    /*
     * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore cuis = CUIPlugin.getDefault().getPreferenceStore();
        PreferenceConverter.setDefault(cuis, HeaderDocHelper.HEADERDOC_MULTI_TOKEN, new RGB(63, 95, 191));
        PreferenceConverter.setDefault(cuis, HeaderDocHelper.HEADERDOC_SINGLE_TOKEN, new RGB(63, 95, 191));
        PreferenceConverter
                .setDefault(cuis, HeaderDocHelper.HEADERDOC_TAG_RECOGNIZED, new RGB(127, 159, 191));
    }
}
