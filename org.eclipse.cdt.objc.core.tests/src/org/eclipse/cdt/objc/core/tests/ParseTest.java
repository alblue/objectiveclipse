package org.eclipse.cdt.objc.core.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.objc.core.internal.dom.parser.objc.ObjCASTProblemDeclaration;
import org.eclipse.cdt.objc.core.internal.dom.parser.objc.ObjCASTSimpleDeclaration;
import org.junit.Test;

public class ParseTest extends AbstractParseTest {

    @SuppressWarnings("nls")
    @Test
    public void testBlocks() {
        valid(parse("void (^my_block)(void);"));
        valid(parse("my_block = ^(void){ printf(\"Hello World!\"); };"));
        valid(parse("void (^my_block)(void) = ^{ printf(\"Hello World!\"); }; int main() { my_block(); }"));
        valid(parse("typedef void (^my_block)(void); my_block block = ^{ printf(\"Hello World!\"); }; int main() { block(); }"));
        valid(parse("int (^my_block)(void); int main() { my_block = ^{ printf(\"Hello World!\"); }; }"));
        valid(parse("int (^my_block)(int, int); int main() { my_block = ^(int x, int y){ plot(x, y); }; }"));
        valid(parse("typedef void (^blockWithString)(char*);\n" + "int main() { \n"
                + "char *greeting = \"hello\";\n"
                + "blockWithString b = ^(char* place){ printf(\"%s %s\\n\", greeting, place); };\n"
                + "greeting = \"goodbye\";\n" + "b(“world”);\n" + " }"));
        valid(parse("typedef void (^blockWithString)(char*);\n" + "int main() { \n"
                + "__block char *mutable_greeting = \"hello\";\n"
                + "blockWithString c = ^{ mutable_greeting = \"goodbye\"; };\n"
                + "printf(\"%s\", mutable_greeting);\n" + "c();\n" + "printf(\"%s\", mutable_greeting);\n"
                + " }"));
        valid(parse("void (*my_block)(int i, int j);"));
        valid(parse("void (^my_block)(int i, int j);"));
        invalid(parse("my_block = ^(void) printf(\"Hello World!\"); "));
        invalid(parse("my_block = ^(void) { printf(\"Hello World!\"); "));
        invalid(parse("my_block = ^ printf(\"Hello World!\"); "));
        invalid(parse("my_block = ^ { printf(\"Hello World!\"); "));
    }

    @SuppressWarnings("nls")
    @Test
    public void testDuplicate() {
        IASTTranslationUnit a;
        valid(parse("@interface Foo {} @end @interface Bar {} @end"));
        invalid(a = parse("@interface Foo {} @interface Bar {} @end"));
        IASTDeclaration[] decl = a.getDeclarations();
        assertTrue("First interface parsed OK", decl[0] instanceof ObjCASTSimpleDeclaration);
        assertTrue("Second declaration is an error", decl[1] instanceof ObjCASTProblemDeclaration);
        assertTrue("Third interface parsed OK", decl[2] instanceof ObjCASTSimpleDeclaration);
    }

    @SuppressWarnings("nls")
    @Test
    public void testDynamic() {
        valid(parse("@implementation DynTest  @dynamic duo; @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testImplementation() {
        valid(parse("@implementation Foo @end"));
        valid(parse("@implementation Foo { } @end"));
        invalid(parse("@implementation @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testInterface() {
        valid(parse("@interface Foo \n - (void)display; \n@end"));
        valid(parse("@interface Foo \n - (void)setX: (int)x andY: (int)y; \n@end"));
        valid(parse("@interface Foo { } @end"));
        valid(parse("@interface Foo { NSString* strung; } @end"));
        invalid(parse("@interface @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testLink() {
        valid(parse("@protocol List @property id <List> next; @end"));
        valid(parse("@interface LinkedList : NSObject <List> { id <List> nextLinkedList;} @end"));
        valid(parse("@implementation LinkedList @synthesize next = nextLinkedList; @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testMessageSends() {
        valid(parseExpression("[foo bar]"));
        invalid(parseExpression("[foo bar wibble]"));
        valid(parseExpression("[foo bar:ding]"));
        invalid(parseExpression("[foo bar:]"));
        invalid(parseExpression("[foo bar:ding dong]"));
        valid(parseExpression("[foo bar:ding dong:merrily on:high]"));
        valid(parseExpression("@\"Foo\""));
        valid(parseExpression("@\"\""));
        valid(parseExpression("[[Foo alloc] init]"));
        valid(parseExpression("[[@\"Foo\" alloc] init]"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testProperty() {
        valid(parse("@interface PropertyTest : NSObject { float val; } @property float val; @end"));
        valid(parse("@interface PropertyTest : NSObject { NSButton *jenson; } @property (nonatomic, retain) IBOutlet NSButton *jenson; @end"));
        valid(parse("@interface PropertyTest : NSObject { Link *local; } @property (nonatomic, retain) __weak Link *local; @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testProtocol() {
        valid(parse("@protocol Foo @end"));
        invalid(parse("@protocol @end"));
    }

    @SuppressWarnings("nls")
    @Test
    public void testSynthesize() {
        valid(parse("@implementation SynthTest  @synthesize up, down, left = right; @end"));
    }

}
