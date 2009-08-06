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

    private void executeCheck(String prefix, String postfix, String expected) throws BadLocationException {
        HDMAESTest a = new HDMAESTest(prefix, postfix);
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
        assertEquals(expected, document.get());
    }

    @SuppressWarnings("nls")
    @Test
    public void testHeaderDoc() throws BadLocationException {
        // No indent, simple start
        executeCheck("/*!", "", "/*!\n */");
        // Indent, simple start
        executeCheck("  /*!", "", "  /*!\n   */");
        // No indent, additional
        executeCheck("/*!\n * ", "\n */", "/*!\n * \n * \n */");
        // Indent, additional
        executeCheck("  /*!\n   * ", "\n   */", "  /*!\n   * \n   * \n   */");
        // No indent, additional
        executeCheck("/*!\n * Foo bar blurb", "\n */", "/*!\n * Foo bar blurb\n * \n */");
        // Indent, additional
        executeCheck("  /*!\n   * Foo bar blurb", "\n   */", "  /*!\n   * Foo bar blurb\n   * \n   */");
        // Auto-generate header output
        final String func = "NSString* foo(NSString* a, NSString* b) { }\n";
        // TODO Investigate why this doesn't generate @function stuff
        executeCheck("/*!", "\n" + func, "/*!\n */\n" + func);
    }
}
