package edu.ufl.cise.plc.runtime;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class FileURLIO {

	/**
	 * Reads the image from the indicated URL or filename. If the given source
	 * is not a valid URL, it is assumed to be a filename.
	 * 
	 * @param source
	 * @return BufferedImage 
	 */
	public static BufferedImage readImage(String source) {
		BufferedImage image;
		try {
			URL url = new URL(source);
			image = readFromURL(url);
		} catch (MalformedURLException e) {// wasn't a URL, maybe it is a file
			image = readFromFile(source);
		}
		return image;
	}
	
	/**
	 * Reads the image from the indicated URL or filename. If the given source
	 * is not a valid URL, it assumes it is a file.
	 * 
	 * The image is resized to the size indicated by w and h, or kept in original size if w or h is null.
	 * 
	 * @param source
	 *            String with source or filename on local filesystem.
	 * @param w
	 *            Desired width of image, or null
	 * @param h
	 *            Desired height of image, or null
	 * @return BufferedImage representing the indicated image.
	 */
	public static BufferedImage readImage(String source, Integer w, Integer h) {
		BufferedImage image;
		try {
			URL url = new URL(source);
			image = readFromURL(url);

		} catch (MalformedURLException e) {// wasn't a URL, maybe it is a file
			image = readFromFile(source);
		}
		if (w==null || h == null) {
			return image;
		}
		return ImageOps.resize(image, w, h);
	}
	
	/**
	 * Reads and returns the image at the given URL
	 * 
	 * Throws a PLCRuntimeException wrapped around the original exception if the operation does not succeed.
	 * 
	 * @param url
	 * @return BufferedImage representing the indicated image
	 */
	static BufferedImage readFromURL(URL url) {
		try {
			System.err.println("reading image from url:  " + url);
			return ImageIO.read(url);
		} catch (IOException e) {
			throw new PLCRuntimeException(e);
		}
	}
	
	/**
	 * Reads and returns the image from the given file
	 * 
	 * Throws a PLCRuntimeException if this fails
	 * 
	 * @param filename
	 * @return
	 */
	static BufferedImage readFromFile(String filename) {
		File f = new File(filename);
		BufferedImage bi;
		try {
			bi = ImageIO.read(f);
		} catch (IOException e) {
			throw new PLCRuntimeException(e.getMessage() + " " + filename, e);
		}
		return bi;
	}
	

	
	
	/**
	 * Writes the given image to a file on the local system indicated by the
	 * given filename.
	 * 
	 * @param image
	 * @param filename
	 */
	public static void writeImage(BufferedImage image, String filename) {
		Path path = Paths.get(filename + ".jpeg");
		try {
			File f = path.toFile();
			boolean success = ImageIO.write(image, "jpeg", f);
			if (!success) {
				System.err.println("success = " + success + " for image file  " + filename);
			}
			System.err.println("writing image to file " + path.toAbsolutePath() + " given filename= "
					+  filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PLCRuntimeException(e);
		}
	}
	

	/**
	 * Writes the given value to a file on the local file system
	 * 
	 * @param value
	 * @param filename
	 */
	public static void writeValue(Serializable value, String filename) {
		ObjectOutputStream oos = getObjectOutputStream(filename);
		try {
			oos.writeObject(value);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new PLCRuntimeException(e);
		}
	}	
	
	static HashMap<String, ObjectInputStream> inputFiles = new HashMap<>();
	static Map<String, ObjectOutputStream> outputFiles = new HashMap<>();
	
	static ObjectOutputStream getObjectOutputStream(String filename) {
		ObjectOutputStream oos = outputFiles.get(filename);
		if (oos == null) {
			Path path = Paths.get(filename);
			try {
				FileOutputStream fos = new FileOutputStream(path.toFile());			
				oos = new ObjectOutputStream(fos);
				outputFiles.put(filename,oos);
			}
			catch (IOException e) {
				throw new PLCRuntimeException(e);
			}
		}
		return oos;		
	}
	
	public static void closeFiles() {
		try {
		for (ObjectInputStream ois: inputFiles.values()) ois.close();
		for (ObjectOutputStream oos: outputFiles.values()) oos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ObjectInputStream getObjectInputStream(String filename) {
		ObjectInputStream ois = inputFiles.get(filename);
		if (ois == null) {
			Path path = Paths.get(filename);
			try {
				FileInputStream fis = new FileInputStream(path.toFile());			
				ois = new ObjectInputStream(fis);
				inputFiles.put(filename,  ois);
			}
			catch (IOException e) {
				throw new PLCRuntimeException(e);
			}
		}
		return ois;
	}
	public static Object readValueFromFile(String filename) {
		ObjectInputStream ois = getObjectInputStream(filename);
		Object obj=null;
		try {
			obj = ois.readObject();
		} catch (IOException | ClassNotFoundException  e) {			
			e.printStackTrace();
		}
		return obj;
	}
		
}
