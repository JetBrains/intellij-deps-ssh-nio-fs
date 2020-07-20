package com.pastdev.jsch.command;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Provides a convenience wrapper around an <code>exec</code> channel. This
 * implementation offers a simplified interface to executing remote commands and
 * retrieving the results of execution.
 */
public abstract class CommandRunner implements Closeable {

    /**
     * Returns a new CommandRunner with the same SessionFactory, but will
     * create a separate session.
     *
     * @return A duplicate CommandRunner with a different session.
     */
    public abstract CommandRunner duplicate();

    /**
     * Executes <code>command</code> and returns the result. Use this method
     * when the command you are executing requires no input, writes only UTF-8
     * compatible text to STDOUT and/or STDERR, and you are comfortable with
     * buffering up all of that data in memory. Otherwise, use
     * {@link #open(String)}, which allows you to work with the underlying
     * streams.
     *
     * @param command
     *            The command to execute
     * @return The resulting data
     *
     * @throws IOException
     *             If unable to read the result data
     */
    public abstract ExecuteResult execute(String command) throws IOException;

    /**
     * Executes <code>command</code> and returns an execution wrapper that
     * provides safe access to and management of the underlying streams of data.
     *
     * @param command
     *            The command to execute
     * @return An execution wrapper that allows you to process the streams
     * @throws IOException
     *             If unable to read the result data
     */
    public abstract ChannelExecWrapper open(String command) throws IOException;

    /**
     * A simple container for the results of a command execution. Contains
     * <ul>
     * <li>The exit code</li>
     * <li>The text written to STDOUT</li>
     * <li>The text written to STDERR</li>
     * </ul>
     * The text will be UTF-8 decoded byte data written by the command.
     */
    public class ExecuteResult {
        private final int exitCode;
        private final String stderr;
        private final String stdout;

        public ExecuteResult( int exitCode, String stdout, String stderr ) {
            this.exitCode = exitCode;
            this.stderr = stderr;
            this.stdout = stdout;
        }

        /**
         * Returns the exit code of the command execution.
         *
         * @return The exit code
         */
        public int getExitCode() {
            return exitCode;
        }

        /**
         * Returns the text written to STDERR. This will be a UTF-8 decoding of
         * the actual bytes written to STDERR.
         *
         * @return The text written to STDERR
         */
        public String getStderr() {
            return stderr;
        }

        /**
         * Returns the text written to STDOUT. This will be a UTF-8 decoding of
         * the actual bytes written to STDOUT.
         *
         * @return The text written to STDOUT
         */
        public String getStdout() {
            return stdout;
        }
    }

    /**
     * Wraps the execution of a command to handle the opening and closing of all
     * the data streams for you. To use this wrapper, you call
     * <code>getXxxStream()</code> for the streams you want to work with, which
     * will return an opened stream. Use the stream as needed then call
     * {@link ChannelExecWrapper#close() close()} on the ChannelExecWrapper
     * itself, which will return the the exit code from the execution of the
     * command.
     */
    public interface ChannelExecWrapper {


        /**
         * Safely closes all stream, waits for the underlying connection to
         * close, then returns the exit code from the command execution.
         *
         * @return The exit code from the command execution
         */
        int close();

        /**
         * Returns the STDERR stream for you to read from. No need to close this
         * stream independently, instead, when done with all processing, call
         * {@link #close()};
         *
         * @return The STDERR stream
         * @throws IOException
         *             If unable to read from the stream
         */
        InputStream getErrStream() throws IOException;

        /**
         * Returns the STDOUT stream for you to read from. No need to close this
         * stream independently, instead, when done with all processing, call
         * {@link #close()};
         *
         * @return The STDOUT stream
         * @throws IOException
         *             If unable to read from the stream
         */
        InputStream getInputStream() throws IOException;

        /**
         * Returns the STDIN stream for you to write to. No need to close this
         * stream independently, instead, when done with all processing, call
         * {@link #close()};
         *
         * @return The STDIN stream
         * @throws IOException
         *             If unable to write to the stream
         */
        public OutputStream getOutputStream() throws IOException;
    }
}
