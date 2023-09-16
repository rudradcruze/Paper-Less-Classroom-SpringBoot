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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private ClassroomUserService classroomUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private SectionService sectionService;

    @Value("${project.image}")
    private String imagePath;

    @GetMapping("/classroom/new")
    public String newClassroom(Model model, Principal principal, RedirectAttributes attributes) {
        if (principal == null)
            return "redirect:/login";

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

        // sending invite code
        String inviteCode = RandomString.getAlphaNumericString(6);
        while (classroomService.findByInviteCode(inviteCode) != null) {
            inviteCode = RandomString.getAlphaNumericString(6);
        }
        classroom.setInviteCode(inviteCode);

        classroom.setActivate(true);
        classroom.setInviteCodeActivate(true);
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
        model.addAttribute("classroomUserList", classroomUserService.listUsersByClassroomUrl(url));
        model.addAttribute("active", "people");
        model.addAttribute("headTitle", classroom.getName());
        model.addAttribute("title", "Classroom People");

        return "classroom-people";
    }

    static void userAndClassroom(Model model, Principal principal, HttpSession session, UserDetailsServiceImplement userService) {
        User user = userService.getByUserEmail(principal.getName());
        session.setAttribute("user", user);
        List<Classroom> classroomList = user.getClassrooms();

        for (ClassroomUser cu : user.getClassroomUsers()) {
            classroomList.add(cu.getClassroom());
        }
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
                                   Principal principal) {

        if (principal == null) return "redirect:/login";

        Classroom classroom = classroomService.findByUrl(url);
        List<Section> sections = sectionService.listSectionsByClassroomUrl(url);


        model.addAttribute("classroom", classroom);
        model.addAttribute("sections", sections);
        model.addAttribute("headTitle", classroom.getName());
        model.addAttribute("title", "Classroom Setting");

        return "classroom-setting";
    }
}
