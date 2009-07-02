package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.internal.core.dom.parser.DeclarationOptions;

@SuppressWarnings("restriction")
public class ObjCDeclarationOptions extends DeclarationOptions {

    final public static int _IMPLEMENTATION = 0x400;
    final public static int _INTERFACE = 0x200;
    final public static int _M_PARAM = 0x800;
    final public static int _PROTOCOL = 0x1000;

    public static final ObjCDeclarationOptions IMPLEMENTATION_LIST = new ObjCDeclarationOptions(
            _IMPLEMENTATION);

    public static final ObjCDeclarationOptions IMPLEMENTATION_METHOD_PARAMETER = new ObjCDeclarationOptions(
            ALLOW_ABSTRACT | _M_PARAM | _IMPLEMENTATION);

    public static final ObjCDeclarationOptions INSTANCE_VARIABLES = new ObjCDeclarationOptions(ALLOW_BITFIELD
            | ALLOW_ABSTRACT);

    public static final ObjCDeclarationOptions INTERFACE_LIST = new ObjCDeclarationOptions(_INTERFACE);

    public static final ObjCDeclarationOptions INTERFACE_METHOD_PARAMETER = new ObjCDeclarationOptions(
            _M_PARAM | _INTERFACE);

    public static final ObjCDeclarationOptions PROTOCOL_LIST = new ObjCDeclarationOptions(_PROTOCOL);

    public static final ObjCDeclarationOptions PROTOCOL_METHOD_PARAMETER = new ObjCDeclarationOptions(
            _M_PARAM | _PROTOCOL);

    final public boolean fIsImplementation;
    final public boolean fIsInterface;
    final public boolean fIsMethodParameter;
    final public boolean fIsProtocol;

    public ObjCDeclarationOptions(int options) {
        super(options);
        fIsImplementation = ((options & _IMPLEMENTATION) != 0) ? true : false;
        fIsInterface = ((options & _INTERFACE) != 0) ? true : false;
        fIsProtocol = ((options & _PROTOCOL) != 0) ? true : false;
        fIsMethodParameter = ((options & _M_PARAM) != 0) ? true : false;
    }

}
