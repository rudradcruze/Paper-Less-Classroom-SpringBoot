package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import bd.edu.diu.cis.classroom.model.Post;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.ClassroomTeacherService;
import bd.edu.diu.cis.classroom.service.PostService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import bd.edu.diu.cis.classroom.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ClassroomTeacherService classroomTeacherService;

    @Value("${project.file}")
    private String contentPath;

    boolean isTeacher(Principal principal, String url, UserDetailsServiceImplement userService) {

        List<ClassroomTeacher> classroomTeacherList = classroomTeacherService.listTeachersByClassroomUrl(url);
        boolean teacher = false;

        User user = userService.getByUserEmail(principal.getName());
        for (ClassroomTeacher classroomTeacher : classroomTeacherList) {
            if (classroomTeacher.getTeacher() == user || classroomTeacher.getClassroom().getTeacher() == user) {
                teacher = true;
                break;
            }
        }
        return teacher;
    }

    @PostMapping("/classroom/post/{url}")
    public String classroomPost(@PathVariable String url,
                                @RequestParam(value = "type") String type,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "content") String content,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                Principal principal, RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        // find the classroom
        Classroom classroom = classroomService.findByUrl(url);


        Post post = new Post();
        User user = userService.getByUserEmail(principal.getName());

        boolean teacher = isTeacher(principal, url, userService);

        if (!classroom.isCanPost() && !teacher && !Objects.equals(classroom.getTeacher().getUsername(), user.getUsername())) {
            attributes.addFlashAttribute("error", "Post is off or only teacher can post into this classroom");
            return "redirect:/classroom/stream/" + url;
        }

        // Identifying the post type
        if (Objects.equals(type, "post"))
            post.setType("POST");
        else if (Objects.equals(type, "material"))
            post.setType("MATERIAL");
        else if (Objects.equals(type, "question"))
            post.setType("QUESTION");
        else
            post.setType("ASSIGNMENT");

        if (!Objects.equals(type, "post")) {
            post.setTitle(title);
        }

        // setting common values
        post.setClassroom(classroom);
        post.setUser(user);
        post.setPostDate(new Date());
        post.setContent(content);
        post.setStatus(true);

        String fileName;

        // checking & uploading the content file
        if (!file.isEmpty()) {
            fileName = fileService.uploadFile(contentPath, file, "content");
            post.setFileName(fileName);
        }

        postService.save(post);
        attributes.addFlashAttribute("success", "You have successfully created the " + type);

        return "redirect:/classroom/stream/" + url;
    }

}
