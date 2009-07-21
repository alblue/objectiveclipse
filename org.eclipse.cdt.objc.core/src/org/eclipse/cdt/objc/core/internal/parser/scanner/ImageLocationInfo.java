package org.eclipse.cdt.objc.core.internal.parser.scanner;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;

/**
 * Information needed for computing image-locations. An image location exists
 * for a name and describes where the name came from. This can be: source code,
 * macro-expansion, parameter to macro-expansion or synthetic.
 * 
 * @since 5.0
 */
public abstract class ImageLocationInfo {

    public static class MacroImageLocationInfo extends ImageLocationInfo {
        private final int fEndOffset;
        private final ObjectStyleMacro fMacro;
        private final int fOffset;

        public MacroImageLocationInfo(ObjectStyleMacro macro, int offset, int endOffset) {
            fMacro = macro;
            fOffset = offset;
            fEndOffset = endOffset;
        }

        @Override
        public boolean canConcatenate(ImageLocationInfo info) {
            if (info instanceof MacroImageLocationInfo) {
                MacroImageLocationInfo mli = (MacroImageLocationInfo) info;
                if (mli.fMacro == fMacro && fEndOffset <= mli.fOffset) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public IASTImageLocation createLocation(LocationMap lm, ImageLocationInfo upto) {
            IASTPreprocessorMacroDefinition md = lm.getMacroDefinition(fMacro);
            IASTFileLocation expansionLoc = md.getExpansionLocation();
            if (expansionLoc != null) {
                final int length = ((MacroImageLocationInfo) upto).fEndOffset - fOffset;
                return new ASTImageLocation(IASTImageLocation.MACRO_DEFINITION, expansionLoc.getFileName(),
                        expansionLoc.getNodeOffset() + fOffset, length);
            }
            return null;
        }
    }

    public static class ParameterImageLocationInfo extends ImageLocationInfo {
        public int fSequenceEndNumber;
        public int fSequenceNumber;

        public ParameterImageLocationInfo(int sequenceNumber, int sequenceEndNumber) {
            fSequenceNumber = sequenceNumber;
            fSequenceEndNumber = sequenceEndNumber;
        }

        @Override
        public boolean canConcatenate(ImageLocationInfo info) {
            if (info instanceof ParameterImageLocationInfo) {
                ParameterImageLocationInfo pli = (ParameterImageLocationInfo) info;
                if (fSequenceEndNumber <= pli.fSequenceNumber) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public IASTImageLocation createLocation(LocationMap lm, ImageLocationInfo upto) {
            int sequenceEnd = ((ParameterImageLocationInfo) upto).fSequenceEndNumber;
            IASTFileLocation loc = lm.getMappedFileLocation(fSequenceNumber, sequenceEnd - fSequenceNumber);
            if (loc != null) {
                return new ASTImageLocation(IASTImageLocation.ARGUMENT_TO_MACRO_EXPANSION, loc.getFileName(),
                        loc.getNodeOffset(), loc.getNodeLength());
            }
            return null;
        }
    }

    public static final ImageLocationInfo[] NO_LOCATION_INFOS = {};
    int fTokenOffsetInExpansion = -1;

    public abstract boolean canConcatenate(ImageLocationInfo info);

    public abstract IASTImageLocation createLocation(LocationMap lm, ImageLocationInfo upto);
}
