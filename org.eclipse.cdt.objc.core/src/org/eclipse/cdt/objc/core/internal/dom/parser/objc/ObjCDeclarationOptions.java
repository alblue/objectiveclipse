package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.internal.core.dom.parser.DeclarationOptions;

@SuppressWarnings("restriction")
public class ObjCDeclarationOptions extends DeclarationOptions {

    final public static int ALLOW_VISIBILITY = 0x200;
    final public static int ALLOW_OPTIONALITY = 0x400;
    final public static int METHOD_PARAM = 0x800;
    final public static int ALLOW_PROPERTY = 0x1000;
    final public static int ALLOW_METHOD = 0x2000;
    final public static int ALLOW_PROTOCOL_MODIFIERS = 0x4000;
    final public static int IS_IMPLEMENTATION = 0x8000;
    final public static int IS_PROPERTY = 0x10000;
    final public static int ALLOW_PROPERTY_ATTR = 0x20000;

    public static final ObjCDeclarationOptions DYNAMIC_PROPERTY_DEF = new ObjCDeclarationOptions(
            NO_INITIALIZER | NO_FUNCTIONS | NO_NESTED | IS_PROPERTY);
    
    public static final ObjCDeclarationOptions SYNTHESIZED_PROPERTY_DEF = new ObjCDeclarationOptions(
            NO_FUNCTIONS | NO_NESTED | IS_PROPERTY);

    public static final ObjCDeclarationOptions IMPLEMENTATION_LIST = new ObjCDeclarationOptions(
            ALLOW_PROPERTY | NO_INITIALIZER | IS_IMPLEMENTATION | ALLOW_METHOD);

    public static final ObjCDeclarationOptions METHOD_PARAMETER = new ObjCDeclarationOptions(
            ALLOW_ABSTRACT | METHOD_PARAM);

    public static final ObjCDeclarationOptions INSTANCE_VARIABLES = new ObjCDeclarationOptions(
            ALLOW_ABSTRACT | NO_INITIALIZER | ALLOW_VISIBILITY);

    public static final ObjCDeclarationOptions INTERFACE_LIST = new ObjCDeclarationOptions(
            ALLOW_PROPERTY | NO_INITIALIZER | ALLOW_METHOD);

    public static final ObjCDeclarationOptions PROPERTY_DECL = new ObjCDeclarationOptions(
            NO_FUNCTIONS | NO_NESTED | IS_PROPERTY | ALLOW_PROPERTY_ATTR);

    public static final ObjCDeclarationOptions PROTOCOL_LIST = new ObjCDeclarationOptions(
            REQUIRE_ABSTRACT | NO_INITIALIZER | ALLOW_OPTIONALITY | ALLOW_METHOD);
    
    final public boolean fIsMethodParameter;
    final public boolean fIsProperty;
    final public boolean fAllowVisibilityLabel;
    final public boolean fAllowOptionalityLabel;
    final public boolean fAllowProperties;
    final public boolean fAllowMethods;
    final public boolean fAllowProtocolParamModifiers;
    final public boolean fAllowPropertyAttributes;
    final public boolean fIsImplementation;

    public ObjCDeclarationOptions(int options) {
        super(options);
        fIsMethodParameter = ((options & METHOD_PARAM) != 0) ? true : false;
        fAllowVisibilityLabel = ((options & ALLOW_VISIBILITY) != 0) ? true : false;
        fAllowOptionalityLabel = ((options & ALLOW_OPTIONALITY) != 0) ? true : false;
        fAllowProperties = ((options & ALLOW_PROPERTY) != 0) ? true : false;
        fAllowMethods = ((options & ALLOW_METHOD) != 0) ? true : false;
        fIsImplementation = ((options & IS_IMPLEMENTATION) != 0) ? true : false;
        fAllowProtocolParamModifiers = ((options & ALLOW_PROTOCOL_MODIFIERS) != 0) ? true : false;
        fIsProperty = ((options & IS_PROPERTY) != 0) ? true : false;
        fAllowPropertyAttributes = ((options & ALLOW_PROPERTY_ATTR) != 0) ? true : false;
    }

}
