package threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import commands.OnSystemExecutor;
import engine.LogLevel;
import engine.sys;

/**
 * Class to assist SWT with stream handling. Has no real application otherwise.
 * Flow diagram: streamSource --(thread)--> streamSourcePiped --(direct)-->
 * stream
 * 
 * (I wanted to write sth. here and forgot it)
 * 
 * @apiNote Most public methods of this class will return a boolean value, which
 *          will be true, if the method succeeded and false otherwise (e.g.
 *          after an Exception).
 * @implNote There seems to be a problem with Piped*Stream, so if long delays are encountered
 * reading / writing, the following resource might be helpful:
 * https://stackoverflow.com/questions/28617175/did-i-find-a-bug-in-java-io-pipedinputstream
 */

public class ShellIOStream {
	private ByteArrayOutputStream streamSource;
	private PipedOutputStream streamSourcePiped;
	private PipedInputStream stream;

	private Thread pipeTransferThread; // Transfers data from streamSource to streamSourcePiped
	private Thread externalCopyThread; // Copies data to an external stream in blockingCopyTo()

	private BufferedWriter streamSourceWriter;
	private BufferedReader streamReader;

	// Keeps track of how many times the streams were reinitialized
	private int totalRegenerations;
	private static final int MAX_REGENERATIONS = 100;

	protected ShellIOStream() {
		totalRegenerations = 0;
		regenerate();
	}

	private boolean regenerate() {
		if (totalRegenerations >= 1) {
			// Not the first time
			cleanup();
			sys.log("IOSTREAM", LogLevel.WARN, "Regenerating ShellIOStream due to previous error.");
			sys.log("IOSTREAM", LogLevel.WARN, "Count: " + totalRegenerations + "/" + MAX_REGENERATIONS);
		}
		if (totalRegenerations >= MAX_REGENERATIONS) {
			sys.log("IOSTREAM", LogLevel.CRIT, "ShellIOStream " + this + " regenerated too often.");
			return false;
		}
		totalRegenerations++;

		streamSource = new ByteArrayOutputStream();
		streamSourcePiped = new PipedOutputStream();
		try {
			stream = new PipedInputStream();
			stream.connect(streamSourcePiped);
		} catch (IOException ioe) {
			sys.log("IOSTREAM", LogLevel.NONCRIT, "Piping streams failed.");
		}

		pipeTransferThread = new Thread(() -> {
			while (!WatchdogThread.shutdownSignal) {
				try {
					synchronized (streamSource) {
						streamSource.wait(100);
						streamSource.writeTo(streamSourcePiped);
					}
				} catch (IOException ioe) {
					sys.log("IOSTREAM", LogLevel.NONCRIT, "Transferring byte stream to pipe failed.");
					regenerate();
					break;
				} catch (InterruptedException ie) {
					sys.log("IOSTREAM", LogLevel.NONCRIT, "Waiting for data interrupted.");
				}
			}
			cleanup();
		});
		pipeTransferThread.start();

		streamSourceWriter = new BufferedWriter(new OutputStreamWriter(streamSource));
		streamReader = new BufferedReader(new InputStreamReader(stream));
		return true;
	}

	private void cleanup() {
		sys.log("IOSTREAM", LogLevel.DEBUG, "Closing streams...");
		try {
			streamSource.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try {
			streamSourcePiped.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		try {
			stream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		sys.log("IOSTREAM", LogLevel.DEBUG, "Closing streams done.");
	}

	public boolean write(String newData) {
		try {
			synchronized (streamSource) {
				sys.log("IOSTREAM", LogLevel.DEBUG, "Appending data to streamSource.");
				streamSourceWriter.append(newData);
				streamSource.notifyAll();
				return true;
			}
		} catch (IOException ioe) {
			sys.log("IOSTREAM", LogLevel.ERR, "Writing data to stream source failed.");
			regenerate();
			return false;
		}
	}

	/**
	 * Clears previous contents of stream (by skipping all available bytes)
	 */
	public boolean clearStreamsData() {
		try {
			stream.skipNBytes(stream.available());
			return true;
		} catch (IOException ioe) {
			sys.log("IOSTREAM", LogLevel.ERR, "Skipping bytes in stream failed.");
			regenerate();
			return false;
		}
	}

	/**
	 * Reads the next line from stream.
	 * 
	 * @return The next line *including* a line break at the end (\n), or null if an
	 *         Exception occurred. Note that this method blocks until a newline is
	 *         available or an Exception is raised.
	 */
	public String readLine() {
		try {
			return streamReader.readLine();
		} catch (IOException ioe) {
			sys.log("IOSTREAM", LogLevel.ERR, "Reading line from stream failed.");
			regenerate();
			return null;
		}
	}

	/**
	 * Continuously transfers the contents of the internal stream to
	 * destinationStream. This method blocks until either stream has ended or an
	 * IOException has been thrown.
	 * This method works the same as {@code OutputStream.transferTo()}, but may be interrupted
	 * any time using the {@code interruptCopy()} method. It is also guaranteed to return, when
	 * either stream closes, which does not work on {@code OutputStream.transferTo()} in some edge cases.
	 * 
	 * @param destinationStream
	 */
	public boolean blockingCopyTo(OutputStream destinationStream) {
		// TODO Find a way to get this method to work (avoid checkWritable())
		// TODO Change returnValues to ErrCodes.something instead of String
		// TODO Specifying localStream instead of stream could still be affecting stream, so remove.
		
		// TODO Interrupt transferDataThread from OSE directly with new method interrupt...();
		
		externalCopyThread = new Thread(() -> {
			try {
				while (!Thread.interrupted() && !streamSource.toString().endsWith(OnSystemExecutor.EOF)) {
					synchronized (streamSource) {
						streamSource.wait(100);
						// TODO Maybe writeTo() will rewrite all bytes,
						// so a writeNewBytesTo() method may be required.
						System.err.println("Writing");
						streamSource.writeTo(destinationStream);
					}
				}
			} catch (IOException ioe) {
				sys.log("IOSTREAM:TRANSFER", LogLevel.STATUS, "Stream copy error.");
				ioe.printStackTrace();
				regenerate();
			} catch (InterruptedException ie) {
				sys.log("IOSTREAM:TRANSFER", LogLevel.DEBUG, "Ext. stream copy interrupted. Stopping early.");
			}
			sys.log("IOSTREAM:TRANSFER", LogLevel.STATUS, "Dest. stream closed. Stopping transfer.");
		});
		
		externalCopyThread.start();

		try {
			externalCopyThread.join();
		} catch (InterruptedException ie) {
			sys.log("IOSTREAM:TRANSFER", LogLevel.ERR,
					"Transfer to dest. stream interrupted. Stopping early.");
		}
		
		return true;
	}

	/**
	 * Does the same as blockingCopyTo() without blocking the current
	 * thread.
	 * 
	 * @param destinationStream
	 */
	public void copyTo(OutputStream destinationStream) {
		new Thread(() -> blockingCopyTo(destinationStream)).start();
	}

	/**
	 * Tests, whether this OutputStream can be written to or if it's closed already.
	 * 
	 * @param outStream
	 * @return Whether a write attempt to outStream doesn't result in an Exception
	 * @implNote This method attempts to write an empty byte array to outStream, so
	 *           it may interfere with the stream's contents.
	 */
	public void interruptCopy() {
		if (externalCopyThread != null)
			externalCopyThread.interrupt();
	}
}
