package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.utils.FileExtensionCheck;
import bd.edu.diu.cis.classroom.utils.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class FileService {


    public String uploadFile(String path, MultipartFile file, String type) {

        if (file.isEmpty())
            return null;

        // File name
        String name = file.getOriginalFilename();

        // extract extension
        assert name != null;
        String extension = name.substring(name.lastIndexOf("."));

        // check it is image or not
        if (type == "image" || FileExtensionCheck.imageCheck(extension)) {
            return "not image";
        }

        System.out.println(extension);
        System.out.println(FileExtensionCheck.imageCheck(extension));

        String randomId = RandomString.getRandomId().concat(new Date().toString().replaceAll(":", "").replaceAll(" ", "")).concat(extension);

        // Filepath
        String filePath = path + File.separator + randomId;

        // File copy
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File could not copy");
        }

        return randomId;
    }
}
