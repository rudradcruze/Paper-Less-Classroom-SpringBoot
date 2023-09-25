package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Section;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.SectionService;
import bd.edu.diu.cis.classroom.utils.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ClassroomService classroomService;


    @PostMapping("/classroom/section/create/{url}")
    public String newSection(Principal principal,
                            @PathVariable String url,
                            @RequestParam("sectionName") String sectionName,
                            @RequestParam("meetingLink") String meetingLink,
                            RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        if (sectionName.isEmpty() || sectionName.isBlank()) {
            attributes.addFlashAttribute("error", "Section cannot be empty");
            return "redirect:/classroom/stream/" + url;
        }

        Section sectionCheck = sectionService.getByName(sectionName);

        if (sectionCheck != null && sectionName.equals(sectionCheck.getName())) {
            attributes.addFlashAttribute("error", "This section is already exist in this classroom!");
            return "redirect:/classroom/stream/" + url;
        }

        // sending invite code
        String inviteCode = RandomString.getAlphaNumericString(10, true);
        while (sectionService.getByJoinCode(inviteCode) != null) {
            inviteCode = RandomString.getAlphaNumericString(10, true);
        }

        Section section = new Section();

        section.setClassroom(classroomService.findByUrl(url));
        section.setStatus(true);

        if (!meetingLink.isBlank() || !meetingLink.isEmpty()) {
            section.setMeetingLink(meetingLink);
            section.setMeetingLinkStatus(true);
        }
        else
            section.setMeetingLinkStatus(false);

        section.setJoinCode(inviteCode);
        section.setName(sectionName);

        try {
            sectionService.save(section);
            attributes.addFlashAttribute("success", "Successfully \"" + sectionName + "\" section is created.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal Server error");
        }

        return "redirect:/classroom/setting/" + url;
    }
}
