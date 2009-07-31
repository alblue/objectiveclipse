package org.eclipse.cdt.objc.core.internal.parser.scanner;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorErrorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorPragmaStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorUndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNodeSpecification;
import org.eclipse.cdt.internal.core.parser.scanner.ILocationResolver;
import org.eclipse.cdt.objc.core.dom.ast.objc.IASTHeaderDocComment;

@SuppressWarnings("restriction")
class ASTComment extends ASTPreprocessorNode implements IASTComment {
    private final boolean fIsBlockComment;

    public ASTComment(IASTTranslationUnit parent, int startNumber, int endNumber, boolean isBlockComment) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fIsBlockComment = isBlockComment;
    }

    public char[] getComment() {
        return getSource(getOffset(), getLength());
    }

    public boolean isBlockComment() {
        return fIsBlockComment;
    }

    public void setComment(char[] comment) {
        assert false;
    }
}

@SuppressWarnings("restriction")
abstract class ASTDirectiveWithCondition extends ASTPreprocessorNode {
    private final boolean fActive;
    private final int fConditionOffset;

    public ASTDirectiveWithCondition(IASTTranslationUnit parent, int startNumber, int condNumber,
            int endNumber, boolean active) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fConditionOffset = condNumber;
        fActive = active;
    }

    public char[] getCondition() {
        return getConditionString().toCharArray();
    }

    public String getConditionString() {
        return new String(getSource(fConditionOffset, getOffset() + getLength() - fConditionOffset));
    }

    public boolean taken() {
        return fActive;
    }
}

class ASTElif extends ASTDirectiveWithCondition implements IASTPreprocessorElifStatement {
    public ASTElif(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber,
            boolean active) {
        super(parent, startNumber, condNumber, condEndNumber, active);
    }
}

class ASTElse extends ASTPreprocessorNode implements IASTPreprocessorElseStatement {
    private final boolean fActive;

    public ASTElse(IASTTranslationUnit parent, int startNumber, int endNumber, boolean active) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fActive = active;
    }

    public boolean taken() {
        return fActive;
    }
}

class ASTEndif extends ASTPreprocessorNode implements IASTPreprocessorEndifStatement {
    public ASTEndif(IASTTranslationUnit parent, int startNumber, int endNumber) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
    }
}

class ASTError extends ASTDirectiveWithCondition implements IASTPreprocessorErrorStatement {
    public ASTError(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber) {
        super(parent, startNumber, condNumber, condEndNumber, true);
    }

    public char[] getMessage() {
        return getCondition();
    }
}

class ASTFileLocation implements IASTFileLocation {
    private final int fLength;
    private final LocationCtxFile fLocationCtx;
    private final int fOffset;

    public ASTFileLocation(LocationCtxFile fileLocationCtx, int startOffset, int length) {
        fLocationCtx = fileLocationCtx;
        fOffset = startOffset;
        fLength = length;
    }

    public IASTFileLocation asFileLocation() {
        return this;
    }

    public int getEndingLineNumber() {
        int end = fLength > 0 ? fOffset + fLength - 1 : fOffset;
        return fLocationCtx.getLineNumber(end);
    }

    public String getFileName() {
        return fLocationCtx.getFilePath();
    }

    public LocationCtxFile getLocationContext() {
        return fLocationCtx;
    }

    public int getNodeLength() {
        return fLength;
    }

    public int getNodeOffset() {
        return fOffset;
    }

    public int getSequenceEndNumber() {
        return fLocationCtx.getSequenceNumberForOffset(fOffset + fLength, true);
    }

    public int getSequenceNumber() {
        return fLocationCtx.getSequenceNumberForOffset(fOffset, true);
    }

    public char[] getSource() {
        return fLocationCtx.getSource(fOffset, fLength);
    }

    public int getStartingLineNumber() {
        return fLocationCtx.getLineNumber(fOffset);
    }

