package org.eclipse.cdt.objc.core.internal.parser.scanner;

/**
 * Interface between location map and preprocessor for modeling contexts that
 * can deal with offsets. These are: synthetic contexts used for pre-included
 * files, file-contexts, macro-expansions.
 * 
 * @since 5.0
 */
public interface ILocationCtx {

    /**
     * If this is a file context the filename of this context is returned,
     * otherwise the filename of the first enclosing context that is a file
     * context is returned.
     */
    String getFilePath();

    /**
     * Returns the enclosing context or <code>null</code> if this is the
     * translation unit context.
     */
    ILocationCtx getParent();

}
