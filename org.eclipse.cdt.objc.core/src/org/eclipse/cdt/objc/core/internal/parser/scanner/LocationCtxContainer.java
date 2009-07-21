package org.eclipse.cdt.objc.core.internal.parser.scanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;

/**
 * Base class for all location contexts that can contain children.
 * <p>
 * 
 * @since 5.0
 */
class LocationCtxContainer extends LocationCtx {
    private ArrayList<LocationCtx> fChildren;

    /**
     * The total length of all children in terms of sequence numbers.
     */
    private int fChildSequenceLength;
    private int[] fLineOffsets;
    private final char[] fSource;

    public LocationCtxContainer(LocationCtxContainer parent, char[] source, int parentOffset,
            int parentEndOffset, int sequenceNumber) {
        super(parent, parentOffset, parentEndOffset, sequenceNumber);
        fSource = source;
    }

    public void addChild(LocationCtx locationCtx) {
        if (fChildren == null) {
            fChildren = new ArrayList<LocationCtx>();
        }
        fChildren.add(locationCtx);
    }

    @Override
    public void addChildSequenceLength(int childLength) {
        fChildSequenceLength += childLength;
    }

    private ArrayList<IASTNodeLocation> addFileLocation(int offset, int length,
            ArrayList<IASTNodeLocation> sofar) {
        IASTFileLocation loc = createFileLocation(offset, length);
        if (loc != null) {
            sofar.add(loc);
        }
        return sofar;
    }

    @Override
    public boolean collectLocations(int sequenceNumber, final int length,
            ArrayList<IASTNodeLocation> locations) {
        final int endSequenceNumber = sequenceNumber + length;
        if (fChildren != null) {
            int childIdx = Math.max(0, findChildIdxLessOrEqualThan(sequenceNumber, false));
            for (; childIdx < fChildren.size(); childIdx++) {
                final LocationCtx child = fChildren.get(childIdx);

                // create the location between start and the child
                if (sequenceNumber < child.fSequenceNumber) {
                    // compute offset backwards from the child's offset
                    final int offset = child.fEndOffsetInParent - (child.fSequenceNumber - sequenceNumber);
                    // it the child is not affected, we are done.
                    if (endSequenceNumber <= child.fSequenceNumber) {
                        addFileLocation(offset, endSequenceNumber - sequenceNumber, locations);
                        return true;
                    }
                    if (offset < child.fOffsetInParent) {
                        addFileLocation(offset, child.fOffsetInParent - offset, locations);
                    }
                    sequenceNumber = child.fSequenceNumber;
                }

                // let the child create locations
                final int childEndSequenceNumber = child.fSequenceNumber + child.getSequenceLength();
                if (sequenceNumber < childEndSequenceNumber) {
                    if (child.collectLocations(sequenceNumber, endSequenceNumber - sequenceNumber, locations)) {
                        return true;
                    }
                    sequenceNumber = childEndSequenceNumber;
                }
            }
        }

        // create the location after the last child.
        final int myEndNumber = fSequenceNumber + getSequenceLength();
        final int offset = fSource.length - (myEndNumber - sequenceNumber);
        if (endSequenceNumber <= myEndNumber) {
            addFileLocation(offset, endSequenceNumber - sequenceNumber, locations);
            return true;
        }
        addFileLocation(offset, fSource.length - offset, locations);
        return false;
    }

    private int[] computeLineOffsets() {
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        for (int i = 0; i < fSource.length; i++) {
            if (fSource[i] == '\n') {
                offsets.add(new Integer(i));
            }
        }
        int[] result = new int[offsets.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = offsets.get(i).intValue();

        }
        return result;
    }

    @Override
    public int convertToSequenceEndNumber(int sequenceNumber) {
        // try to delegate to a child.
        final LocationCtx child = findChildLessOrEqualThan(sequenceNumber, false);
        if (child != null) {
            sequenceNumber = child.convertToSequenceEndNumber(sequenceNumber);
        }

        // if the potentially converted sequence number is the beginning of this
        // context,
        // skip the denotation of this context in the parent.
        if (sequenceNumber == fSequenceNumber) {
            return sequenceNumber - fEndOffsetInParent + fOffsetInParent;
        }

        return sequenceNumber;
    }

    ASTFileLocation createFileLocation(int start, int length) {
        return null;
    }

