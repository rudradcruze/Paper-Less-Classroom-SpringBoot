package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserDetailsServiceImplement userService;

    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {
        if (principal == null)
            return "redirect:/login";

        User user = userService.getByUserEmail(principal.getName());
        session.setAttribute("user", user);
        List<Classroom> classroomList = user.getClassrooms();
        model.addAttribute("classrooms", classroomList);
        session.setAttribute("classrooms", classroomList);

        return "index";
    }
}
