package org.eclipse.cdt.objc.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ObjCProjectNature implements IProjectNature {

    public static final String OBJC_NATURE_ID = ObjCPlugin.PLUGIN_ID + ".objcnature"; //$NON-NLS-1$

    /**
     * Utility method for adding a nature to a project.
     * 
     * @param project
     *            the project to add the nature
     * @param natureId
     *            the id of the nature to assign to the project
     * @param monitor
     *            a progress monitor to indicate the duration of the operation,
     *            or <code>null</code> if progress reporting is not required.
     * 
     */
    public static void addNature(IProject project, String natureId, IProgressMonitor monitor)
            throws CoreException {
        try {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            for (String prevNature : prevNatures) {
                if (natureId.equals(prevNature)) {
                    return;
                }
            }
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
            newNatures[0] = natureId;
            description.setNatureIds(newNatures);
            project.setDescription(description, monitor);
        } finally {
            monitor.done();
        }
    }

    public static void addObjCNature(IProject project, IProgressMonitor mon) throws CoreException {
        addNature(project, OBJC_NATURE_ID, mon);
    }

    public static void removeCNature(IProject project, IProgressMonitor mon) throws CoreException {
        removeNature(project, OBJC_NATURE_ID, mon);
    }

    /**
     * Utility method for removing a project nature from a project.
     * 
     * @param project
     *            the project to remove the nature from
     * @param natureId
     *            the nature id to remove
     * @param monitor
     *            a progress monitor to indicate the duration of the operation,
     *            or <code>null</code> if progress reporting is not required.
     */
    public static void removeNature(IProject project, String natureId, IProgressMonitor monitor)
            throws CoreException {
        IProjectDescription description = project.getDescription();
        String[] prevNatures = description.getNatureIds();
        List<String> newNatures = new ArrayList<String>(Arrays.asList(prevNatures));
        newNatures.remove(natureId);
        description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
        project.setDescription(description, monitor);
    }

    private IProject project;

    public ObjCProjectNature() {
    }

    public ObjCProjectNature(IProject project) {
        setProject(project);
    }

    /**
     * @see IProjectNature#configure
     */
    public void configure() throws CoreException {
    }

    /**
     * @see IProjectNature#deconfigure
     */
    public void deconfigure() throws CoreException {
    }

    /**
     * @see IProjectNature#getProject
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @see IProjectNature#setProject
     */
    public void setProject(IProject project) {
        this.project = project;
    }
}
