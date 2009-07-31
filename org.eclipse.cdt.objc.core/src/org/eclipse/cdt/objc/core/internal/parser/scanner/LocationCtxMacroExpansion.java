package org.eclipse.cdt.objc.core.internal.parser.scanner;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;

/**
 * A location context representing macro expansions.
 * 
 * @since 5.0
 */
@SuppressWarnings("restriction")
class LocationCtxMacroExpansion extends LocationCtx {
    private final ASTMacroReferenceName fExpansionName;
    private final int fLength;
    private final ImageLocationInfo[] fLocationInfos;
    private final LocationMap fLocationMap;

    public LocationCtxMacroExpansion(LocationMap map, LocationCtxContainer parent, int parentOffset,
            int parentEndOffset, int sequenceNumber, int length, ImageLocationInfo[] imageLocations,
            ASTMacroReferenceName expansionName) {
        super(parent, parentOffset, parentEndOffset, sequenceNumber);
        fLocationMap = map;
        fLength = length;
        fLocationInfos = imageLocations;
        fExpansionName = expansionName;
        if (expansionName.getParent() instanceof ASTMacroExpansion == false) {
            throw new IllegalArgumentException(expansionName.toString() + " is not a macro expansion name"); //$NON-NLS-1$
        }
    }

    @Override
    public boolean collectLocations(int start, int length, ArrayList<IASTNodeLocation> locations) {
        final int offset = start - fSequenceNumber;
        assert offset >= 0 && length >= 0;

        if (offset + length <= fLength) {
            locations.add(new ASTMacroExpansionLocation(this, offset, length));
            return true;
        }

        locations.add(new ASTMacroExpansionLocation(this, offset, fLength - offset));
        return false;
    }

    @Override
    public LocationCtxMacroExpansion findEnclosingMacroExpansion(int sequenceNumber, int length) {
        return this;
    }

    public ASTMacroExpansion getExpansion() {
        return (ASTMacroExpansion) fExpansionName.getParent();
    }

    public IASTImageLocation getImageLocation(int offset, int length) {
        if (length == 0) {
            return null;
        }
        final int end = offset + length;
        int nextToCheck = offset;
        ImageLocationInfo firstInfo = null;
        ImageLocationInfo lastInfo = null;
        for (ImageLocationInfo info : fLocationInfos) {
            if (info.fTokenOffsetInExpansion == nextToCheck) {
                if (firstInfo == null || lastInfo == null) {
                    firstInfo = lastInfo = info;
                } else if (lastInfo.canConcatenate(info)) {
                    lastInfo = info;
                } else {
                    return null;
                }
                if (++nextToCheck == end) {
                    return firstInfo.createLocation(fLocationMap, lastInfo);
                }
            } else if (info.fTokenOffsetInExpansion > nextToCheck) {
                return null;
            }
        }
        return null;
    }

    public IASTPreprocessorMacroDefinition getMacroDefinition() {
        return fLocationMap.getMacroDefinition((IMacroBinding) fExpansionName.getBinding());
    }

    public ASTMacroReferenceName getMacroReference() {
        return fExpansionName;
    }

    public ASTPreprocessorName[] getNestedMacroReferences() {
        return fLocationMap.getNestedMacroReferences((ASTMacroExpansion) fExpansionName.getParent());
    }

    @Override
    public int getSequenceLength() {
        return fLength;
    }
}
