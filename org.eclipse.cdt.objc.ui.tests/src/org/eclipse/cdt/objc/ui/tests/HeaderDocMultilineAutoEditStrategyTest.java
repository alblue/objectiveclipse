package org.eclipse.cdt.objc.ui.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.objc.core.tests.AbstractParseTest;
import org.eclipse.cdt.objc.ui.text.doctools.headerdoc.HeaderDocMultilineAutoEditStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;

public class HeaderDocMultilineAutoEditStrategyTest extends AbstractParseTest {

    class CommandTest extends DocumentCommand {

    }

    class HDMAESTest extends HeaderDocMultilineAutoEditStrategy {
        private final String document;
        private final String postfix;
        private final String prefix;

        public HDMAESTest(String prefix, String postfix) {
            this.prefix = prefix;
            this.postfix = postfix;
            document = this.prefix + this.postfix;
        }

        @Override
        public IASTTranslationUnit getAST() {
            return parse(document);
        }

        public IDocument getDocument() {
            return new Document(document);
        }
    }

    @Test
    public void testHeaderDoc() throws BadLocationException {
        // TODO Extend this with more test cases
        HDMAESTest a = new HDMAESTest("/*!", ""); //$NON-NLS-1$ //$NON-NLS-2$
        CommandTest c = new CommandTest();
        IDocument document = a.getDocument();
        c.text = "\n"; //$NON-NLS-1$
        c.length = 0;
        c.offset = a.prefix.length();
        c.caretOffset = -1;
        c.owner = null;
        c.doit = true;
        a.customizeDocumentAfterNewLine(document, c);
        document.replace(c.offset, c.length, c.text);
        assertEquals("/*!\n */", document.get()); //$NON-NLS-1$
    }
}
