/**
 * A FileObject used to represent Java source provided in a String.  Required for implementation of DynamicCompiler
 * 
 * @Adopted from https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/tools/JavaCompiler.html
 *
 */

package edu.ufl.cise.plc.runtime.javaCompilerClassLoader;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;


public class StringJavaFileObject extends SimpleJavaFileObject {
	
	final String code;  //The string containing the source code
	
	/**
	 * @param name     name of class
	 * @param code     source code for class
	 */
	public StringJavaFileObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                Kind.SOURCE);
          this.code = code;		
	}


    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
	


