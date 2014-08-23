/* Copyright 2014 Bastiaan Welmers */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class PreprocessJS {

	/**
	 * Character set for which all 256 bytes are valid.
	 * 
	 * ISO-8859-15 is, but, for instance, US-ASCII, UTF-8 or UTF-16 is not.
	 * 
	 * Since this program doesn't do anything with non-ascii characters
	 * except of passing it as-is, the encoding is only used to ensure
	 * no data is lost with InputStreamReader and OutputStreamReader.
	 */
	private static String CHARSET = "ISO-8859-15";
	
	private static BufferedWriter out = null;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		
		BufferedReader in = null;
		String file = null;
		for (int i = 0; i < args.length; i += 2) {
			if (args.length >= i) {
				if (args[i].equals("-i")) {
					file = args[i + 1];
				}
				if (args[i].equals("-o")) {
					try {
						out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[i + 1]), CHARSET));
					} catch (FileNotFoundException e) {
						System.err.println(String.format("Given output file not found: %s", args[i + 1]));
						System.exit(1);
					}
				}
			}
		}
		
		if (out == null) {
			// write to stdout
			out = new BufferedWriter(new OutputStreamWriter(System.out, CHARSET));
		}

		if (file == null) {
			// use stdin and cwd
			process(new InputStreamReader(System.in, CHARSET), System.getProperty("user.dir"));
		} else {
			process(file);
		}
		
		try {
			out.close();
		} catch (IOException e) {
			System.err.println(String.format("Error closing output stream: %s", e.getMessage()));
			System.exit(1);
		}
	}
	
	private static void process(String fileName) throws IOException {
		File f = new File(fileName);
		String cwd = f.getParent();
		InputStreamReader in = openFile(f);
		process(in, cwd);
		in.close();
	}
	
	private static void process(InputStreamReader in, String cwd) throws IOException {
		// read entire file into string buffer
		StringBuffer sbuff = new StringBuffer(1024);
		char[] buff = new char[1024];
		int n;
		while (-1 != (n = in.read(buff)))
			sbuff.append(buff, 0, n);
		
		// TODO
		// find matching preprocessing tags
		// and write them to out
		
		out.append(sbuff);
		
	}
	
	/**
	 * Open file
	 * 
	 * @param fileName
	 * @return input stream reader ready for reading the file.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	private static InputStreamReader openFile(File file) 
			throws UnsupportedEncodingException {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(file), CHARSET);
		} catch (FileNotFoundException e) {
			System.err.println(String.format("File not found: %s", file.getPath()));
			System.exit(1);
		}
		return in;
	}
	
	
}
