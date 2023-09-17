package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Section;
import bd.edu.diu.cis.classroom.model.SectionUser;
import bd.edu.diu.cis.classroom.service.SectionService;
import bd.edu.diu.cis.classroom.service.SectionUserService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;

@Controller
public class SectionUserController {

    @Autowired
    private SectionUserService sectionUserService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @PostMapping("/classroom/section/join")
    public String joinClassroomSection(Principal principal,
                                       @RequestParam("inviteCode") String inviteCode,
                                       RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        Section section = sectionService.getByJoinCode(inviteCode);

        if (section == null) {
            attributes.addFlashAttribute("error", "Wrong invitation Code! Please enter a valid invitation code");
            return "redirect:/";
        }

        if (!section.isStatus()) {
            attributes.addFlashAttribute("error", "The section invitation code is inactive! Please contact with your teacher");
            return "redirect:/";
        }

        if (!section.getClassroom().isActivate()) {
            attributes.addFlashAttribute("error", "The classroom is inactive! Please contact with your teacher");
            return "redirect:/";
        }

        SectionUser sectionUser = sectionUserService.getByUrlStudentEmail(section.getClassroom().getUrl(), principal.getName());

        if (sectionUser != null) {
            attributes.addFlashAttribute("error", "You have already join into " + sectionUser.getSection().getClassroom().getName() + " this classroom under " + sectionUser.getSection().getName() + " this section");
            return "redirect:/classroom/stream/" + sectionUser.getSection().getClassroom().getUrl();
        } else
            sectionUser = new SectionUser();

        sectionUser.setSection(section);
        sectionUser.setStudent(userService.getByUserEmail(principal.getName()));
        sectionUser.setActivate(true);
        sectionUser.setRegistered(new Date());

        try {
            sectionUserService.save(sectionUser);
            attributes.addFlashAttribute("success", "You have successfully join into " + section.getClassroom().getName() + " at " + section.getName() + " this section");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal server error!");
        }

        return "redirect:/classroom/stream/" + section.getClassroom().getUrl();
    }
    
}
