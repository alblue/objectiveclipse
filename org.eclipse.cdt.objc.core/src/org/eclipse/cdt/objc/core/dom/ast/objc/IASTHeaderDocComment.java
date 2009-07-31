/*******************************************************************************
 * Copyright (c) 2009 Alex Blewitt and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Alex Blewitt - Initial API and Implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.dom.ast.objc;

import org.eclipse.cdt.core.dom.ast.IASTComment;

/**
 * This represents a block comment, but in which additional information as
 * parsed by HeaderDoc may be reconstituted
 * 
 * @see <a
 *      href="http://developer.apple.com/darwin/projects/headerdoc/">http://developer.apple.com/darwin/projects/headerdoc/</a>
 * @since 0.3
 */
public interface IASTHeaderDocComment extends IASTComment {

    /**
     * Returns the string value of the tag, or <code>null</code> if not present.
     * If the value is present but has no string value, an empty string will be
     * returned.
     * 
     * @param tag
     *            the name of the tag (like <code>class</code>,
     *            <code>function</code>, <code>param</code> etc.)
     * @return the string value of the tag, or <code>null</code> if not present.
     */
    public String getValue(String tag);

}