    @Override
    public String toString() {
        return getFileName() + "[" + fOffset + "," + (fOffset + fLength) + ")"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }
}

class ASTFileLocationForBuiltins implements IASTFileLocation {
    private final String fFile;
    private final int fLength;
    private final int fOffset;

    public ASTFileLocationForBuiltins(String file, int startOffset, int length) {
        fFile = file;
        fOffset = startOffset;
        fLength = length;
    }

    public IASTFileLocation asFileLocation() {
        return this;
    }

    public int getEndingLineNumber() {
        return 0;
    }

    public String getFileName() {
        return fFile;
    }

    public int getNodeLength() {
        return fLength;
    }

    public int getNodeOffset() {
        return fOffset;
    }

    public int getStartingLineNumber() {
        return 0;
    }
}

class ASTFunctionStyleMacroDefinition extends ASTMacroDefinition implements
        IASTPreprocessorFunctionStyleMacroDefinition {
    /**
     * Constructor for builtins
     */
    public ASTFunctionStyleMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro,
            IASTFileLocation nameLoc, int expansionOffset) {
        super(parent, macro, nameLoc, expansionOffset);
    }

    /**
     * Regular constructor.
     */
    public ASTFunctionStyleMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, int startNumber,
            int nameNumber, int nameEndNumber, int expansionNumber, int endNumber, boolean active) {
        super(parent, macro, startNumber, nameNumber, nameEndNumber, expansionNumber, endNumber, active);
    }

    public void addParameter(IASTFunctionStyleMacroParameter parm) {
        assert false;
    }

    public IASTFunctionStyleMacroParameter[] getParameters() {
        IMacroBinding macro = getMacro();
        char[][] paramList = macro.getParameterList();
        IASTFunctionStyleMacroParameter[] result = new IASTFunctionStyleMacroParameter[paramList.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ASTMacroParameter(this, paramList[i], -1, -1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getName().getSimpleID());
        result.append('(');
        boolean needComma = false;
        for (IASTFunctionStyleMacroParameter param : getParameters()) {
            if (needComma) {
                result.append(',');
            }
            result.append(param.getParameter());
            needComma = true;
        }
        result.append(')');
        result.append('=');
        result.append(getExpansion());
        return result.toString();
    }
}

class ASTHeaderDocComment extends ASTComment implements IASTHeaderDocComment {

    public ASTHeaderDocComment(IASTTranslationUnit parent, int startNumber, int endNumber,
            boolean isBlockComment) {
        super(parent, startNumber, endNumber, isBlockComment);
    }

    public String getValue(String tag) {
        // TODO Implement
        return null;
    }
}

class ASTIf extends ASTDirectiveWithCondition implements IASTPreprocessorIfStatement {
    public ASTIf(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber,
            boolean active) {
        super(parent, startNumber, condNumber, condEndNumber, active);
    }
}

class ASTIfdef extends ASTDirectiveWithCondition implements IASTPreprocessorIfdefStatement {
    ASTMacroReferenceName fMacroRef;

