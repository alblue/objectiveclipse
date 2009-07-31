package org.eclipse.cdt.objc.core.internal.model;

import java.util.Map;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.model.CElementInfo;
import org.eclipse.cdt.internal.core.model.CProject;
import org.eclipse.cdt.internal.core.model.Openable;
import org.eclipse.cdt.internal.core.model.OpenableInfo;
import org.eclipse.cdt.internal.core.util.MementoTokenizer;
import org.eclipse.cdt.objc.core.ObjCPlugin;
import org.eclipse.cdt.objc.core.model.IFramework;
import org.eclipse.cdt.objc.core.model.IFrameworkContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("restriction")
public class FrameworkContainer extends Openable implements IFrameworkContainer {
    public FrameworkContainer(CProject cProject) {
        super(cProject, null,
                ObjCPlugin.getResourceString("FrameworkContainer.Frameworks"), ICElement.C_VCONTAINER); //$NON-NLS-1$
        try {
            for (IFramework framework : getFrameworks()) {
                // addChild(framework);
            }
        } catch (CModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected boolean buildStructure(OpenableInfo info, IProgressMonitor pm,
            Map<ICElement, CElementInfo> newElements, IResource underlyingResource) throws CModelException {
        for (IFramework framework : getFrameworks()) {
            addChild(framework);
        }
        return true;
        // TODO I think this kicks off a background process, not needed for
        // frameworks
    }

    @Override
    protected CElementInfo createElementInfo() {
        return new FrameworkContainerInfo(this);
    }

    public IFramework[] getFrameworks() throws CModelException {
        // TODO Testing
        return new IFramework[] { new Framework(this, "Foundation") };
    }

    @Override
    public ICElement getHandleFromMemento(String token, MementoTokenizer memento) {
        return null;
    }

    @Override
    protected char getHandleMementoDelimiter() {
        return 0;
    }

}
