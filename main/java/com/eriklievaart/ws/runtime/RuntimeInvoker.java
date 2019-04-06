package com.eriklievaart.ws.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.eriklievaart.ws.toolkit.io.IORuntimeException;

/**
 * Invokes {@link RuntimeCommand} objects on the native command line.
 *
 * @author Erik Lievaart
 */
public class RuntimeInvoker {

	private static RuntimeStreams streams = new RuntimeStreams();

	private RuntimeInvoker() {
	}

	/**
	 * Invokes the supplied {@link RuntimeCommand}.
	 */
	public static boolean invoke(final RuntimeCommand command) {
		return invoke(command, null);
	}

	/**
	 * Invokes the supplied {@link RuntimeCommand} in the specified directory.
	 *
	 * @return true on success, false if the exit code was not 0 or an Exception occurred.
	 */
	public static boolean invoke(final RuntimeCommand command, final File dir) {
		return invoke(command, dir, streams);
	}

	public static boolean invoke(RuntimeCommand command, File dir, RuntimeStreams output) {
		try {
			int exit = execute(command, dir, output);
			return exit == 0;

		} catch (IORuntimeException ioe) {
			output.thrown(ioe);
			return false;
		}
	}

	private static int execute(final RuntimeCommand command, final File dir, RuntimeStreams output) {
		try {
			Process process = Runtime.getRuntime().exec(command.cmd(), null, dir);

			// Reading the streams from the Process prevents the task from hanging.
			dumpInNewThread(process.getErrorStream(), output.getErrorOutput());
			dumpInCurrentThread(process.getInputStream(), output.getNormalOutput());
			return process.waitFor();

		} catch (InterruptedException | IOException e) {
			throw new IORuntimeException(e);
		}
	}

	/**
	 * Read the stream in a new Thread.
	 */
	private static void dumpInNewThread(final InputStream input, final RuntimeOutput output) {
		new Thread(new RuntimeRunnable(input, output)).start();
	}

	/**
	 * Read the stream in the currently active Thread.
	 */
	private static void dumpInCurrentThread(final InputStream input, final RuntimeOutput output) {
		new RuntimeRunnable(input, output).run();
	}
}