    final int findChildIdxLessOrEqualThan(int sequenceNumber, boolean beforeReplacedChars) {
        if (fChildren == null) {
            return -1;
        }
        int upper = fChildren.size();
        int lower = 0;
        while (upper > lower) {
            int middle = (upper + lower) / 2;
            LocationCtx child = fChildren.get(middle);
            int childSequenceNumber = child.fSequenceNumber;
            if (beforeReplacedChars) {
                childSequenceNumber -= child.fEndOffsetInParent - child.fOffsetInParent;
            }
            if (childSequenceNumber <= sequenceNumber) {
                lower = middle + 1;
            } else {
                upper = middle;
            }
        }
        return lower - 1;
    }

    final LocationCtx findChildLessOrEqualThan(final int sequenceNumber, boolean beforeReplacedChars) {
        final int idx = findChildIdxLessOrEqualThan(sequenceNumber, beforeReplacedChars);
        return idx >= 0 ? fChildren.get(idx) : null;
    }

    @Override
    public final LocationCtxMacroExpansion findEnclosingMacroExpansion(int sequenceNumber, int length) {
        int testEnd = length > 1 ? sequenceNumber + length - 1 : sequenceNumber;
        final LocationCtx child = findChildLessOrEqualThan(sequenceNumber, true);
        if (child != null && child.fSequenceNumber + child.getSequenceLength() > testEnd) {
            return child.findEnclosingMacroExpansion(sequenceNumber, length);
        }
        return null;
    }

    @Override
    public ASTFileLocation findMappedFileLocation(int sequenceNumber, int length) {
        // try to delegate to a child.
        int testEnd = length > 1 ? sequenceNumber + length - 1 : sequenceNumber;
        final LocationCtx child = findChildLessOrEqualThan(sequenceNumber, false);
        if (child != null && child.fSequenceNumber + child.getSequenceLength() > testEnd) {
            return child.findMappedFileLocation(sequenceNumber, length);
        }
        return super.findMappedFileLocation(sequenceNumber, length);
    }

    @Override
    public final LocationCtx findSurroundingContext(int sequenceNumber, int length) {
        int testEnd = length > 1 ? sequenceNumber + length - 1 : sequenceNumber;
        final LocationCtx child = findChildLessOrEqualThan(sequenceNumber, false);
        if (child != null && child.fSequenceNumber + child.getSequenceLength() > testEnd) {
            return child.findSurroundingContext(sequenceNumber, length);
        }
        return this;
    }

    @Override
    public Collection<LocationCtx> getChildren() {
        if (fChildren == null) {
            return Collections.emptyList();
        }
        return fChildren;
    }

    @Override
    public void getInclusions(ArrayList<IASTInclusionNode> result) {
        if (fChildren != null) {
            for (LocationCtx ctx : fChildren) {
                if (ctx.getInclusionStatement() != null) {
                    result.add(new ASTInclusionNode(ctx));
                } else {
                    ctx.getInclusions(result);
                }
            }
        }
    }

    @Override
    public int getLineNumber(int offset) {
        if (fLineOffsets == null) {
            fLineOffsets = computeLineOffsets();
        }
        int idx = Arrays.binarySearch(fLineOffsets, offset);
        if (idx < 0) {
            return -idx;
        }
        return idx + 1;
    }

    @Override
    public final int getSequenceLength() {
        return fSource.length + fChildSequenceLength;
    }

    @Override
    public final int getSequenceNumberForOffset(int offset, boolean checkChildren) {
        int result = fSequenceNumber + fChildSequenceLength + offset;
        if (checkChildren && fChildren != null) {
            for (int i = fChildren.size() - 1; i >= 0; i--) {
                final LocationCtx child = fChildren.get(i);
                if (child.fEndOffsetInParent > offset) { // child was inserted
                                                         // behind the offset,
                                                         // adjust sequence
                                                         // number
                    result -= child.getSequenceLength();
                } else {
                    return result;
                }
            }
        }
        return result;
    }

    public char[] getSource(int offset, int length) {
        offset = Math.max(0, Math.min(offset, fSource.length));
        length = Math.max(0, Math.min(length, fSource.length - offset));
        char[] result = new char[length];
        System.arraycopy(fSource, offset, result, 0, length);
        return result;
    }
}
