package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.*;
import bd.edu.diu.cis.classroom.service.*;
import bd.edu.diu.cis.classroom.utils.FileExtensionCheck;
import bd.edu.diu.cis.classroom.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserDetailsServiceImplement userService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private PostService postService;

    @Autowired
    private SectionUserService sectionUserService;

    @Value("${project.file}")
    private String contentPath;

    @Value("${project.image}")
    private String imagePath;

    @PostMapping("/classroom/post/{url}/{id}/submission/save")
    public String saveSubmission(@ModelAttribute Submission submission,
                                 @PathVariable String id,
                                 @PathVariable String url,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 Principal principal,
                                 RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        User user = userService.getByUserEmail(principal.getName());
        Classroom classroom = classroomService.findByUrl(url);
        Post post = postService.getById(Long.parseLong(id));

        if (!post.isCanSubmit()) {
            attributes.addFlashAttribute("warning", "Submission is turned of please contract with your teacher.");
            return "redirect:/classroom/instruction/" + url + "/" + id;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String currentDate = sdf.format(new Date());
        String dueDate = sdf.format(post.getDueDate());

        if (currentDate.compareTo(dueDate) > 0) {
            attributes.addFlashAttribute("warning", "Submission date is over, Please contract with your teacher.");
            post.setCanSubmit(false);
            postService.save(post);
            return "redirect:/classroom/instruction/" + url + "/" + id;
        }

        // post sections
        List<Section> sectionList = post.getSections();
        SectionUser sectionUser = sectionUserService.getByUrlStudentEmail(url, user.getUsername());

        boolean exist = false;

        for (Section section : sectionList) {
            if (sectionUser.getSection() == section) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            attributes.addFlashAttribute("error", "You are not an student of this classroom!");
            return "redirect:/";
        }

        String fileName;

        // checking & uploading the content file
        if (!file.isEmpty()) {
            if (FileExtensionCheck.imageCheck(file.getOriginalFilename()))
                fileName = fileService.uploadFile(imagePath, file, "image");
            else
                fileName = fileService.uploadFile(contentPath, file, "content");
            submission.setFileName(fileName);
        }

        submission.setStudent(user);
        submission.setStatus("SUBMIT");
        submission.setPost(post);

        try {
            submissionService.save(submission);
            attributes.addFlashAttribute("success", "Your submission is successfully recorded!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Internal server error");
        }

        return "redirect:/classroom/instruction/" + url + "/" + id;
    }
}
