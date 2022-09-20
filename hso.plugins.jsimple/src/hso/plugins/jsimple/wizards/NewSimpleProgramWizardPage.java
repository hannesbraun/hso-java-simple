package hso.plugins.jsimple.wizards;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class NewSimpleProgramWizardPage extends WizardPage {
	private IJavaProject javaProject = null;
	
	private String containerPath = "";
	
	private String parentPackage = "";

	private Text programNameText;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewSimpleProgramWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("HSO Simple Program");
		setDescription("This wizard creates a new package containing a Java class with a main method.");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Program name:");

		programNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		programNameText.setLayoutData(gd);
		programNameText.addModifyListener(e -> dialogChanged());
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection ssel) {
			final Object obj = ssel.getFirstElement();
			if (obj instanceof IJavaProject javaProject) {
				processSelection(javaProject);
			} else if (obj instanceof IProject project) {
				try {
					if (project.hasNature(JavaCore.NATURE_ID)) {
						processSelection(JavaCore.create(project));
					}
				} catch (CoreException e) {
				}
			} else if (obj instanceof IPackageFragmentRoot packageFragmentRoot) {
				processSelection(packageFragmentRoot);
			} else if (obj instanceof IPackageFragment packageFragment) {
				processSelection(packageFragment);
			} else if (obj instanceof ICompilationUnit compilationUnit) {
				processSelection(compilationUnit);
			}
		}
		
		programNameText.setText("");
	}
	
	private void processSelection(IContainer container) {
		containerPath = container.getFullPath().toString();
	}
	
	private void processSelection(IResource resource) {
		if (resource instanceof IContainer container) {
			processSelection(container);
		} else {
			processSelection(resource.getParent());
		}
	}
	
	private void processSelection(IPackageFragmentRoot packageFragmentRoot) {
		try {
			IResource resource = packageFragmentRoot.getUnderlyingResource();
			processSelection(resource);
		} catch (JavaModelException e) {
		}
		javaProject = packageFragmentRoot.getJavaProject();
	}
	
	private void processSelection(IJavaProject javaProject) {
		try {
			List<IPackageFragmentRoot> packageFragmentRoots = Arrays.stream(javaProject.getPackageFragmentRoots()).filter(pfr -> {
				try {
					return pfr.getUnderlyingResource() != null;
				} catch (JavaModelException e) {
					return false;
				}
			}).collect(Collectors.toList());
			
			if (packageFragmentRoots.size() > 0) {
				processSelection(packageFragmentRoots.get(0));
			}
		} catch (JavaModelException e) {
		}
	}
	
	private void processSelection(IPackageFragment packageFragment) {
		IJavaElement parent = packageFragment.getParent();
		do {
			if (parent instanceof IPackageFragmentRoot packageFragmentRoot) {
				processSelection(packageFragmentRoot);
				return;
			}
			parent = parent.getParent();
		} while (parent != null);
		
		processSelection(packageFragment.getJavaProject());
	}
	
	private void processSelection(ICompilationUnit compilationUnit) {
		IJavaElement parent = compilationUnit.getParent();
		do {
			if (parent instanceof IPackageFragmentRoot packageFragmentRoot) {
				processSelection(packageFragmentRoot);
				return;
			}
			parent = parent.getParent();
		} while (parent != null);
		
		processSelection(compilationUnit.getJavaProject());
	}
	
	private static boolean isValidJavaIdentifier(String id) {
		if (id == null || id.isBlank()) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(id.charAt(0))) {
			return false;
		}
		return id.chars().skip(1).allMatch(c -> Character.isJavaIdentifierPart(c));
		
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		final String fileName = getProgramName();

		if (getContainerName().length() == 0) {
			updateStatus("Program path is unkown");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("Program name must be specified");
			return;
		}
		if (!isValidJavaIdentifier(fileName)) {
			updateStatus("Program name is not a valid java identifier");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public String getContainerName() {
		return containerPath;
	}

	public String getParentPackage() {
		return parentPackage;
	}
	
	public String getProgramName() {
		return programNameText.getText();
	}
}
