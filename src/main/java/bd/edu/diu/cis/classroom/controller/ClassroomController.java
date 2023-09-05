package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @GetMapping("/classroom/new")
    public String newClassroom(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        model.addAttribute("classroom", new Classroom());
        model.addAttribute("title", "New Classroom");

        return "new-classroom";
    }

    @PostMapping("/classroom/new/save")
    public String saveClassroom(
            @RequestParam("image") MultipartFile file,
            @RequestParam("classroom") Classroom classroom,
            RedirectAttributes attributes,
            Principal principal) {

        if (principal == null)
            return "redirect:/login";

        System.out.println("value get");
        System.out.println(classroom);
        return classroom.toString();
    }
}
