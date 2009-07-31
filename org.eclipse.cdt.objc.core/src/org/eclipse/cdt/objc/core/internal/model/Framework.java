package org.eclipse.cdt.objc.core.internal.model;

import java.util.Map;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.model.CElementInfo;
import org.eclipse.cdt.internal.core.model.Openable;
import org.eclipse.cdt.internal.core.model.OpenableInfo;
import org.eclipse.cdt.internal.core.util.MementoTokenizer;
import org.eclipse.cdt.objc.core.model.IFramework;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("restriction")
public class Framework extends Openable implements IFramework {

    private static IResource getFrameworkCalled(String name) {
        // TODO This should handle other types in the future
        // Path path = new Path("/System/Library/Frameworks/" + name +
        // ".framework");
        // return (IResource) path.toFile();
        return null;
    }

    public Framework(ICElement parent, String name) {
        super(parent, getFrameworkCalled(name), name, ICElement.C_CCONTAINER);
    }

    @Override
    protected boolean buildStructure(OpenableInfo info, IProgressMonitor pm,
            Map<ICElement, CElementInfo> newElements, IResource underlyingResource) throws CModelException {
        return true;
    }

    @Override
    protected CElementInfo createElementInfo() {
        return new FrameworkInfo(this);
    }

    @Override
    public ICElement getHandleFromMemento(String token, MementoTokenizer memento) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected char getHandleMementoDelimiter() {
        // TODO Auto-generated method stub
        return 0;
    }

}
