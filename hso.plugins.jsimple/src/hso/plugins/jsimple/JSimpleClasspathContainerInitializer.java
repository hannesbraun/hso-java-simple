package hso.plugins.jsimple;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JSimpleClasspathContainerInitializer extends ClasspathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException {
		IProject project = javaProject.getProject();
		if (project.exists() && project.isOpen()) {
			JavaCore.setClasspathContainer(
					JSimpleClasspathContainer.CONTAINER_ID,
					new IJavaProject[] {javaProject},
					new IClasspathContainer[] { new JSimpleClasspathContainer() },
					null
			);
		}
	}

}
