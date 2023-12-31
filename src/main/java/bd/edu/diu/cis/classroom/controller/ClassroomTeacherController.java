package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import bd.edu.diu.cis.classroom.model.SectionUser;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.ClassroomTeacherService;
import bd.edu.diu.cis.classroom.service.SectionUserService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class ClassroomTeacherController {

    @Autowired
    private ClassroomTeacherService classroomTeacherService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @Autowired
    private SectionUserService sectionUserService;

    @PostMapping("/classroom/teacher/add/{url}")
    public String inviteTeacher(@PathVariable String url,
                                Principal principal,
                                RedirectAttributes attributes,
                                @RequestParam("email") String email) {

        if (principal == null) return "redirect:/login";

        User invitedTeacher = userService.getByUserEmail(email);
        ClassroomTeacher classroomTeacher;
        classroomTeacher = classroomTeacherService.getByTeacherUserNameAndClassroomUrl(url, email);

        SectionUser sectionUser = sectionUserService.getByUrlStudentEmail(url, email);

        if (sectionUser != null) {
            attributes.addFlashAttribute("error", "This teacher is already enrolled as a student!");
            return "redirect:/classroom/people/" + url;
        }

        if (classroomTeacher != null) {
            attributes.addFlashAttribute("error", "This teacher is already invited!");
            return "redirect:/classroom/people/" + url;
        } else
            classroomTeacher = new ClassroomTeacher();

        classroomTeacher.setTeacher(invitedTeacher);
        classroomTeacher.setStatus("PENDING");
        classroomTeacher.setClassroom(classroomService.findByUrl(url));
        classroomTeacher.setRegistered(new Date());

        try {
            classroomTeacherService.save(classroomTeacher);
            attributes.addFlashAttribute("success", invitedTeacher.getFirstName() + " " + invitedTeacher.getLastName() + " in successfully invited");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal server error");
        }

        return "redirect:/classroom/people/" + url;
    }

    @GetMapping("/classroom/teacher/request")
    public String classroomRequest(Principal principal,
                                   Model model,
                                   HttpSession session) {

        if (principal == null) return "redirect:/login";

        List<ClassroomTeacher> classroomTeacherRequest = classroomTeacherService.getAllTeacherRequestByTeacherUserName(principal.getName());
        ClassroomController.userAndClassroom(model, principal, session, userService);
        model.addAttribute("classroomTeacherRequest", classroomTeacherRequest);

        return "teacher-request";
    }

    @GetMapping("/classroom/teacher/request/update")
    public String updateTeacherRequest(RedirectAttributes attributes,
                                       @RequestParam("url") String url,
                                       @RequestParam("status") String status,
                                       Principal principal) {

        if (principal == null) return "redirect:/login";

        ClassroomTeacher classroomTeacher = classroomTeacherService.getByTeacherUserNameAndClassroomUrl(url, principal.getName());

        if (Objects.equals(status, "ACCEPT")) {
            classroomTeacher.setStatus("ACCEPT");
            attributes.addFlashAttribute("success", "You are successfully joined into the classroom");
            classroomTeacherService.save(classroomTeacher);
            return "redirect:/classroom/stream/" + url;
        } else if (Objects.equals(status, "REJECT")){
            classroomTeacher.setStatus("REJECT");
            attributes.addFlashAttribute("warning", "You are rejected the classroom teacher invitation");
            classroomTeacherService.save(classroomTeacher);
        } else {
            attributes.addFlashAttribute("error", "Error for bad request.");
        }

        return "redirect:/classroom/teacher/request";
    }
}
