package autogenerate.code.plugin.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;

public class IOUtils {
	private static Log LOG = LogFactory.getLog(IOUtils.class);

	// private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static BufferedReader createBufferedReader(InputStream stream, String name) throws IOException {
		if (stream == null) {
			return null;
		}
		if (name.endsWith(".gz")) {
			stream = new GZIPInputStream(stream);
			name = name.substring(0, name.length() - ".gz".length());
		}

		BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		return r;
	}

	public static BufferedReader createBufferedReader(File file) throws IOException {
		String name = file.getName();
		InputStream in = new FileInputStream(file);
		if (name.endsWith(".tmp")) {
			name = name.substring(0, name.length() - ".tmp".length());
		}
		if (name.endsWith(".gz")) {
			in = new GZIPInputStream(in);
			name = name.substring(0, name.length() - ".gz".length());
		}

		BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		return r;
	}

	public static InputStream createInputStream(File file) throws IOException {
		return createInputStream(file.getName());
	}

	public static InputStream createInputStream(String fileName) throws IOException {
		InputStream in = new FileInputStream(fileName);
		String name = fileName;
		if (name.endsWith(".tmp")) {
			name = name.substring(0, name.length() - ".tmp".length());
		}
		if (name.endsWith(".gz")) {
			in = new GZIPInputStream(in);
			name = name.substring(0, name.length() - ".gz".length());
		}

		return in;
	}

	public static Writer createWriter(File file) throws IOException {
		File parent = file.getParentFile();
		if (parent != null) {
			boolean isSuccess = parent.mkdirs();
			if (!isSuccess && !parent.exists()) {
				LOG.error("Can't create dir:" + parent.getPath());
			}
		}
		String name = file.getName();
		OutputStream out = new FileOutputStream(file);
		out = new BufferedOutputStream(out);
		if (name.endsWith(".tmp")) {
			name = name.substring(0, name.length() - ".tmp".length());
		}
		if (name.endsWith(".gz")) {
			out = new GZIPOutputStream(out);
			name = name.substring(0, name.length() - ".gz".length());
		}
//		if (name.endsWith(".lz4")) {
//			out = new LZ4BlockOutputStream(out);
//			name = name.substring(0, name.length() - ".lz4".length());
//		}
		Writer w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		return w;
	}

	public static OutputStream createOutputStream(String fileName) throws IOException {
		return createOutputStream(new File(fileName));
	}

	public static OutputStream createOutputStream(File file) throws IOException {
		File parent = file.getParentFile();
		if (parent != null) {
			boolean isSuccess = parent.mkdirs();
			if (!isSuccess && !parent.exists()) {
				LOG.error("Can't create dir:" + parent.getPath());
			}
		}
		String name = file.getName();
		OutputStream out = new FileOutputStream(file);
		out = new BufferedOutputStream(out);
		if (name.endsWith(".tmp")) {
			name = name.substring(0, name.length() - ".tmp".length());
		}
		if (name.endsWith(".gz")) {
			out = new GZIPOutputStream(out);
			name = name.substring(0, name.length() - ".gz".length());
		}
//		if (name.endsWith(".lz4")) {
//			out = new LZ4BlockOutputStream(out);
//			name = name.substring(0, name.length() - ".lz4".length());
//		}
		return out;
	}

	public static InputStream getConfigStream(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		return getConfigStream(stream);
	}

	public static BufferedReader getConfigReader(String file) {
		try {
			InputStream stream = new FileInputStream(file);
			return getConfigReader(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("", e);
		}
	}

//	public static BufferedReader getConfigReaderByName(String name) {
//		InputStream stream = getConfigStream(name);
//		if (stream == null) {
//			return null;
//		}
//		return getConfigReader(stream);
//	}

	public static BufferedReader getConfigReader(File file) {
		try {
			InputStream stream = new FileInputStream(file);
			return getConfigReader(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("", e);
		}
	}

	public static BufferedReader getConfigReader(InputStream stream) {
		try {
			if (!stream.markSupported()) {
				stream = new BufferedInputStream(stream);
			}
			stream.mark(4);
			byte[] head = new byte[4];
			int length = stream.read(head);
			if (length < 0) {
				return new BufferedReader(new InputStreamReader(stream));
			}
			stream.reset();
			BufferedReader bufferReader = null;
			bufferReader = getUTFReader(head, stream);
			if (bufferReader != null) {
				return bufferReader;
			}
			bufferReader = getZipReader(head, stream);
//            if(bufferReader != null)
//            {
//                return bufferReader;
//            }
//            bufferReader = getSecretKeyReader(head,stream);
			if (bufferReader != null) {
				return bufferReader;
			} else {
				return new BufferedReader(new InputStreamReader(stream));
			}
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	private static InputStream getConfigStream(InputStream stream) {
		try {
			if (!stream.markSupported()) {
				stream = new BufferedInputStream(stream);
			}
			stream.mark(4);
			byte[] head = new byte[4];
			int length = stream.read(head);
			if (length < 0) {
				return stream;
			}
			stream.reset();
			if (head[0] == (byte) 0x50 && head[1] == (byte) 0x4B && head[2] == 0x03 && head[3] == 0x04) {
				ZipInputStream zipStream = new ZipInputStream(stream);
				zipStream.getNextEntry();
				return getConfigStream(zipStream);
			} else if (head[0] == (byte) 0x1F && head[1] == (byte) 0x8B && head[2] == (byte) 0x08) {
				stream = new GZIPInputStream(stream);
				return getConfigStream(stream);
			} else {
				return stream;
			}
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	/**
	 * 璇诲帇缂╁寘
	 * 
	 * @param head
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static BufferedReader getZipReader(byte[] head, InputStream stream) throws IOException {
		if (head[0] == (byte) 0x50 && head[1] == (byte) 0x4B && head[2] == 0x03 && head[3] == 0x04) {
			ZipInputStream zipStream = new ZipInputStream(stream);
			zipStream.getNextEntry();
			return getConfigReader(zipStream);
		} else if (head[0] == (byte) 0x1F && head[1] == (byte) 0x8B && head[2] == (byte) 0x08) {
			stream = new GZIPInputStream(stream);
			return getConfigReader(stream);
		} else {
			return null;
		}
	}

	private static BufferedReader getUTFReader(byte[] head, InputStream stream) throws IOException {
		if (head[0] == (byte) 0xFF && head[1] == (byte) 0xFE) {
			stream.skip(2);
			return new BufferedReader(new InputStreamReader(stream, "UTF-16LE"));
		} else if (head[0] == (byte) 0xFE && head[1] == (byte) 0xFF) {
			stream.skip(2);
			return new BufferedReader(new InputStreamReader(stream, "UTF-16BE"));
		} else if (head[0] == (byte) 0xEF && head[1] == (byte) 0xBB && head[2] == (byte) 0xBF) {
			stream.skip(3);
			return new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		} else {
			return null;
		}
	}

	public static void close(InputStream x) {
		if (x == null)
			return;
		try {
			x.close();
		} catch (Exception e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage(), e);
		}
	}

	public static void close(OutputStream x) {
		if (x == null)
			return;
		try {
			x.close();
		} catch (Exception e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage(), e);
		}
	}

	public static void close(Reader x) {
		if (x == null)
			return;
		try {
			x.close();
		} catch (Exception e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage(), e);
		}
	}

	public static void close(URLConnection x) {
		if (x == null)
			return;
		try {
			x.getInputStream().close();
		} catch (Exception _) {
		}
		try {
			x.getOutputStream().close();
		} catch (Exception _) {
		}
	}

	public static void close(Writer x) {
		if (x == null)
			return;
		try {
			x.close();
		} catch (Exception e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage(), e);
		}
	}

	public static void close(RandomAccessFile x) {
		if (x == null)
			return;
		try {
			x.close();
		} catch (Exception e) {
			if (LOG.isErrorEnabled())
				LOG.error(e.getMessage(), e);
		}
	}

	public static String readAll(Reader reader) throws IOException {
		int blockSize = 1024;
		char[] block = new char[blockSize];

		StringBuilder buf = new StringBuilder();
		boolean end = false;
		while (!end) {
			int len;
			do
				if ((len = reader.read(block)) == -1) {
					end = true;
				}
			while (len == 0);
			buf.append(block, 0, len);
		}

		return buf.toString();
	}

	public static String readAll(File file, String encoding) throws IOException {
		InputStreamReader reader = null;
		try {
			InputStream in = new FileInputStream(file);
			reader = new InputStreamReader(in, encoding);
			String str = readAll(reader);

			return str;
		} finally {
			close(reader);
		}
	}

	public static void copy(File srcFile, File destFile) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			copy(in, out);
		} finally {
			close(in);
			close(out);
		}
	}

	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > 2147483647L)
			return -1;

		return (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void copyFileToPath(File source, File target) {
		File tarpath = new File(target, source.getName());
		if (source.isDirectory()) {
			tarpath.mkdir();
			File[] dir = source.listFiles();
			for (int i = 0; i < dir.length; i++) {
				copyFileToPath(dir[i], tarpath);
			}
		} else {
			try {
				InputStream is = new FileInputStream(source);
				OutputStream os = new FileOutputStream(tarpath);
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					os.write(buf, 0, len);
				}
				is.close();
				os.close();
			} catch (FileNotFoundException fe) {
				fe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static File write(File file, String source) throws IOException {
		if (file.exists()) {
			file.delete();
		}
		// File file = new File(dir, this.name);
		OutputStreamWriter writer = null;

		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			writer.write(source);
		} finally {
			close(writer);
		}

		return file;
	}

	public static boolean matches(String pattern, String file) {
		if (file != null)
			file = file.replace('\\', '/');
		String exprPattern = convertToExprPattern(pattern);
		return Pattern.matches(exprPattern, file);
	}

	public static boolean matches(Pattern pattern, String file) {
		if (file != null)
			file = file.replace('\\', '/');
		return pattern.matcher(file).matches();
	}

	public static String convertToExprPattern(String filePattern) {
		char[] charArray = filePattern.toCharArray();

		StringBuffer buf = new StringBuffer();

		buf.append("\\Q");
		for (int i = 0; i < charArray.length; ++i) {
			char ch = charArray[i];
			if (ch == '*') {
				buf.append("\\E");
				buf.append(".*");
				buf.append("\\Q");
			} else {
				buf.append(ch);
			}
		}

		buf.append("\\E");

		return buf.toString();
	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			for (File item : file.listFiles())
				delete(item);

		}

		boolean success = file.delete();
		if (!(success))
			throw new IOException("delete file '" + file.getPath() + "' failed");
	}

	/**
	 *  
	 * @param file
	 * @param notDeleteFiles
	 * @throws IOException
	 */
	public static void delete(File file, List<String> notDeleteFiles) throws IOException {
		if (file.isDirectory()) {
			String fileName = "";
			for (File item : file.listFiles()) {
				fileName = item.getName();
				if (!notDeleteFiles.contains(fileName)) {
					delete(item, notDeleteFiles);
				}
			}
		}

		boolean success = file.delete();
		if (!(success))
			throw new IOException("delete file '" + file.getPath() + "' failed");
	}

	public static void fixedUTF8File(File file) throws IOException {
		int length = (int) file.length();

		FileInputStream in = null;

		byte[] bytes = new byte[length];
		try {
			in = new FileInputStream(file);
			in.read(bytes, 0, 3);

			if ((bytes[0] == -18) && (bytes[1] == -69) && (bytes[2] == -1)) {
				return;
			}
			in.read(bytes, 3, length - 3);
		} finally {
			close(in);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			writeUTF8FileHead(out);
			out.write(bytes);
		} finally {
			close(out);
		}
	}

	public static byte[] toByteArray(InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[4096];
		int read = 0;
		while (read != -1) {
			read = stream.read(buffer);
			if (read > 0) {
				baos.write(buffer, 0, read);
			}
		}

		return baos.toByteArray();
	}

	public static void writeUTF8FileHead(FileOutputStream out) throws IOException {
		out.write(239);
		out.write(187);
		out.write(191);
	}

	public static void refreshSelectionProject(IStructuredSelection selection) {
		IAdaptable element = (IAdaptable) selection.getFirstElement();
		IResource resource = (IResource) element.getAdapter(IResource.class);
		refreshProject(resource.getProject());
	}

	public static void refreshProject(final IProject project) {

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
			if (LOG.isErrorEnabled()) {
				LOG.error("refresh error", e);
			}
		}
//        IWorkspaceRunnable operation = new IWorkspaceRunnable() {
//            public void run(IProgressMonitor monitor) throws CoreException {
//                project.refreshLocal(2, new SubProgressMonitor(monitor, 1));
//            }
//        };
//        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//
//        try {
//            (new ProgressMonitorDialog(shell)).run(true, true, new WorkbenchRunnableAdapter(operation));
//        } catch (InvocationTargetException var4) {
//            if (LOG.isErrorEnabled()) {
//                LOG.error("refresh error", var4);
//            }
//        } catch (InterruptedException var5) {
//            if (LOG.isErrorEnabled()) {
//                LOG.error("refresh error", var5);
//            }
//        }

	}

	// 读取文件内容
	public static String readFile(File file) {
		StringBuilder result = new StringBuilder();
		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "UTF-8");// 构造一个BufferedReader类来读取文件
			br = new BufferedReader(isr);

			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();

		}finally {
			close(br);
			close(fis);
			close(isr);
		}
		return result.toString();
	}

}