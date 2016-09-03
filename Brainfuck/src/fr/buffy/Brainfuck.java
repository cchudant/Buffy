package fr.buffy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Simple brainfuck interpreter for Brainfuck code in Java. Originally named BFI
 * (BrainFuck Intepreter) but renamed to Buffy because why not
 * 
 * @author Charles and Antide88
 * @see #brainfuck(String, Scanner, int, int)
 */
public class Brainfuck {

	/**
	 * Simple example for a command-line brainfuck which read his code in a file
	 * 
	 * @param args
	 *            unused
	 * @throws MalformatedBrainfuckException
	 *             throwed when a brainfuck error occurs @see
	 *             {@link #brainfuck(String, Scanner, int, int)}
	 * @throws IOException
	 * @see #brainfuck(String, Scanner, int, int)
	 */
	public static void main(String[] args) throws MalformatedBrainfuckException, IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter file absolute path:");
		String address = scan.nextLine();

		long nanotime = System.nanoTime();
		System.out.println(brainfuck(new String(Files.readAllBytes(new File(address).toPath())), scan, 1024, -1));

		System.out.println("Took " + (System.nanoTime() - nanotime) + " ns");

		scan.close();
	}

	/**
	 * Execute a brainfuck code. This code don't throws any exception if the
	 * code has useless character.
	 * 
	 * @param code
	 *            the code to execute
	 * @param console
	 *            the console reader for the brainfuck <code>,</code> command.
	 *            Set it to <code>null</code> to disable this
	 * @param cellCount
	 *            the number of cell the code can use
	 * @param loopLimit
	 *            the loop-limit to set (<code>-1</code> for no limitations)
	 * @return the code output as a String
	 * @throws MalformatedBrainfuckException
	 *             when closing a loop before opening it, or opening a loop and
	 *             never close it, or when the loopLimit is exceeded
	 * @see MalformatedBrainfuckException
	 */
	public static String brainfuck(String code, Scanner console, int cellCount, int loopLimit)
			throws MalformatedBrainfuckException {
		byte[] cells = new byte[cellCount];
		int cursor = 0;
		StringBuilder result = new StringBuilder();
		List<Integer> loops = new ArrayList<>();
		int loopCounter = 0;

		for (int i = 0; i < code.length(); i++) {
			char ch = code.charAt(i);

			switch (ch) {
			case '+': // Add one to the current cell
				cells[cursor]++;
				break;
			case '-': // Remove one to the current cell
				cells[cursor]--;
				break;
			case '<': // Shift the current cell to the left
				cursor--;
				if (cursor == -1)
					cursor = cells.length - 1;
				break;
			case '>': // Shift the current cell to the right
				cursor++;
				if (cursor == cells.length)
					cursor = 0;
				break;
			case '.': // Add the current cell as a character to the output
				result.append((char) (cells[cursor] & 0xFF));
				break;
			case ',': // If console is not null, prompt a character
				if (console != null) {
					String next = console.nextLine();
					char cha = next.length() > 0 ? next.charAt(0) : 0;
					cells[cursor] = (byte) cha;
				}
				break;
			case '[': // Start a new loop
				loops.add(i);
				break;
			case ']': // Return to the beginning of the loop if the cell is not
						// equals to 0, stop the loop otherwise
				loopCounter++;
				if (loops.isEmpty())
					throw new MalformatedBrainfuckException("Cannot close a loop before opening it");
				if (loopLimit != -1 && loopCounter > loopLimit)
					throw new MalformatedBrainfuckException(
							"The loop limit is exceeded (Loop limit set to " + loopLimit + ")");

				if (cells[cursor] != 0) {
					i = loops.get(loops.size() - 1) - 1;
				} else {
					loops.remove(loops.size() - 1);
				}
				break;
			}
		}

		if (!loops.isEmpty())
			throw new MalformatedBrainfuckException("A loop was opened but never closed");

		return result.toString();
	}

	/**
	 * Throwed by Brainfuck#brainfuck(String, Scanner, int, int) when closing a
	 * loop before opening it, or opening a loop and never close it, or when the
	 * loopLimit is exceeded
	 * 
	 * @see Brainfuck#brainfuck(String, Scanner, int, int)
	 */
	public static class MalformatedBrainfuckException extends Exception {
		private static final long serialVersionUID = 7787508074711093486L;

		/**
		 * {@inheritDoc}
		 */
		public MalformatedBrainfuckException() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public MalformatedBrainfuckException(String message) {
			super(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public MalformatedBrainfuckException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * {@inheritDoc}
		 */
		public MalformatedBrainfuckException(Throwable cause) {
			super(cause);
		}

		/**
		 * {@inheritDoc}
		 */
		protected MalformatedBrainfuckException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
	}

}
