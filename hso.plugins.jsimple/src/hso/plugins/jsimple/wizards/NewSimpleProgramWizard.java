package hso.plugins.jsimple.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import hso.plugins.jsimple.JSimpleClasspathContainer;

/**
 * This is a wizard to create a new "simple program". Its role is
 * to create a new Java class residing in a new package.
 * The new package will be created at the source root directory.
 * The name for the package will be the supplied name with the first
 * letter lowercased. The class gets the same name with the first letter
 * uppercased and "Main" appended. The editor for the class will be opened
 * automatically after creation.
 */
public class NewSimpleProgramWizard extends Wizard implements INewWizard {
	private NewSimpleProgramWizardPage page;
	private ISelection selection;

	public NewSimpleProgramWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page = new NewSimpleProgramWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when the 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final IJavaProject javaProject = page.getJavaProject();
		final String containerName = page.getContainerName();
		final String parentPackage = page.getParentPackage();
		final String fileName = page.getProgramName();
		IRunnableWithProgress op = monitor -> {
			try {
				doFinish(javaProject, containerName, parentPackage, fileName, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will add the JSimple library to the classpath,
	 * create the package as well as the class, and open
	 * the editor on the newly created class.
	 */
	private void doFinish(IJavaProject javaProject, String containerName, String parentPackage, String programName, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating simple program " + programName, 4);
		
		// Add hso library to classpath
		modifyClasspath(javaProject, monitor);
		monitor.worked(1);
		
		// Get container
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw new CoreException(Status.error("Container \"" + containerName + "\" does not exist."));
		}
		IContainer container = (IContainer) resource;
		
		// Determine package and class name
		final String packageName = Character.toLowerCase(programName.charAt(0)) + programName.substring(1);
		final String className = Character.toUpperCase(programName.charAt(0)) + programName.substring(1) + "Main";
		
		// Create package
		final IFolder simplePackage = container.getFolder(new Path(packageName));
		if (simplePackage.exists()) {
			throw new CoreException(Status.error("Program \"" + programName + "\" already exists."));
		} else {
			simplePackage.create(true, true, monitor);
		}
		monitor.worked(1);
		
		// Create template class
		final Path pathClassFile = new Path(className + ".java");
		final IFile file = simplePackage.getFile(pathClassFile);
		try {
			final InputStream stream = openContentStream(parentPackage, packageName, className);
			if (file.exists()) {
				// file.setContents(stream, true, true, monitor);
				throw new CoreException(Status.error("File \"" + pathClassFile + "\" already exists."));
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
			throw new CoreException(Status.error("IO Exception while creating file \"" + pathClassFile + "\": " + e.getMessage()));
		}
		monitor.worked(1);
		
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * Adds the JSimple library to the classpath of the Java project in case it's not already added.
	 */
	private void modifyClasspath(IJavaProject javaProject, IProgressMonitor monitor) {
		try {
			IClasspathEntry[] oldClasspath = javaProject.getRawClasspath();
			boolean libFound = false;
			for (IClasspathEntry entry : oldClasspath) {
				if (entry.getPath().equals(JSimpleClasspathContainer.CONTAINER_ID)) {
					libFound = true;
				}
			}
			if (!libFound) {
				IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length + 1];
				System.arraycopy(oldClasspath, 0, newClasspath, 0, oldClasspath.length);
				newClasspath[newClasspath.length - 1] = JavaCore.newContainerEntry(JSimpleClasspathContainer.CONTAINER_ID);
				javaProject.setRawClasspath(newClasspath, true, monitor);
			}
		} catch (JavaModelException e) {
		}
		
	}

	/**
	 * @return the input stream containing the source code of the generated Java class.
	 */
	private InputStream openContentStream(String parentPackage, String packageName, String className) {
		StringBuilder contents = new StringBuilder();
		contents.append("package ");
		if (!parentPackage.isEmpty()) {
			contents.append(parentPackage + ".");
		}
		contents.append(packageName + ";");
		contents.append("\n\n");
		contents.append("import hso.*;");
		contents.append("\n\n");
		contents.append("public class " + className + " {");
		contents.append("\n\n");
		contents.append("	public static void main(String[] args) {\n");
		contents.append("	}\n");
		contents.append("}\n");

		return new ByteArrayInputStream(contents.toString().getBytes());
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
