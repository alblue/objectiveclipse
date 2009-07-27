package org.eclipse.cdt.objc.ui.internal.cview;

import org.eclipse.cdt.internal.ui.cview.CViewContentProvider;

public class ObjCViewContentProvider extends CViewContentProvider {
    public ObjCViewContentProvider() {
    }

    @Override
    public Object[] getChildren(final Object element) {
        // if (element instanceof IProject) {
        // Object[] children = super.getChildren(element);
        // if (children == null) {
        // return null;
        // }
        // Object[] newChildren = new Object[children.length + 1];
        // newChildren[0] = new FrameworkContainer((CProject)
        // CModelManager.getDefault().getCModel()
        // .findCProject((IProject) element)); // should be CProject
        // System.arraycopy(children, 0, newChildren, 1, children.length);
        // System.out.println("Children of " + element + " (" +
        // element.getClass()
        // + ")");
        // return newChildren;
        // } else {

        // System.out.println("Children of " + element + " (" +
        // element.getClass() + ")");
        return super.getChildren(element);
        // }
    }

    @Override
    public boolean hasChildren(Object element) {
        return super.hasChildren(element);
    }

}
