package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.FileResponse;
import bd.edu.diu.cis.classroom.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileTestController {

    @Value("${project.image}")
    private String imagePath;

    @Autowired
    private FileService fileService;

    @PostMapping("/file/upload")
    public FileResponse fileUpload(@RequestParam("image")MultipartFile file) {

        String fileName = null;

        if (file != null) {
            fileName = this.fileService.uploadFile(imagePath, file, "image");
            return new FileResponse(fileName, "File Upload Successfully");
        } else  {
            return new FileResponse(fileName, "File Could not Successfully");
        }
    }
}
