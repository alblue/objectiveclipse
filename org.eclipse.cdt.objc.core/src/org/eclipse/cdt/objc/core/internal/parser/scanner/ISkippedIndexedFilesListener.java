package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.parser.scanner.IncludeFileContent;

/**
 * Interface to listen for information about files skipped by the preprocessor,
 * because they are found in the index
 */
public interface ISkippedIndexedFilesListener {

    /**
     * Notifies the listeners that an include file has been skipped.
     * 
     * @param offset
     *            offset at which the file is included (see
     *            {@link ASTNode#getOffset()}
     * @param fileContent
     *            information about the skipped file.
     */
    void skippedFile(int offset, IncludeFileContent fileContent);
}
