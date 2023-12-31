package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.*;
import bd.edu.diu.cis.classroom.service.*;
import bd.edu.diu.cis.classroom.utils.FileExtensionCheck;
import bd.edu.diu.cis.classroom.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private SubmissionService submissionService;

    @Value("${project.file}")
    private String contentPath;

    @Value("${project.image}")
    private String imagePath;

    boolean isTeacher(Principal principal, UserDetailsServiceImplement userService, Classroom classroom) {
        return classroomTeacherList(principal, userService, classroom, classroomTeacherService);
    }

    static boolean classroomTeacherList(Principal principal, UserDetailsServiceImplement userService, Classroom classroom, ClassroomTeacherService classroomTeacherService) {
        List<ClassroomTeacher> classroomTeacherList = classroomTeacherService.listTeachersByClassroomUrl(classroom.getUrl());
        boolean teacher = false;

        User user = userService.getByUserEmail(principal.getName());

        if (Objects.equals(classroom.getTeacher().getUsername(), user.getUsername())) {
            return true;
        }

        for (ClassroomTeacher classroomTeacher : classroomTeacherList) {
            if (Objects.equals(classroomTeacher.getTeacher().getUsername(), user.getUsername())) {
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
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "str_dueDate", required = false) String str_dueDate,
                                @RequestParam(value = "sections", required = false) List<Long> sections,
                                Principal principal, RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        // find the classroom
        Classroom classroom = classroomService.findByUrl(url);

        User user = userService.getByUserEmail(principal.getName());

        boolean teacher = isTeacher(principal, userService, classroom);

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
        if (Objects.equals(type, "post")) {
            post.setType("POST");
        }
        else if (Objects.equals(type, "material"))
            post.setType("MATERIAL");
        else if (Objects.equals(type, "question"))
            post.setType("QUESTION");
        else {
            post.setType("ASSIGNMENT");
            post.setCanSubmit(true);
        }


        if (str_dueDate != null) {

            try {
                // Define the DateTimeFormatter for parsing the datetime-local string (Java 8 and later)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

                // Parse the string to a LocalDateTime
                LocalDateTime localDateTime = LocalDateTime.parse(str_dueDate, formatter);

                // Convert LocalDateTime to java.util.Date
                Date utilDate = Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

                post.setDueDate(utilDate);
            } catch (Exception e) {
                attributes.addFlashAttribute("error", "Due date error");
            }
        }

        // setting common values
        post.setClassroom(classroom);
        post.setUser(user);
        post.setPostDate(new Date());
        post.setStatus(true);

        String fileName;

        // checking & uploading the content file
        if (!file.isEmpty()) {
            if (FileExtensionCheck.imageCheck(Objects.requireNonNull(file.getOriginalFilename())))
                fileName = fileService.uploadFile(imagePath, file, "image");
            else
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

    @GetMapping("/classroom/post/{url}/{id}")
    public String updatePostStatus(@PathVariable String url,
                                   @PathVariable String id,
                                   @RequestParam("type") String type,
                                   Principal principal,
                                   RedirectAttributes attributes) {
        if (principal == null) return "redirect:/login";

        User user = userService.getByUserEmail(principal.getName());
        Post post = postService.getById(Long.parseLong(id));

        boolean teacher = isTeacher(principal, userService, post.getClassroom());

        if (!post.getUser().equals(user) || !teacher) {
            attributes.addFlashAttribute("error", "You are not the post creator or teacher.");
            return "redirect:/classroom/stream/" + url;
        }

        if (Objects.equals(type, "status"))
            post.setStatus(!post.isStatus());
        else if (Objects.equals(type, "submit")) {
            post.setCanSubmit(!post.isCanSubmit());
        }

        try {
            postService.save(post);
            attributes.addFlashAttribute("success", new SimpleDateFormat("dd MMM yyyy").format(post.getPostDate()) + " post is successfully " + (post.isStatus() ? "Visible" : "Hidden"));
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal Server error please try after sometimes");
        }

        return "redirect:/classroom/stream/" + url;
    }

    @GetMapping("/classroom/instruction/{url}/{id}")
    public String postInstruction(@PathVariable String id,
                                  @PathVariable String url,
                                  Model model, Principal principal, HttpSession session) {

        if (principal == null) return "redirect:/login";

        Classroom classroom = classroomService.findByUrl(url);

        ClassroomController.userAndClassroom(model, principal, session, userService);
        boolean teacher = isTeacher(principal, userService, classroom);

        Post post = postService.getById(Long.parseLong(id));
        Submission submission;

        submission = submissionService.getByPostIdAndUser(post.getId(), principal.getName());

        if (submission == null)
            submission = new Submission();

        model.addAttribute("classroom", classroom);
        model.addAttribute("isTeacher", teacher);
        model.addAttribute("post", post);
        model.addAttribute("submission", submission);

        return "post-instruction";
    }

}
