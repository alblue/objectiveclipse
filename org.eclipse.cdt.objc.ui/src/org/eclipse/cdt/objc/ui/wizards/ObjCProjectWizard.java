package org.eclipse.cdt.objc.ui.wizards;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.objc.core.ObjCProjectNature;
import org.eclipse.cdt.ui.newui.UIMessages;
import org.eclipse.cdt.ui.wizards.CDTCommonProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class ObjCProjectWizard extends CDTCommonProjectWizard {

    @Override
    public void addPages() {
        fMainPage = new ObjCMainWizardPage();
        fMainPage.setTitle("Objective C wizard");
        fMainPage.setDescription("Objeciive C description");
        addPage(fMainPage);
    }

    @Override
    protected IProject continueCreation(IProject prj) {
        if (continueCreationMonitor == null) {
            continueCreationMonitor = new NullProgressMonitor();
        }

        try {
            continueCreationMonitor.beginTask(UIMessages.getString("CProjectWizard.0"), 2); //$NON-NLS-1$
            ObjCProjectNature.addObjCNature(prj, new SubProgressMonitor(continueCreationMonitor, 1));
            CProjectNature.addCNature(prj, new SubProgressMonitor(continueCreationMonitor, 1));
        } catch (CoreException e) {
        } finally {
            continueCreationMonitor.done();
        }
        return prj;
    }

    @Override
    public String[] getContentTypeIDs() {
        return new String[] { CCorePlugin.CONTENT_TYPE_CSOURCE, CCorePlugin.CONTENT_TYPE_CHEADER };
    }

    @Override
    public String[] getNatures() {
        return new String[] { ObjCProjectNature.OBJC_NATURE_ID, CProjectNature.C_NATURE_ID,
                "org.eclipse.cdt.objc.core.objcSource" };
    }

}
