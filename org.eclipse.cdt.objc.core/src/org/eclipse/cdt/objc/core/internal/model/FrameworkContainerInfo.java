package org.eclipse.cdt.objc.core.internal.model;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.model.CElement;
import org.eclipse.cdt.internal.core.model.OpenableInfo;

@SuppressWarnings("restriction")
public class FrameworkContainerInfo extends OpenableInfo {

    protected FrameworkContainerInfo(CElement element) {
        super(element);
    }

    @Override
    protected void addChild(ICElement child) {
        if (!includesChild(child)) {
            super.addChild(child);
        }
    }
}
