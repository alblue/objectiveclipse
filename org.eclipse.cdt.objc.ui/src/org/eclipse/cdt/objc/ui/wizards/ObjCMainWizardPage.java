package org.eclipse.cdt.objc.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.ui.wizards.CDTMainWizardPage;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;

public class ObjCMainWizardPage extends CDTMainWizardPage {

    public ObjCMainWizardPage() {
        super("Objective C Wizard");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List filterItems(List items) {
        List filtered = new ArrayList(items.size());
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            EntryDescriptor descriptor = (EntryDescriptor) iterator.next();
            if ("org.eclipse.cdt.build.core.buildArtefactType.exe".equals(descriptor.getId()) //$NON-NLS-1$
                    || descriptor.getId().startsWith("objc.")) { //$NON-NLS-1$
                filtered.add(descriptor);
            }
        }
        return filtered;
    }

}
