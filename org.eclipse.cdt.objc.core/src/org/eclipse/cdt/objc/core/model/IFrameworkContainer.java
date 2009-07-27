package org.eclipse.cdt.objc.core.model;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IOpenable;
import org.eclipse.cdt.core.model.IParent;

/**
 * Represents a container of all the IFrameworks found in the project
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IFrameworkContainer extends ICElement, IParent, IOpenable {

    public IFramework[] getFrameworks() throws CModelException;
}
