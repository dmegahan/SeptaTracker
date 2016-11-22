import java.io.*;
import java.nio.file.*;

/**
 * Created by Danny on 8/30/2016.
 */
public class FileReadWriter {
    String fileName;
    public FileReadWriter(String fileName){
        this.fileName = fileName;
    }

    public void writeToFile(String s){
        // Convert the string to a
        // byte array.
        byte data[] = s.getBytes();
        Path p = Paths.get(this.fileName);

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public String readFromFile(){
        String full = "";
        Path file = Paths.get("./tracked.txt");
        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                full = full + line;
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return full;
    }
}
