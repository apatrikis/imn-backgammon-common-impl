package net.ichmags.backgammon.reflection;

import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ichmags.backgammon.generic.Visitor;

/**
 * @see <a href="http://stackoverflow.com/questions/3222638/get-all-of-the-classes-in-the-classpath">stackoverflow</a>
 * @author <a href="http://stackoverflow.com/users/200224/andy">Andy</a>
 */
public class ClassPathTraverser {

	private static Logger LOG = LoggerFactory.getLogger(ClassPathTraverser.class);
	
	private boolean includeSystemClasses;
	
	public ClassPathTraverser(boolean includeSystemClasses) {
		this.includeSystemClasses = includeSystemClasses;
	}
	
	public void findClasses(Visitor<String> visitor) {
		if(includeSystemClasses) {
			findSystemClasses(visitor);
		}
		findClasspathClasses(visitor);
    }
	
	private void findSystemClasses(Visitor<String> visitor) {
        String javaHome = System.getProperty("java.home");
        if(javaHome != null) {
	        File file = new File(javaHome + File.separator + "lib");
	        if (file.exists()) {
	            findClasses(file, file, true, visitor);
	        }
        } else {
        	LOG.error("Can not search system class: proptery 'java.home' not set.");
        }
	}
	
	private void findClasspathClasses(Visitor<String> visitor) {
		String classpath = System.getProperty("java.class.path");
		String[] paths = classpath.split(System.getProperty("path.separator"));
		
		for (String path : paths) {
			File file = new File(path);
			if (file.exists()) {
				findClasses(file, file, true, visitor);
			}
		}
	}

    private boolean findClasses(File root, File file, boolean includeJars, Visitor<String> visitor) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                if (!findClasses(root, child, includeJars, visitor)) {
                    return false;
                }
            }
        } else {
            if (file.getName().toLowerCase().endsWith(".jar") && includeJars) {
                JarFile jar = null;
                try {
                    jar = new JarFile(file);
                } catch (Exception ex) {

                }
                if (jar != null) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        int extIndex = name.lastIndexOf(".class");
                        if (extIndex > 0) {
                            if (!visitor.visit(name.substring(0, extIndex).replace("/", "."))) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if (file.getName().toLowerCase().endsWith(".class")) {
                if (!visitor.visit(createClassName(root, file))) {
                    return false;
                }
            }
        }

        return true;
    }

    private String createClassName(File root, File file) {
        StringBuffer sb = new StringBuffer();
        String fileName = file.getName();
        sb.append(fileName.substring(0, fileName.lastIndexOf(".class")));
        file = file.getParentFile();
        while (file != null && !file.equals(root)) {
            sb.insert(0, '.').insert(0, file.getName());
            file = file.getParentFile();
        }
        return sb.toString();
    }
}
