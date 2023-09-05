package bd.edu.diu.cis.classroom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileService {
    public String uploadFile(String path, MultipartFile file, String type) {

        // File name
        String name = file.getOriginalFilename();

        // Filepath
        String filePath = path + File.separator + name;

        // Create folder if not created
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        // File copy
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("File could not copy");
            System.out.println("Exception Message -> " + e.getMessage());
        }

        return name;
    }
}
