package hso.plugins.jsimple;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

public class JSimpleClasspathContainer implements IClasspathContainer {

	public static final IPath CONTAINER_ID = new Path("hso.plugins.jsimple.requiredLibraries");

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		return new IClasspathEntry[] { JavaCore.newLibraryEntry(null, null, null) }; // TODO first arg is IPath
	}

	@Override
	public String getDescription() {
		return "JSimple Library";
	}

	@Override
	public int getKind() {
		return K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return CONTAINER_ID;
	}

}
