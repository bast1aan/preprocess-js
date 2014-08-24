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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final String CHARSET = "ISO-8859-15";
	
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
			out.append(process(new InputStreamReader(System.in, CHARSET), System.getProperty("user.dir")));
		} else {
			out.append(process(file));
		}
		
		try {
			out.close();
		} catch (IOException e) {
			System.err.println(String.format("Error closing output stream: %s", e.getMessage()));
			System.exit(1);
		}
	}
	
	private static CharSequence process(String fileName) throws IOException {
		File f = new File(fileName);
		return process(f);
	}
	
	private static CharSequence process(File file) throws IOException {
		String cwd = file.getParent();
		InputStreamReader in = openFile(file);
		CharSequence s = process(in, cwd);
		in.close();
		return s;
	}
	
	private static CharSequence process(InputStreamReader in, String cwd) throws IOException {
		CharSequence sbuff = readStream(in);
		
		StringBuffer afterInclude = new StringBuffer();
		
		// find matching include tags
		Pattern p = Pattern.compile("\"include\\s(\\S+?)\\s*\";");
		Matcher m = p.matcher(sbuff);
		int prev = 0;
		while(m.find()) {
			afterInclude.append(sbuff.subSequence(prev, m.start()));
			afterInclude.append(process(new File(cwd, m.group(1))));
			prev = m.end();
		}
		afterInclude.append(sbuff.subSequence(prev, sbuff.length()));
		
		//System.err.println(String.format("CWD: %s", cwd));
		return afterInclude;
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
	
	/**
	 * Read entire stream into CharSequence.
	 * 
	 * @param in input stream
	 * @return result
	 * @throws IOException
	 */
	private static CharSequence readStream(InputStreamReader in) throws IOException {
		
		StringBuffer sbuff = new StringBuffer(1024);
		char[] buff = new char[1024];
		int n;
		while (-1 != (n = in.read(buff)))
			sbuff.append(buff, 0, n);
		
		return sbuff;
	}
	
	
}
