package test;

import java.io.*;
import java.lang.reflect.Method;

public class TabConvertor {

    private File file;
    private File tempFile;
    private static final String POST_FIX = ".bk";
    private static final int BUFFER_SIZE = 1024 * 50;

    public TabConvertor(final String fName) throws IOException {
        file = new File(fName);
        if(!file.exists()){
            throw new FileNotFoundException(fName);
        }
        tempFile = new File(file.getParent(), file.getName()+POST_FIX);

    }

    public void process() throws IOException{
        writeToTemp();
        replaceOriginalFile();
        tempFile.delete();
    }

    public boolean testMethod(final int a, final String... b){
        return true;
    }

    private void writeToTemp() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
        String line;
        while((line = reader.readLine()) != null){
            line = line.replace('\t', ' ');
            System.out.println(line);
            writer.write(line+"\n");
            writer.flush();
        }
        writer.close();
        reader.close();
    }

    private void replaceOriginalFile() throws IOException {
        final FileOutputStream output = new FileOutputStream(file);
        final FileInputStream input = new FileInputStream(tempFile);
        final byte[] buffer = new byte[BUFFER_SIZE];
        int byteRead;
        while((byteRead = input.read(buffer)) > 0){
            output.write(buffer, 0, byteRead);
            output.flush();
        }
        output.close();
        input.close();
    }

    public static void main(final String... args) throws IOException, NoSuchMethodException {
        final TabConvertor convertor = new TabConvertor("/home/gang-liu/Desktop/test.txt");
        convertor.process();
        final Method method = TabConvertor.class.getDeclaredMethod("testMethod", new Class[]{int.class, String[].class});
        System.out.println(method.getName());

        int x = 0, y=2, z=1;
        System.out.println(y>0?
                x>0?
                        z>0?"yes":"no":
                        "good":
                "nad");
    }

}
