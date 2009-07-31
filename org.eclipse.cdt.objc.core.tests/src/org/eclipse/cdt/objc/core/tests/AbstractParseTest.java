package org.eclipse.cdt.objc.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ICodeReaderFactory;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.NullLogService;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.core.parser.ParserUtil;
import org.eclipse.cdt.internal.core.dom.parser.c.CVisitor;
import org.eclipse.cdt.objc.core.dom.parser.objc.ObjCScannerExtensionConfiguration;
import org.eclipse.cdt.objc.core.internal.dom.parser.objc.GNUObjCSourceParser;
import org.eclipse.cdt.objc.core.internal.parser.scanner.ObjCPreprocessor;

public abstract class AbstractParseTest {

    protected void assertParse(IASTTranslationUnit ast, boolean errors) {
        assertNotNull(ast);
        if (ast != null) {
            final IASTProblem[] problems = CVisitor.getProblems(ast);
            assertNotNull(problems);
            if (problems != null) {
                if (errors) {
                    assertTrue(problems.length > 0);
                } else {
                    assertTrue(problems.length == 0);
                }
            }
        }
    }

    protected void invalid(IASTTranslationUnit ast) {
        assertParse(ast, true);
    }

    protected IASTTranslationUnit parse(String code) {
        final CodeReader reader = new CodeReader(code.toCharArray());
        final IScannerInfo info = new IScannerInfo() {

            public Map<String, String> getDefinedSymbols() {
                return new HashMap<String, String>();
            }

            public String[] getIncludePaths() {
                return new String[0];
            }
        };
        final ParserLanguage language = ParserLanguage.C;
        final IParserLogService log = new NullLogService();
        final IScannerExtensionConfiguration configuration = ObjCScannerExtensionConfiguration.getInstance();
        final ICodeReaderFactory readerFactory = null;
        final IScanner scanner = new ObjCPreprocessor(reader, info, language, log, configuration,
                readerFactory);
        final GNUObjCSourceParser parser = new GNUObjCSourceParser(scanner, ParserMode.COMPLETE_PARSE,
                ParserUtil.getParserLogService(), new GCCParserExtensionConfiguration());
        final IASTTranslationUnit ast = parser.parse();
        return ast;
    }

    protected IASTTranslationUnit parseExpression(String expression) {
        final IASTTranslationUnit ast = parse("main() { " + expression + ";}"); //$NON-NLS-1$ //$NON-NLS-2$
        return ast;
    }

    protected void valid(IASTTranslationUnit ast) {
        assertParse(ast, false);
    }

}
