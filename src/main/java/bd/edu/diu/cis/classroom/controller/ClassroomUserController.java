package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.ClassroomUserService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ClassroomUserController {

    @Autowired
    private ClassroomUserService classroomUserService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private UserDetailsServiceImplement userService;

//    @PostMapping("/classroom/join")
//    public String joinClassroomPage(
//            @RequestParam("inviteCode") String inviteCode,
//            RedirectAttributes attributes,
//            Principal principal) {
//
//        if (principal == null) return "redirect:/login";
//
//        ClassroomUser classroomUser = new ClassroomUser();
//
////        Classroom classroom = classroomService.findByInviteCode(inviteCode);
//
//        if (classroom == null) {
//            attributes.addFlashAttribute("error", "Wrong invitation Code! Please enter a valid invitation code");
//            return "redirect:/";
//        }
//
//        classroomUser.setClassroom(classroom);
//        classroomUser.setStudent(userService.getByUserEmail(principal.getName()));
//        classroomUser.setRegistered(new Date());
//        classroomUser.setActivate(true);
//
//        classroomUserService.save(classroomUser);
//        attributes.addFlashAttribute("success", "You have successfully joined into " + classroom.getName() + " classroom");
//
//        return "redirect:/";
//    }
}
