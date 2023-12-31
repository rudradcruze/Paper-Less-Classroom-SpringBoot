package bd.edu.diu.cis.classroom.controller;

import bd.edu.diu.cis.classroom.model.Role;
import bd.edu.diu.cis.classroom.model.User;
import bd.edu.diu.cis.classroom.repository.RoleRepository;
import bd.edu.diu.cis.classroom.repository.UserRepository;
import bd.edu.diu.cis.classroom.service.UserDetailsServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class AuthController {

    @Autowired
    private UserDetailsServiceImplement userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Principal principal, Model model) {
        if (principal != null) return "redirect:/";
        model.addAttribute("title", "PLC Login");
        model.addAttribute("logcss", "logcss");
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "PLC - Registration");
        model.addAttribute("logcss", "logcss");
        return "register";
    }

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute("user")User user,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes attributes,
                                  @RequestParam("confirmPassword") String confirmPassword) {

        model.addAttribute("logcss", "logcss");

        try {
            if (result.hasErrors()) {
                model.addAttribute("user", user);
                return "register";
            }

            User userNew = userDetailsService.getByUserEmail(user.getUsername());

            if (userNew != null) {
                model.addAttribute("error", "Email is already registered");
                model.addAttribute("user", user);
                return "register";
            }

            if (user.getFirstName().trim() == null) {
                model.addAttribute("error", "First name can not be empty");
                model.addAttribute("user", user);
                return "register";
            }

            if (user.getLastName().trim() == null) {
                model.addAttribute("error", "Last name can not be empty");
                model.addAttribute("user", user);
                return "register";
            }

            if (user.getAddress().trim() == null) {
                model.addAttribute("error", "Address can not be empty");
                model.addAttribute("user", user);
                return "register";
            }

            if (!isNumeric(user.getPhoneNumber()) && user.getPhoneNumber().length() != 11) {
                model.addAttribute("error", "Please enter 11 digit phone number & number only.");
                model.addAttribute("user", user);
                return "register";
            }

            if (user.getPassword().length() >= 5 && user.getPassword().length() <= 20) {
                if (user.getPassword().equals(confirmPassword)) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setEnable(true);
                    List<Role> roleList = user.getRoles();
                    roleList.add(roleRepository.findRoleByName("USER"));
                    user.setRoles(roleList);
                    userRepository.save(user);
                    attributes.addFlashAttribute("success", user.getFirstName() + " is successfully registered.");

                    return "redirect:/login";
                } else {
                    model.addAttribute("error", "Password is not same");
                    model.addAttribute("user", user);
                }
            } else {
                model.addAttribute("error", "Password should have 5-20 characters");
                model.addAttribute("user", user);
            }
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Server have ran some problems");
            model.addAttribute("user", user);
        }
        return "register";
    }
}
