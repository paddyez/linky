package net.psammead.linky.util;

import java.io.File;

public class Data {
	private static final File APP_DIR	= new File(System.getProperty("user.dir"));
	
	/** return a File relative to the installation path or an absoulte file if the path is absolute */
	public static File appFile(String path) { 
		File	file	= new File(path);
		if (file.isAbsolute())	return file;
		else					return new File(APP_DIR, path); 
	}
}
