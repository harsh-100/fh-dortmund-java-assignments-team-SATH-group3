
package logging;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
// This uses character streams for writing logs and byte streams 
// for archiving logs 

public class LogManager {

    private final Path logDir;

      public LogManager(String directory) throws IOException {
         this.logDir = Paths.get(directory);
        if (!Files.exists(logDir)) Files.createDirectories(logDir);
      }


    //   created or append using the character stream 
    public void writeLog(String fileName, String logMessage) {
        Path logFilePath = logDir.resolve(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle logging error (e.g., write to console, alert user)
        }
    }


    //move a log file 
    public void moveLog(String from , String to) throws IOException {

        try{

                Path sourcePath = logDir.resolve(from);
                Path targetPath = logDir.resolve(to);
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }

    // Delete log file
    public void deleteLog(String fileName) {
        Path logFilePath = logDir.resolve(fileName);
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            e.printStackTrace();
                 
        }
    }


    // Archive log file using byte stream

    // We can also done it with Files.copy() method, 
    // but as professor mentioned to use byte stream also to have idea about it 
    public void archiveLog(String fileName) {
        Path logFilePath = logDir.resolve(fileName);
        Path archiveFilePath = logDir.resolve("archive");
        Path dest = archiveFilePath.resolve(fileName);
       try (InputStream in = Files.newInputStream(logFilePath);
     OutputStream out = Files.newOutputStream(dest, StandardOpenOption.CREATE)) {

    byte[] buffer = new byte[1024]; // 1KB
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
    }

    System.out.println("Log file archived to: " + dest.toString());

} catch (IOException e) {
    e.printStackTrace();
}
    }


}


