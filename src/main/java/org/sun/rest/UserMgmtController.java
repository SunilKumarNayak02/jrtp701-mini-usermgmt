package org.sun.rest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.sun.binding.ActivateUser;
import org.sun.binding.LoginCredentials;
import org.sun.binding.RecoverPassword;
import org.sun.binding.UserAccount;
import org.sun.entity.UserMaster;
import org.sun.service.IUserMgmtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserMgmtController {

    @Autowired
    private IUserMgmtService userService;
    
   

    @GetMapping("/")
    public String registerUserForm(Model model) {
        model.addAttribute("userAccount", new UserAccount());
        return "registration";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserAccount account, Model model) {
        try {
            String resultMsg = userService.registerUser(account);
            model.addAttribute("message", resultMsg);
            return "registration";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "User registration failed! Please try again.");
            return "registration";
        }
    }

    @GetMapping("/activate")
    public String activateUserForm(Model model) {
        model.addAttribute("activateUser", new ActivateUser());
        return "activation";
    }

    @PostMapping("/activate")
    public String activateUser(@ModelAttribute ActivateUser activateUser, Model model, RedirectAttributes redirectAttributes) {
        try {
            String resultMsg = userService.activateUser(activateUser);
            redirectAttributes.addFlashAttribute("message", resultMsg);
            // Redirect to the "view_user" page
            return "redirect:/users";  // Corrected redirection
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Activation failed. Please try again.");
            return "activation"; 
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            return "redirect:/users";
        }
        model.addAttribute("loginCredentials", new LoginCredentials());
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@ModelAttribute LoginCredentials credentials, Model model, HttpServletRequest request) {
        try {
            String resultMsg = userService.login(credentials);
            if (resultMsg.equals("Valid Credentials Log in Successfull")) {
                // Store the user's session information
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUser", credentials.getEmail());
                return "redirect:/users";
            } else {
                model.addAttribute("message", resultMsg);
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Login failed. Please try again.");
            return "login";
        }
    }


    @GetMapping("/users")
    public String showUsers(Model model) {
        List<UserMaster> userMasters = userService.listUsersMaster(); 
        model.addAttribute("userMasters", userMasters);
        return "view_users"; // This should be the correct path to your template
    }

    @GetMapping("/logout") 
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); 
        }
        return "redirect:/login";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        UserAccount user = userService.showUserByUserId(id);
        if (user != null) {
            model.addAttribute("userAccount", user);
            return "edit_user";
        } else {
            return "redirect:/users"; // Handle user not found scenario
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserAccount user, Model model, RedirectAttributes redirectAttributes) {
        try {
            String resultMsg = userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message", resultMsg);
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Update failed. Please try again.");
            return "edit_user";
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            String resultMsg = userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", resultMsg);
            return "redirect:/users"; // Redirect to the users page after deletion
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Delete failed. Please try again.");
            return "redirect:/users";
        }
    }

    @PostMapping("/changeStatus/{id}/{status}")
    public String changeUserStatus(@PathVariable Integer id, @PathVariable String status, RedirectAttributes redirectAttributes) {
        try {
            String resultMsg = userService.changeUserStatus(id, status);
            redirectAttributes.addFlashAttribute("message", resultMsg);
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Status change failed. Please try again.");
            return "redirect:/users";
        }
    }

    @GetMapping("/recoverPassword")
    public String recoverPasswordForm(Model model) {
        model.addAttribute("recoverPassword", new RecoverPassword());
        return "recover_password";
    }

    @PostMapping("/recoverPassword")
    public String recoverPassword(@ModelAttribute RecoverPassword recover, Model model) {
        try {
            String resultMsg = userService.recoverPassword(recover);
            model.addAttribute("message", resultMsg);
            return "recover_password";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Password recovery failed. Please try again.");
            return "recover_password";
        }
    }
}