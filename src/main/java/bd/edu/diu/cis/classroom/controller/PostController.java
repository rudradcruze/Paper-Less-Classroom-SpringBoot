package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.model.Post;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.service.ClassroomService;
import bd.edu.diu.cis.classroom.service.PostService;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @PostMapping("/classroom/post/{url}")
    public String classroomPost(@PathVariable String url,
                                @RequestParam(value = "type", required = false) String type,
                                Principal principal, RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        // find the classroom
        Classroom classroom = classroomService.findByUrl(url);

        if (!classroom.isCanPost()) {
            attributes.addFlashAttribute("error", "Only teacher can post into this classroom");
            return "redirect:/classroom/stream/" + url;
        }

        Post post = new Post();
        User user = userService.getByUserEmail(principal.getName());

        // Identifying the post type
        if (Objects.equals(type, "post"))
            post.setType("POST");
        else if (Objects.equals(type, "material"))
            post.setType("MATERIAL");
        else if (Objects.equals(type, "question"))
            post.setType("QUESTION");
        else
            post.setType("ASSIGNMENT");

        // setting common values
        post.setClassroom(classroom);
        post.setUser(user);
        post.setPostDate(new Date());

        return "redirect:/classroom/stream/" + url;
    }

}
