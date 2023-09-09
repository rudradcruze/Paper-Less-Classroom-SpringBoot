package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.model.ClassroomUser;
import bd.edu.diu.cis.classroom.model.Post;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.ClassroomUserService;
import bd.edu.diu.cis.classroom.utils.FileService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
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
        classroom.setUrl(RandomString.getAlphaNumericString(20));
        String inviteCode = RandomString.getAlphaNumericString(6);

        // generate invite code until the classroom gets null
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
        model.addAttribute("classroomUserList", classroom.getStudents());
        model.addAttribute("active", "people");

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
        List<Post> posts = classroom.getPosts();
        userAndClassroom(model, principal, session, userService);
        model.addAttribute("classroom", classroom);
        model.addAttribute("active", "stream");
        model.addAttribute("posts", posts);

        return "classroom-stream";
    }
}
