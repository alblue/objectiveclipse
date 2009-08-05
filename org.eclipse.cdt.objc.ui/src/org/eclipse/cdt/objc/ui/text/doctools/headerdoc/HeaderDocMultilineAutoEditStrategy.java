/*******************************************************************************
 * Copyright (c) 2008, 2009 Symbian Software Systems and others. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Ferguson (Symbian) - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.ui.text.doctools.headerdoc;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMethodDeclarator;
import org.eclipse.cdt.ui.text.ICPartitions;
import org.eclipse.cdt.ui.text.doctools.DefaultMultilineCommentAutoEditStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;

/**
 * {@link IAutoEditStrategy} for adding HeaderDoc tags for comments.
 * 
 * @since 5.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class HeaderDocMultilineAutoEditStrategy extends DefaultMultilineCommentAutoEditStrategy {
    private static final String PARAM = "@param "; //$NON-NLS-1$
    private static final String RETURN = "@return\n"; //$NON-NLS-1$

    protected boolean documentDeclarations = true;
    protected boolean documentPureVirtuals = true;

    public HeaderDocMultilineAutoEditStrategy() {
    }

    /*
     * @see
     * org.eclipse.cdt.ui.text.doctools.DefaultMultilineCommentAutoEditStrategy
     * #customizeAfterNewLineForDeclaration(org.eclipse.jface.text.IDocument,
     * org.eclipse.cdt.core.dom.ast.IASTDeclaration,
     * org.eclipse.jface.text.ITypedRegion)
     */
    @Override
    protected StringBuilder customizeAfterNewLineForDeclaration(IDocument doc, IASTDeclaration dec,
            ITypedRegion partition) {

        while (dec instanceof ICPPASTTemplateDeclaration) {
            dec = ((ICPPASTTemplateDeclaration) dec).getDeclaration();
        }
        // TODO Put in header for class types etc. here
        if (dec instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition fd = (IASTFunctionDefinition) dec;
            return documentFunction(fd.getDeclarator(), fd.getDeclSpecifier());
        }

        if (dec instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration sdec = (IASTSimpleDeclaration) dec;
            StringBuilder result = new StringBuilder();

            if (sdec.getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                return result;
            } else if (sdec.getDeclSpecifier() instanceof ICPPASTDeclSpecifier) {
                IASTDeclarator[] dcs = sdec.getDeclarators();
                if (dcs.length == 1 && dcs[0] instanceof ICPPASTFunctionDeclarator) {
                    ICPPASTFunctionDeclarator fdecl = (ICPPASTFunctionDeclarator) dcs[0];
                    boolean shouldDocument = documentDeclarations
                            || (documentPureVirtuals && fdecl.isPureVirtual());
                    if (shouldDocument) {
                        return documentFunction(fdecl, sdec.getDeclSpecifier());
                    }
                }
            }
        }
        return new StringBuilder();
    }

    /**
     * Copies the indentation of the previous line and adds a star. If the
     * comment just started on this line adds also a blank.
     * 
     * @param doc
     *            the document to work on
     * @param c
     *            the command to deal with
     */
    @Override
    public void customizeDocumentAfterNewLine(IDocument doc, final DocumentCommand c) {
        int offset = c.offset;
        if (offset == -1 || doc.getLength() == 0) {
            return;
        }

        final StringBuilder buf = new StringBuilder(c.text);
        try {
            // find start of line
            IRegion line = doc.getLineInformationOfOffset(c.offset);

            IRegion prefix = findPrefixRange(doc, line);
            String indentation = doc.get(prefix.getOffset(), prefix.getLength());

            if (shouldCloseMultiline(doc, c.offset)) {
                try {
                    doc.replace(c.offset, 0, indentation + " " + MULTILINE_END); // close the comment in order to parse //$NON-NLS-1$

                    // as we are auto-closing, the comment becomes eligible for
                    // auto-doc'ing
                    IASTDeclaration dec = null;
                    IASTTranslationUnit ast = getAST();

                    if (ast != null) {
                        dec = findFollowingDeclaration(ast, offset);
                        if (dec == null) {
                            IASTNodeSelector ans = ast.getNodeSelector(ast.getFilePath());
                            IASTNode node = ans.findEnclosingNode(offset, 0);
                            if (node instanceof IASTDeclaration) {
                                dec = (IASTDeclaration) node;
                            }
                        }
                    }

                    if (dec != null) {
                        ITypedRegion partition = TextUtilities.getPartition(doc,
                                ICPartitions.C_PARTITIONING /* this! */, offset, false);
                        StringBuilder content = customizeAfterNewLineForDeclaration(doc, dec, partition);
                        buf.append(indent(content, indentation + MULTILINE_MID));
                    }

                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                c.shiftsCaret = false;
                c.text = buf.toString();
                int newOffset = offset(c.text, new String[] { "@abstract", "@description" }); //$NON-NLS-1$ //$NON-NLS-2$
                if (newOffset == -1) {
                    newOffset = indentation.length();
                }
                c.caretOffset = c.offset + newOffset;
            } else {
                // buf.append('\n');
                buf.append(indentation);
                if (!indentation.endsWith(MULTILINE_MID)) {
                    buf.append(MULTILINE_MID);
                }
                c.text = buf.toString();
            }

        } catch (BadLocationException excp) {
            // stop work
        }
    }

    /**
     * @param decl
     *            the function declarator to document
     * @param ds
     *            the function specifier to document
     * @return content describing the specified function
     */
    protected StringBuilder documentFunction(IASTFunctionDeclarator decl, IASTDeclSpecifier ds) {
        StringBuilder result = new StringBuilder();

        if (decl instanceof IObjCASTMethodDeclarator) {
            result.append("@method "); //$NON-NLS-1$
        } else {
            result.append("@function "); //$NON-NLS-1$
        }
        result.append(decl.getName());
        result.append("\n"); //$NON-NLS-1$
        result.append("@abstract \n"); //$NON-NLS-1$

        result.append(documentFunctionParameters(getParameterDecls(decl)));

        boolean hasReturn = true;
        if (ds instanceof IASTSimpleDeclSpecifier) {
            IASTSimpleDeclSpecifier sds = (IASTSimpleDeclSpecifier) ds;
            if (sds.getType() == IASTSimpleDeclSpecifier.t_void) {
                hasReturn = false;
            }
        }
        if (hasReturn) {
            result.append(documentFunctionReturn());
        }
        result.append("@description \n"); //$NON-NLS-1$

        return result;
    }

    /**
     * Returns the comment content to add to the documentation comment.
     * 
     * @param decls
     *            The parameter declarations to describe
     * @return a buffer containing the comment content to generate to describe
     *         the parameters of the specified {@link IASTParameterDeclaration}
     *         objects.
     */
    protected StringBuilder documentFunctionParameters(IASTParameterDeclaration[] decls) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < decls.length; i++) {
            if (!isVoidParameter(decls[i])) {
                result.append(PARAM + getParameterName(decls[i]) + "\n"); //$NON-NLS-1$
            }
        }
        return result;
    }

    /**
     * @return the comment content to describe the return
     */
    protected StringBuilder documentFunctionReturn() {
        return new StringBuilder(RETURN);
    }

    /**
     * @param decl
     *            the function declarator to analyze
     * @return the parameter declarations for the specified function definition
     */
    protected IASTParameterDeclaration[] getParameterDecls(IASTFunctionDeclarator decl) {
        IASTParameterDeclaration[] result;
        if (decl instanceof IASTStandardFunctionDeclarator) {
            IASTStandardFunctionDeclarator standardFunctionDecl = (IASTStandardFunctionDeclarator) decl;
            result = standardFunctionDecl.getParameters();
        } else /*
                * if (def instanceof ICASTKnRFunctionDeclarator) {
                * ICASTKnRFunctionDeclarator knrDeclarator=
                * (ICASTKnRFunctionDeclarator)decl; result=
                * knrDeclarator.getParameterDeclarations(); } else
                */{
            result = new IASTParameterDeclaration[0];
        }
        return result;
    }

    /**
     * @param decl
     * @return the name of the parameter
     */
    String getParameterName(IASTParameterDeclaration decl) {
        IASTDeclarator dtor = decl.getDeclarator();
        for (int i = 0; i < 8 && dtor.getName().getRawSignature().length() == 0
                && dtor.getNestedDeclarator() != null; i++) {
            dtor = dtor.getNestedDeclarator();
        }
        return dtor.getName().getRawSignature();
    }

    /**
     * @param decl
     * @return true if the specified parameter declaration is of void type
     */
    boolean isVoidParameter(IASTParameterDeclaration decl) {
        if (decl.getDeclSpecifier() instanceof IASTSimpleDeclSpecifier) {
            if (((IASTSimpleDeclSpecifier) decl.getDeclSpecifier()).getType() == IASTSimpleDeclSpecifier.t_void) {
                IASTDeclarator dtor = decl.getDeclarator();
                if (dtor.getPointerOperators().length == 0) {
                    if (!(dtor instanceof IASTFunctionDeclarator) && !(dtor instanceof IASTArrayDeclarator)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int offset(String word, String[] words) {
        for (int i = 0; i < words.length; i++) {
            int index = word.indexOf(words[i]);
            if (index != -1) {
                return index + 1 + words[i].length();
            }
        }
        return -1;
    }
}
