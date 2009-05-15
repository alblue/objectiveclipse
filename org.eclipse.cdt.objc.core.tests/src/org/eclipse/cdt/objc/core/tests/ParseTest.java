package org.eclipse.cdt.objc.core.tests;

import org.junit.Test;

public class ParseTest extends AbstractParseTest {
    @SuppressWarnings("nls")
    // @Test
    public void testClass() {
        valid(parse("@class Foo @end"));
        invalid(parse("@class @end"));
    }

    @SuppressWarnings("nls")
    // @Test
    public void testInterface() {
        valid(parse("@interface Foo @end"));
        valid(parse("@interface Foo { } @end"));
        valid(parse("@interface Foo { NSString* strung; } @end"));
        invalid(parse("@interface @end"));
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
    // @Test
    public void testProtocol() {
        valid(parse("@protocol Foo @end"));
        invalid(parse("@protocol @end"));
    }
}
