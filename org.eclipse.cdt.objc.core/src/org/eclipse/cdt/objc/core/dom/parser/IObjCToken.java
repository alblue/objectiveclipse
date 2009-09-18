package org.eclipse.cdt.objc.core.dom.parser;

import org.eclipse.cdt.core.parser.IGCCToken;

public interface IObjCToken extends IGCCToken {

    public static final int t_AtOptional = FIRST_RESERVED_IGCCToken + 6;
    public static final int t_AtDynamic = FIRST_RESERVED_IGCCToken + 7;
    public static final int t_AtSynthesize = FIRST_RESERVED_IGCCToken + 8;
    public static final int t_AtSynchronized = FIRST_RESERVED_IGCCToken + 9;
    public static final int t_AtClass = FIRST_RESERVED_IGCCToken + 10;
    public static final int t_AtDefs = FIRST_RESERVED_IGCCToken + 11;
    public static final int t_AtEncode = FIRST_RESERVED_IGCCToken + 12;
    public static final int t_AtEnd = FIRST_RESERVED_IGCCToken + 13;
    public static final int t_AtInterface = FIRST_RESERVED_IGCCToken + 14;
    public static final int t_AtImplementation = FIRST_RESERVED_IGCCToken + 15;
    public static final int t_AtPrivate = FIRST_RESERVED_IGCCToken + 16;
    public static final int t_AtProtected = FIRST_RESERVED_IGCCToken + 17;
    public static final int t_AtProtocol = FIRST_RESERVED_IGCCToken + 18;
    public static final int t_AtPublic = FIRST_RESERVED_IGCCToken + 19;
    public static final int t_AtSelector = FIRST_RESERVED_IGCCToken + 20;
    public static final int t_id = FIRST_RESERVED_IGCCToken + 21;
    public static final int t_self = FIRST_RESERVED_IGCCToken + 22;
    public static final int t_nil = FIRST_RESERVED_IGCCToken + 23;
    public static final int t_super = FIRST_RESERVED_IGCCToken + 24;
    public static final int t_in = FIRST_RESERVED_IGCCToken + 25;
    public static final int t_out = FIRST_RESERVED_IGCCToken + 26;
    public static final int t_inout = FIRST_RESERVED_IGCCToken + 27;
    public static final int t_bycopy = FIRST_RESERVED_IGCCToken + 28;
    public static final int t_byref = FIRST_RESERVED_IGCCToken + 29;
    public static final int t_oneway = FIRST_RESERVED_IGCCToken + 30;
    public static final int t_SEL = FIRST_RESERVED_IGCCToken + 31;
    public static final int t_BOOL = FIRST_RESERVED_IGCCToken + 32;
    public static final int t_YES = FIRST_RESERVED_IGCCToken + 33;
    public static final int t_NO = FIRST_RESERVED_IGCCToken + 34;
    public static final int t_AtTry = FIRST_RESERVED_IGCCToken + 35;
    public static final int t_AtCatch = FIRST_RESERVED_IGCCToken + 36;
    public static final int t_AtFinally = FIRST_RESERVED_IGCCToken + 37;
    public static final int t_AtThrow = FIRST_RESERVED_IGCCToken + 38;
    public static final int t_AtRequired = FIRST_RESERVED_IGCCToken + 39;
    public static final int t__weak = FIRST_RESERVED_IGCCToken + 40;
    public static final int t__strong = FIRST_RESERVED_IGCCToken + 41;
    public static final int t_AtProperty = FIRST_RESERVED_IGCCToken + 42;
    public static final int t_IBOutlet = FIRST_RESERVED_IGCCToken + 43;
    public static final int t_getter = FIRST_RESERVED_IGCCToken + 44;
    public static final int t_setter = FIRST_RESERVED_IGCCToken + 45;
    public static final int t_readwrite = FIRST_RESERVED_IGCCToken + 46;
    public static final int t_readonly = FIRST_RESERVED_IGCCToken + 47;
    public static final int t_assign = FIRST_RESERVED_IGCCToken + 48;
    public static final int t_retain = FIRST_RESERVED_IGCCToken + 49;
    public static final int t_copy = FIRST_RESERVED_IGCCToken + 50;
    public static final int t_nonatomic = FIRST_RESERVED_IGCCToken + 51;
    public static final int t__block = FIRST_RESERVED_IGCCToken + 52;
    
}
