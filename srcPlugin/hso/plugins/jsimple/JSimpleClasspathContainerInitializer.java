package hso.plugins.jsimple;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class JSimpleClasspathContainerInitializer extends ClasspathContainerInitializer {

	public static final IPath CONTAINER_ID = new Path("hso.plugins.jsimple.requiredLibraries");

	@Override
	public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException {
		
	}

}
