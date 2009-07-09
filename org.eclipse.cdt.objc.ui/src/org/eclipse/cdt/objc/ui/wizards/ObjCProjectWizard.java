package org.eclipse.cdt.objc.ui.wizards;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.language.ProjectLanguageConfiguration;
import org.eclipse.cdt.core.model.LanguageManager;
import org.eclipse.cdt.objc.core.ObjCProjectNature;
import org.eclipse.cdt.objc.core.dom.ast.objc.ObjCLanguage;
import org.eclipse.cdt.ui.newui.UIMessages;
import org.eclipse.cdt.ui.wizards.CDTCommonProjectWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;

public class ObjCProjectWizard extends CDTCommonProjectWizard {

    @Override
    public void addPages() {
        fMainPage = new ObjCMainWizardPage();
        fMainPage.setTitle("Objective C wizard");
        fMainPage.setDescription("Objective C description");
        addPage(fMainPage);
    }

    @Override
    public boolean canFinish() {
        if (fMainPage.h_selected != null) {
            if (!fMainPage.h_selected.canFinish()) {
                return false;
            }
            String s = fMainPage.h_selected.getErrorMessage();
            if (s != null) {
                return false;
            }
        }
        for (int i = 0; i < super.getPageCount(); i++) {
            if (!(super.getPages()[i]).isPageComplete()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected IProject continueCreation(IProject prj) {
        if (continueCreationMonitor == null) {
            continueCreationMonitor = new NullProgressMonitor();
        }

        try {
            continueCreationMonitor.beginTask(UIMessages.getString("CProjectWizard.0"), 3); //$NON-NLS-1$
            ObjCProjectNature.addObjCNature(prj, new SubProgressMonitor(continueCreationMonitor, 1));
            CProjectNature.addCNature(prj, new SubProgressMonitor(continueCreationMonitor, 1));

            LanguageManager langManager = LanguageManager.getInstance();

            ProjectLanguageConfiguration langConfig = langManager.getLanguageConfiguration(prj);
            // Sets C headers to be Objective-C types
            langConfig.addContentTypeMapping(null, CCorePlugin.CONTENT_TYPE_CHEADER, ObjCLanguage.ID);

            langManager.storeLanguageMappingConfiguration(prj, new IContentType[0]);
            continueCreationMonitor.worked(1);
        } catch (CoreException e) {
            e.printStackTrace();
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