    public ASTIfdef(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber,
            boolean taken, IMacroBinding macro) {
        super(parent, startNumber, condNumber, condEndNumber, taken);
        if (macro != null) {
            fMacroRef = new ASTMacroReferenceName(this, IASTPreprocessorStatement.MACRO_NAME, condNumber,
                    condEndNumber, macro, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement#getMacroReference
     * ()
     */
    public ASTPreprocessorName getMacroReference() {
        return fMacroRef;
    }
}

class ASTIfndef extends ASTDirectiveWithCondition implements IASTPreprocessorIfndefStatement {
    private ASTMacroReferenceName fMacroRef;

    public ASTIfndef(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber,
            boolean taken, IMacroBinding macro) {
        super(parent, startNumber, condNumber, condEndNumber, taken);
        if (macro != null) {
            fMacroRef = new ASTMacroReferenceName(this, IASTPreprocessorStatement.MACRO_NAME, condNumber,
                    condEndNumber, macro, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement#getMacroReference
     * ()
     */
    public ASTPreprocessorName getMacroReference() {
        return fMacroRef;
    }
}

class ASTImageLocation extends ASTFileLocationForBuiltins implements IASTImageLocation {
    private final int fKind;

    public ASTImageLocation(int kind, String file, int offset, int length) {
        super(file, offset, length);
        fKind = kind;
    }

    public int getLocationKind() {
        return fKind;
    }
}

class ASTInclusionNode implements IASTInclusionNode {
    private IASTInclusionNode[] fInclusions;
    protected LocationCtx fLocationCtx;

    public ASTInclusionNode(LocationCtx ctx) {
        fLocationCtx = ctx;
    }

    public IASTPreprocessorIncludeStatement getIncludeDirective() {
        return fLocationCtx.getInclusionStatement();
    }

    public IASTInclusionNode[] getNestedInclusions() {
        if (fInclusions == null) {
            ArrayList<IASTInclusionNode> result = new ArrayList<IASTInclusionNode>();
            fLocationCtx.getInclusions(result);
            fInclusions = result.toArray(new IASTInclusionNode[result.size()]);
        }
        return fInclusions;
    }
}

@SuppressWarnings("restriction")
class ASTInclusionStatement extends ASTPreprocessorNode implements IASTPreprocessorIncludeStatement {
    private final boolean fFoundByHeuristics;
    private final boolean fIsResolved;
    private final boolean fIsSystemInclude;
    private final ASTPreprocessorName fName;
    private final String fPath;

    public ASTInclusionStatement(IASTTranslationUnit parent, int startNumber, int nameStartNumber,
            int nameEndNumber, int endNumber, char[] headerName, String filePath, boolean userInclude,
            boolean active, boolean heuristic) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fName = new ASTPreprocessorName(this, IASTPreprocessorIncludeStatement.INCLUDE_NAME, nameStartNumber,
                nameEndNumber, headerName, null);
        fPath = filePath == null ? "" : filePath; //$NON-NLS-1$
        fIsResolved = filePath != null;
        fIsSystemInclude = !userInclude;
        fFoundByHeuristics = heuristic;
        if (!active) {
            setInactive();
        }
    }

    @Override
    void findNode(ASTNodeSpecification<?> nodeSpec) {
        super.findNode(nodeSpec);
        nodeSpec.visit(fName);
    }

    public IASTName getName() {
        return fName;
    }

    public String getPath() {
        return fPath;
    }

    public boolean isResolved() {
        return fIsResolved;
    }

    public boolean isResolvedByHeuristics() {
        return fFoundByHeuristics;
    }

    public boolean isSystemInclude() {
        return fIsSystemInclude;
    }
}

@SuppressWarnings("restriction")
class ASTMacroDefinition extends ASTPreprocessorNode implements IASTPreprocessorObjectStyleMacroDefinition {
    private final boolean fActive;
    private final int fExpansionNumber;
    private final int fExpansionOffset;
    private final ASTPreprocessorName fName;

    /**
     * Constructor for built-in macros
     * 
     * @param expansionOffset
     */
    public ASTMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, IASTFileLocation floc,
            int expansionOffset) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, -1, -1);
        fName = new ASTBuiltinName(this, IASTPreprocessorMacroDefinition.MACRO_NAME, floc, macro
                .getNameCharArray(), macro);
        fExpansionNumber = -1;
        fExpansionOffset = expansionOffset;
        fActive = true;
    }

    /**
     * Regular constructor.
     */
    public ASTMacroDefinition(IASTTranslationUnit parent, IMacroBinding macro, int startNumber,
            int nameNumber, int nameEndNumber, int expansionNumber, int endNumber, boolean active) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, endNumber);
        fExpansionNumber = expansionNumber;
        fExpansionOffset = -1;
        fActive = active;
        fName = new ASTPreprocessorDefinition(this, IASTPreprocessorMacroDefinition.MACRO_NAME, nameNumber,
                nameEndNumber, macro.getNameCharArray(), macro);
    }

