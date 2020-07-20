package com.pastdev.jsch.sftp;

import java.io.Closeable;
import java.io.IOException;


public abstract class SftpRunner implements Closeable {

    abstract public void execute( Sftp sftp ) throws IOException;

    @Override
    public abstract void close() throws IOException;

    public interface Sftp {
        void run() throws IOException;
    }
}
