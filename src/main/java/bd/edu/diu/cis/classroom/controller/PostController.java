package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.*;
import bd.edu.diu.cis.classroom.service.*;
import bd.edu.diu.cis.classroom.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Autowired
    private SectionService sectionService;

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
    public String classroomPost(@ModelAttribute Post post,
                                @PathVariable String url,
                                @RequestParam(value = "type") String type,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "sections", required = false) List<Long> sections,
                                Principal principal, RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        // find the classroom
        Classroom classroom = classroomService.findByUrl(url);

        User user = userService.getByUserEmail(principal.getName());

        boolean teacher = isTeacher(principal, url, userService);

        if (!classroom.isCanPost() && !teacher && !Objects.equals(classroom.getTeacher().getUsername(), user.getUsername())) {
            attributes.addFlashAttribute("error", "Post is off or only teacher can post into this classroom");
            return "redirect:/classroom/stream/" + url;
        }

        if (sections == null) {
            attributes.addFlashAttribute("error", "Section cannot be null, you need to choose a section");
            return "redirect:/classroom/stream/" + url;
        }

        List<Section> selectedSections = sectionService.getSectionsByIds(sections);
        post.setSections(selectedSections);

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
        post.setStatus(true);

        String fileName;

        // checking & uploading the content file
        if (!file.isEmpty()) {
            fileName = fileService.uploadFile(contentPath, file, "content");
            post.setFileName(fileName);
        }

        try {
            postService.save(post);
            attributes.addFlashAttribute("success", "You have successfully created the " + type);
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal Server Error!");
        }

        return "redirect:/classroom/stream/" + url;
    }

}