    @Override
    void findNode(ASTNodeSpecification<?> nodeSpec) {
        super.findNode(nodeSpec);
        nodeSpec.visit(fName);
    }

    @Override
    public String getContainingFilename() {
        if (fName instanceof ASTBuiltinName) {
            return fName.getContainingFilename();
        }
        return super.getContainingFilename();
    }

    public String getExpansion() {
        return new String(getMacro().getExpansion());
    }

    public IASTFileLocation getExpansionLocation() {
        if (fExpansionNumber >= 0) {
            IASTTranslationUnit ast = getTranslationUnit();
            if (ast != null) {
                ILocationResolver lr = (ILocationResolver) ast.getAdapter(ILocationResolver.class);
                if (lr != null) {
                    return lr.getMappedFileLocation(fExpansionNumber, getOffset() + getLength()
                            - fExpansionNumber);
                }
            }
        }
        if (fExpansionOffset >= 0) {
            String fileName = fName.getContainingFilename();
            if (fileName != null) {
                final char[] expansionImage = getMacro().getExpansionImage();
                return new ASTFileLocationForBuiltins(fileName, fExpansionOffset, expansionImage.length);
            }
        }
        return null;
    }

    protected IMacroBinding getMacro() {
        return (IMacroBinding) fName.getBinding();
    }

    public IASTName getName() {
        return fName;
    }

    public int getRoleForName(IASTName n) {
        return (fName == n) ? r_definition : r_unclear;
    }

    @Override
    final public boolean isActive() {
        return fActive;
    }

    public void setExpansion(String exp) {
        assert false;
    }

    public void setName(IASTName name) {
        assert false;
    }

    @Override
    public String toString() {
        return getName().toString() + '=' + getExpansion();
    }
}

class ASTMacroExpansion extends ASTPreprocessorNode implements IASTPreprocessorMacroExpansion {

    private LocationCtxMacroExpansion fContext;

    public ASTMacroExpansion(IASTNode parent, int startNumber, int endNumber) {
        super(parent, IASTTranslationUnit.MACRO_EXPANSION, startNumber, endNumber);
    }

