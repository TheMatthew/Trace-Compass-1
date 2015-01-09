package org.eclipse.tracecompass.tmf.attributetree.ui.wizard;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class AttributeTreeWizard extends Wizard implements INewWizard {
	
	private String PAGE_NAME_TREE_NAME = "Attribute Tree Name";
	private String WIZARD_WINDOW_TITLE = "New Attribute Tree";
	private String ATTRIBUTE_TREE_EXTENSION = "xml";
	
	private ISelection selection;	
	AttributeTreeWizardPage treeNamePage;
	
	@Override
	public void addPages() {
		treeNamePage = new AttributeTreeWizardPage(PAGE_NAME_TREE_NAME);
		addPage(treeNamePage);
	}
	
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        setWindowTitle(WIZARD_WINDOW_TITLE);
    }

	@Override
	public boolean performFinish() {
		String attributeTreeName = treeNamePage.getAttributeTreeName();
		
		IFile attributeTreeFile = null;
		IFolder attributeTreeFolder = null;
		IProject attributeTreeProject = null;
		
		Object selectedElement = null;
		if (selection instanceof IStructuredSelection) {
			selectedElement = ((IStructuredSelection) selection).getFirstElement();
		}
		
		if (selectedElement instanceof IProject) {
			attributeTreeProject = (IProject) selectedElement;
		} else if (selectedElement instanceof IFolder) {
			attributeTreeFolder = (IFolder) selectedElement;
			attributeTreeProject = attributeTreeFolder.getProject();
		}
		
		if (attributeTreeProject == null || !attributeTreeProject.isAccessible()) {
			return false;
		}
		
		if(attributeTreeFolder == null) {
			attributeTreeFolder = attributeTreeProject.getFolder("Statemachine/Tree/");
//			if(!attributeTreeFolder.exists()) {
//				try {
//					attributeTreeFolder.create(IResource.NONE, true, null);
//				} catch (CoreException e) {
//					return false;
//				}
//			}
		}
		
		attributeTreeFile = attributeTreeFolder.getFile(attributeTreeName + "." + ATTRIBUTE_TREE_EXTENSION);
//		if(!attributeTreeFile.exists()) {
//			try {
//				attributeTreeFile.create(new ByteArrayInputStream("test".getBytes()), IResource.NONE, null);
//			} catch (CoreException e) {
//				return false;
//			}
//		}
		return true;
	}

}
