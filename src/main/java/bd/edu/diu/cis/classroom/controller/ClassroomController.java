package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.*;
import bd.edu.diu.cis.classroom.service.*;
import bd.edu.diu.cis.classroom.utils.FileService;
import bd.edu.diu.cis.classroom.utils.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @Autowired
    private ClassroomTeacherService classroomTeacherService;

    @Autowired
    private PostService postService;

    @Autowired
    private SectionService sectionService;

    @Value("${project.image}")
    private String imagePath;

    @GetMapping("/classroom/new")
    public String newClassroom(Model model, Principal principal, HttpSession session) {
        if (principal == null)
            return "redirect:/login";

        userAndClassroom(model, principal, session, userService);
        model.addAttribute("classroom", new Classroom());
        model.addAttribute("title", "New Classroom");
        model.addAttribute("headTitle", "Create New Classroom");

        return "classroom-new";
    }

    @PostMapping("/classroom/new/save")
    public String saveClassroom(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute("classroom") Classroom classroom,
            Principal principal,
            Model model) {

        if (principal == null)
            return "redirect:/login";

        String fileName = null;

        if (file != null) {
            fileName = fileService.uploadFile(imagePath, file, "image");
        }

        if (Objects.equals(fileName, "not image")) {
            model.addAttribute("error", "Upload a valid image file, supported extension: (jpg, png, jpeg, gif)");
            model.addAttribute("classroom", classroom);

            return "classroom-new";
        }

        if (fileName == null)
            fileName = "1.jpg";

        classroom.setImageName(fileName);

        // sending classroom url
        String classroomUrl = RandomString.getAlphaNumericString(20);
        while (classroomService.findByUrl(classroomUrl) != null) {
            classroomUrl = RandomString.getAlphaNumericString(20);
        }
        classroom.setUrl(classroomUrl);

        classroom.setActivate(true);
        classroom.setCanPost(true);
        classroom.setTeacher(userService.getByUserEmail(principal.getName()));

        classroomService.save(classroom);

        return "redirect:/";
    }

    @GetMapping("/classroom/people/{url}")
    public String classroomPeople(@PathVariable String url,
                                  Model model, Principal principal, HttpSession session) {

        if (principal == null) return "redirect:/login";

        Classroom classroom = classroomService.findByUrl(url);
        userAndClassroom(model, principal, session, userService);
        model.addAttribute("classroom", classroom);

        List<ClassroomTeacher> classroomTeacherList = classroomTeacherService.listTeachersByClassroomUrl(url);

        boolean isTeacher = false;
        for (ClassroomTeacher classroomTeacher : classroomTeacherList) {
            if (Objects.equals(classroomTeacher.getTeacher().getUsername(), principal.getName())) {
                isTeacher = true;
                break;
            }
            System.out.println(principal.getName() + " " + classroomTeacher.getTeacher().getUsername());
        }

        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("classroomTeacherList", classroomTeacherList);

        List<Section> sectionList = classroom.getSections();
        model.addAttribute("sectionList", sectionList);

        int students = 0;

        for (Section counting : sectionList) {
            students += counting.getStudents().size();
        }

        model.addAttribute("totalStudents", students);
        model.addAttribute("active", "people");
        model.addAttribute("headTitle", classroom.getName());
        model.addAttribute("title", "Classroom People");

        return "classroom-people";
    }

    static void userAndClassroom(Model model, Principal principal, HttpSession session, UserDetailsServiceImplement userService) {
        User user = userService.getByUserEmail(principal.getName());
        session.setAttribute("user", user);
        List<Classroom> classroomList = user.getClassrooms();

//        for (ClassroomTeacher cu : user.getClassroomUsers()) {
//            classroomList.add(cu.getClassroom());
//        }
        model.addAttribute("classrooms", classroomList);
    }

    @GetMapping("/classroom/stream/{url}")
    public String classroomStream(@PathVariable String url,
                                  Principal principal,
                                  Model model, HttpSession session) {

        if (principal == null) return "redirect:/";

        Classroom classroom = classroomService.findByUrl(url);
        List<Post> posts = postService.getAllByClassroomUrlDateDesc(url);
        userAndClassroom(model, principal, session, userService);
        model.addAttribute("classroom", classroom);
        model.addAttribute("active", "stream");
        model.addAttribute("posts", posts);
        model.addAttribute("headTitle", classroom.getName());
        model.addAttribute("title", "Classroom Stream");

        return "classroom-stream";
    }

    @GetMapping("/classroom/setting/{url}")
    public String classroomSetting(@PathVariable String url,
                                   Model model,
                                   Principal principal,
                                   HttpSession session) {

        if (principal == null) return "redirect:/login";

        Classroom classroom = classroomService.findByUrl(url);
        List<Section> sections = sectionService.listSectionsByClassroomUrl(url);
        userAndClassroom(model, principal, session, userService);

        model.addAttribute("classroom", classroom);
        model.addAttribute("sections", sections);
        model.addAttribute("headTitle", classroom.getName());
        model.addAttribute("title", "Classroom Setting");

        return "classroom-setting";
    }
}