    public LocationCtxMacroExpansion getContext() {
        return fContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion#
     * getMacroDefinition()
     */
    public IASTPreprocessorMacroDefinition getMacroDefinition() {
        return fContext.getMacroDefinition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion#getName()
     */
    public ASTMacroReferenceName getMacroReference() {
        return fContext.getMacroReference();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion#
     * getNestedExpansions()
     */
    public ASTPreprocessorName[] getNestedMacroReferences() {
        return fContext.getNestedMacroReferences();
    }

    void setContext(LocationCtxMacroExpansion expansionCtx) {
        fContext = expansionCtx;
    }
}

@SuppressWarnings("deprecation")
class ASTMacroExpansionLocation implements IASTMacroExpansionLocation,
        org.eclipse.cdt.core.dom.ast.IASTMacroExpansion {

    private final LocationCtxMacroExpansion fContext;
    private final int fLength;
    private final int fOffset;

    public ASTMacroExpansionLocation(LocationCtxMacroExpansion macroExpansionCtx, int offset, int length) {
        fContext = macroExpansionCtx;
        fOffset = offset;
        fLength = length;
    }

    public IASTFileLocation asFileLocation() {
        return ((LocationCtxContainer) fContext.getParent()).createFileLocation(fContext.fOffsetInParent,
                fContext.fEndOffsetInParent - fContext.fOffsetInParent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation#getExpansion()
     */
    public IASTPreprocessorMacroExpansion getExpansion() {
        return fContext.getExpansion();
    }

    public IASTNodeLocation[] getExpansionLocations() {
        final IASTFileLocation fl = asFileLocation();
        return fl == null ? new IASTNodeLocation[0] : new IASTNodeLocation[] { fl };
    }

    public IASTImageLocation getImageLocation() {
        return fContext.getImageLocation(fOffset, fLength);
    }

    public IASTPreprocessorMacroDefinition getMacroDefinition() {
        return fContext.getMacroDefinition();
    }

    public IASTName getMacroReference() {
        return fContext.getMacroReference();
    }

    public int getNodeLength() {
        return fLength;
    }

    public int getNodeOffset() {
        return fOffset;
    }

    @Override
    public String toString() {
        return fContext.getMacroDefinition().getName().toString()
                + "[" + fOffset + "," + (fOffset + fLength) + ")"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }
}

class ASTMacroParameter extends ASTPreprocessorNode implements IASTFunctionStyleMacroParameter {
    private final String fParameter;

    public ASTMacroParameter(IASTPreprocessorFunctionStyleMacroDefinition parent, char[] param, int offset,
            int endOffset) {
        super(parent, IASTPreprocessorFunctionStyleMacroDefinition.PARAMETER, offset, endOffset);
        fParameter = new String(param);
    }

    public String getParameter() {
        return fParameter;
    }

    public void setParameter(String value) {
        assert false;
    }
}

class ASTPragma extends ASTDirectiveWithCondition implements IASTPreprocessorPragmaStatement {
    public ASTPragma(IASTTranslationUnit parent, int startNumber, int condNumber, int condEndNumber) {
        super(parent, startNumber, condNumber, condEndNumber, true);
    }

    public char[] getMessage() {
        return getCondition();
    }
}

/**
 * Models various AST-constructs obtained from the preprocessor.
 * 
 * @since 5.0
 */
@SuppressWarnings("restriction")
abstract class ASTPreprocessorNode extends ASTNode {
    public ASTPreprocessorNode(IASTNode parent, ASTNodeProperty property, int startNumber, int endNumber) {
        setParent(parent);
        setPropertyInParent(property);
        setOffset(startNumber);
        setLength(endNumber - startNumber);
    }

    public IASTNode copy() {
        throw new UnsupportedOperationException();
    }

    /**
     * Searches nodes by file location.
     */
    void findNode(ASTNodeSpecification<?> nodeSpec) {
        nodeSpec.visit(this);
    }

    @Override
    public IToken getLeadingSyntax() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    protected char[] getSource(int offset, int length) {
        final IASTTranslationUnit ast = getTranslationUnit();
        if (ast != null) {
            ILocationResolver lr = (ILocationResolver) ast.getAdapter(ILocationResolver.class);
            if (lr != null) {
                final IASTFileLocation loc = lr.getMappedFileLocation(offset, length);
                if (loc != null) {
                    return lr.getUnpreprocessedSignature(loc);
                }
            }
        }
        return CharArrayUtils.EMPTY;
    }

    @Override
    public IToken getTrailingSyntax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.valueOf(getSource(getOffset(), getLength()));
    }
}

@SuppressWarnings("restriction")
class ASTUndef extends ASTPreprocessorNode implements IASTPreprocessorUndefStatement {
    private final ASTPreprocessorName fName;

    public ASTUndef(IASTTranslationUnit parent, char[] name, int startNumber, int nameNumber,
            int nameEndNumber, IBinding binding, boolean isActive) {
        super(parent, IASTTranslationUnit.PREPROCESSOR_STATEMENT, startNumber, nameEndNumber);
        fName = new ASTPreprocessorName(this, IASTPreprocessorStatement.MACRO_NAME, nameNumber,
                nameEndNumber, name, binding);
        if (!isActive) {
            setInactive();
        }
    }

    public ASTPreprocessorName getMacroName() {
        return fName;
    }
}

class DependencyTree extends ASTInclusionNode implements IDependencyTree {
    public DependencyTree(LocationCtx ctx) {
        super(ctx);
    }

    public IASTInclusionNode[] getInclusions() {
        return getNestedInclusions();
    }

    public String getTranslationUnitPath() {
        return fLocationCtx.getFilePath();
    }
}
