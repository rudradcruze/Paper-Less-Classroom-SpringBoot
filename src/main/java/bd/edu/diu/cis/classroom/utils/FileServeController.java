package bd.edu.diu.cis.classroom.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class FileServeController {
    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String imagePath;

    @Value("${project.file}")
    private String contentPath;

    //    Method to serve files
    @GetMapping("/files/{name}")
    public void serveFile(
            @PathVariable("name") String name,
            HttpServletResponse response) throws IOException {
        String extension = name.substring(name.lastIndexOf("."));

        InputStream resource;
        extension = extension.replace(".", "");
        if (FileExtensionCheck.imageCheck(extension.toUpperCase()))
            resource = fileService.getResource(imagePath, name);
        else
            resource = fileService.getResource(contentPath, name);

        switch (extension) {
            case "png" -> response.setContentType(MediaType.IMAGE_PNG_VALUE);
            case "jpg", "jpeg" -> response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            case "gif" -> response.setContentType(MediaType.IMAGE_GIF_VALUE);
            case "pdf" -> response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        }

        StreamUtils.copy(resource, response.getOutputStream());
    }
}
