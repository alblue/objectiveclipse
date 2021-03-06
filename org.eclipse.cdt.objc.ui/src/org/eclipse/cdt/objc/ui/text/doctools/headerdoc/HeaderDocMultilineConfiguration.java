/*******************************************************************************
 * Copyright (c) 2008 Symbian Software Systems and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Ferguson (Symbian) - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.ui.text.doctools.headerdoc;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.doctools.IDocCommentViewerConfiguration;
import org.eclipse.cdt.ui.text.doctools.generic.AbstractGenericTagDocCommentViewerConfiguration;
import org.eclipse.cdt.ui.text.doctools.generic.GenericDocTag;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * {@link IDocCommentViewerConfiguration} implementation for HeaderDoc
 * multi-line documentation comments.
 * <em>This class may be sub-classed by clients</em>
 * 
 * @since 5.0
 */
public class HeaderDocMultilineConfiguration extends AbstractGenericTagDocCommentViewerConfiguration {
    /**
     * Default constructor
     */
    public HeaderDocMultilineConfiguration() {
        super(HeaderDocHelper.getHeaderDocTags(), new char[] { '@', '\\' },
                HeaderDocHelper.HEADERDOC_MULTI_TOKEN, HeaderDocHelper.HEADERDOC_TAG_RECOGNIZED);
    }

    /**
     * Constructor intended for use by sub-classes.
     * 
     * @param tags
     *            a non-null array of tags this configuration should recognize
     * @param tagMarkers
     *            a non-null array of characters used to prefix the tags (e.g. @
     *            or \)
     * @param defaultToken
     *            the default scanner token id
     * @param tagToken
     *            the scanner token to use to mark used by this configuration
     * @see AbstractGenericTagDocCommentViewerConfiguration
     */
    protected HeaderDocMultilineConfiguration(GenericDocTag[] tags, char[] tagMarkers, String defaultToken,
            String tagToken) {
        super(tags, tagMarkers, defaultToken, tagToken);
    }

    /*
     * @seeorg.eclipse.cdt.ui.text.doctools.IDocCommentViewerConfiguration#
     * createAutoEditStrategy()
     */
    public IAutoEditStrategy createAutoEditStrategy() {
        return new HeaderDocMultilineAutoEditStrategy();
    }

    /*
     * @seeorg.eclipse.cdt.ui.text.doctools.IDocCommentViewerConfiguration#
     * isDocumentationComment(org.eclipse.jface.text.IDocument, int, int)
     */
    public boolean isDocumentationComment(IDocument doc, int offset, int length) {
        try {
            if (offset + 2 < doc.getLength()) {
                char c = doc.getChar(offset + 2);
                return c == '!';
            }
        } catch (BadLocationException ble) {
            CUIPlugin.log(ble);
        }
        return false;
    }
}
