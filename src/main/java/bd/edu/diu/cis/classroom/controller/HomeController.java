package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserDetailsServiceImplement userService;

    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {
        if (principal == null)
            return "redirect:/login";

        ClassroomController.userAndClassroom(model, principal, session, userService);

        return "index";
    }
}
