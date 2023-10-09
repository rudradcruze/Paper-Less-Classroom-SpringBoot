package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.*;
import bd.edu.diu.cis.classroom.service.*;
import bd.edu.diu.cis.classroom.utils.FileExtensionCheck;
import bd.edu.diu.cis.classroom.utils.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    private boolean checkStudentExist(Post post, String url, String userName) {
        List<Section> sectionList = post.getSections();
        SectionUser sectionUser = sectionUserService.getByUrlStudentEmail(url, userName);

        boolean exist = false;

        for (Section section : sectionList) {
            if (sectionUser.getSection() == section) {
                exist = true;
                break;
            }
        }

        return !exist;
    }

    @PostMapping("/classroom/post/{url}/{id}/submission/save")
    public String saveSubmission(@ModelAttribute Submission submission,
                                 @PathVariable String id,
                                 @PathVariable String url,
                                 @RequestParam(value = "submissionId", required = false) String submissionId,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 Principal principal,
                                 RedirectAttributes attributes) {

        if (principal == null) return "redirect:/login";

        if (!submissionId.isBlank() || !submissionId.isEmpty()) {
            Submission newSubmission = submissionService.getById(Long.parseLong(submissionId));
            newSubmission.setContent(submission.getContent());
            submission = newSubmission;
        }

        User user = userService.getByUserEmail(principal.getName());
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
        if (checkStudentExist(post, url, user.getUsername())) {
            attributes.addFlashAttribute("error", "You are not an student of this classroom!");
            return "redirect:/";
        }

        String fileName;

        // checking & uploading the content file
        if (!file.isEmpty()) {
            if (submission.getFileName() != null && submissionId != null) {
                try {
                    boolean delete = fileService.deleteFile(submission.getFileName());
                } catch (IOException e) {
                    attributes.addFlashAttribute("error", "Previous File is not deleted!");
                    return "redirect:/classroom/instruction/" + url + "/" + id;
                }
            }

            boolean extensionCheck = FileExtensionCheck.imageCheck(Objects.requireNonNull(file.getOriginalFilename()));
            if (extensionCheck)
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

    @GetMapping("/classroom/post/{url}/{id}/submission/status")
    public String updateStatus(@PathVariable String id,
                               @PathVariable String url,
                               Principal principal,
                               @RequestParam(value = "submissionId") String submissionId,
                               RedirectAttributes attributes) {

        if (submissionId == null) {
            attributes.addFlashAttribute("error", "Internal Server Error! Submission is not detected!");
            return "redirect:/classroom/instruction/" + url + "/" + id;
        }

        Post post = postService.getById(Long.parseLong(id));

        if (checkStudentExist(post, url, principal.getName())) {
            attributes.addFlashAttribute("error", "You are not an student of this classroom!");
            return "redirect:/";
        }

        Submission submission = submissionService.getById(Long.parseLong(submissionId));

        if (Objects.equals(submission.getStatus(), "SUBMIT")) {
            submission.setStatus("UNSUBMIT");
            try {
                submissionService.save(submission);
                attributes.addFlashAttribute("success", "Submission is successfully un-submit");
            } catch (Exception e) {
                attributes.addFlashAttribute("error", "Internal server error");
            }
        }
        else {
            attributes.addFlashAttribute("warning", "Not submit yet.");
        }
        return "redirect:/classroom/instruction/" + url + "/" + id;
    }
}
