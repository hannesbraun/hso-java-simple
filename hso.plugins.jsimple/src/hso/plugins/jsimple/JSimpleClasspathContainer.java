package hso.plugins.jsimple;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class JSimpleClasspathContainer implements IClasspathContainer {

	public static final IPath CONTAINER_ID = new Path("hso.plugins.jsimple.requiredLibraries");

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		final Bundle bundle = Platform.getBundle("hso.jsimple");
		String pathStr = bundle.adapt(File.class).getAbsolutePath();

		// Use the bin directory if we're running the plugin "directly" in a second Eclipse instance. (no JAR)
		// Is this correct way of doing it? Not sure.
		pathStr = pathStr.replaceAll("/$", "");
		if (!pathStr.endsWith(".jar")) {
			pathStr += System.getProperty("file.separator") + "bin";
		}

		IPath path = new Path(pathStr);
		return new IClasspathEntry[] { JavaCore.newLibraryEntry(path, null, null) };
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
