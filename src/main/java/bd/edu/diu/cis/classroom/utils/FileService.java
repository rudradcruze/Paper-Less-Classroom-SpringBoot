package bd.edu.diu.cis.classroom.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

@Service
public class FileService {

    @Value("${project.image}")
    private String imagePath;

    @Value("${project.file}")
    private String contentPath;

    public String uploadFile(String path, MultipartFile file, String type) {

        if (file.isEmpty())
            return null;

        String name = file.getOriginalFilename();

        assert name != null;
        String extension = name.substring(name.lastIndexOf(".")).replace(".", "");

        if (Objects.equals(type, "image") && !FileExtensionCheck.imageCheck(name)) {
            return "not image";
        }

        String randomId = RandomString.getRandomId().concat(new Date().toString().replaceAll(":", "").replaceAll(" ", "")).concat("." + extension);
        String filePath = path + File.separator + randomId;

        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File could not copy");
        }

        return randomId;
    }

    public InputStream getResource(String path, String fileName) {
        String fullPath = path + File.separator+fileName;
        try {
            return new FileInputStream(fullPath);
        } catch (FileNotFoundException e) {
            System.out.println("Message: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteFile(String fileName) throws IOException {

        String fullPath;

        if (FileExtensionCheck.imageCheck(fileName))
            fullPath = imagePath + File.separator;
        else
            fullPath = contentPath + File.separator;

        Path root = Paths.get(fullPath);
        boolean isDeleted = false;
        try {
            Path file = root.resolve(fileName);
            Files.deleteIfExists(file);
            isDeleted = true;
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }

        return isDeleted;
    }
}
