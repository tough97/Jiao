package test;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;


public class CreateZipFileDirectory {

    public static void main(String args[]) throws IOException {
        final File sourceDir = new File("/home/gang-liu/develop/lib/javassist-3.17.1-GA");
        final File destFile = new File("/home/gang-liu/develop/lib/javassist-3.17.1-GA/test.zip");
        final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(destFile));
        final int SIZE = 1024 * 10;

        final byte[] buffer = new byte[SIZE];
        for(final File file : sourceDir.listFiles()){
            if(file.isFile()){
                final FileInputStream inputStream = new FileInputStream(file);
                final ZipEntry entry = new ZipEntry("a/b/c/"+file.getName());
                outputStream.putNextEntry(entry);
                int bufferRead;
                while((bufferRead = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, bufferRead);
                    outputStream.flush();
                }
                inputStream.close();
                outputStream.closeEntry();
            }
        }

        outputStream.close();
    }

}