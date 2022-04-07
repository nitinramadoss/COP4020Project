package edu.ufl.cise.plc.runtime.javaCompilerClassLoader;

/*  Class required for implementation of DynamicCompiler */

import java.io.IOException;
import java.security.SecureClassLoader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class InMemoryClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	
	InMemoryBytecodeObject byteCodeObject;
	
	public InMemoryClassFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}
	
	@Override
	public ClassLoader getClassLoader(Location location) {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				byte[] b = byteCodeObject.getBytes();
				return super.defineClass(name, b, 0, b.length);
			}
		};
	}
	
	public JavaFileObject getJavaFileForOutput(Location location, String name, Kind kind, FileObject sibling) throws IOException{
		byteCodeObject = new InMemoryBytecodeObject(name, kind);
		return byteCodeObject;
	}

}
