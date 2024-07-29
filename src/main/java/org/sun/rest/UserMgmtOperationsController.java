package org.sun.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.sun.binding.ActivateUser;
import org.sun.binding.LoginCredentials;
import org.sun.binding.RecoverPassword;
import org.sun.binding.UserAccount;
import org.sun.service.IUserMgmtService;

import java.util.List;

@Controller
public class UserMgmtOperationsController {

    @Autowired
    private IUserMgmtService userService;

    @GetMapping("/") // Add this mapping for the registration form page
    public String showRegistrationForm(Model model) {
        model.addAttribute("userAccount", new UserAccount());
        return "registration"; // Return the Thymeleaf template name
    }
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("userAccount") UserAccount account, Model model) {
        try {
            String resultMsg = userService.registerUser(account);
            // --- No need to add the message to the model here ---
            // model.addAttribute("message", resultMsg);
            return "redirect:/activate"; // Redirect to /activate
        } catch (Exception e) {
            // Handle errors (You might want to redirect to an error page)
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            return "registration"; // Or redirect to an error page
        }
    }

    @GetMapping("/activate")  // Mapping for the activation form page
    public String showActivateForm(Model model) {
        model.addAttribute("activateUser", new ActivateUser());
        return "activate";
    }

    @PostMapping("/activate")
    public String activateUser(@ModelAttribute("activateUser") ActivateUser user, Model model) {
        try {
            String resultMsg = userService.activateUser(user);
            model.addAttribute("message", resultMsg);

            // Redirect to login after successful activation
            if (resultMsg.contains("Successfully")) {
                return "redirect:/login";
            } else {
                return "activate";
            }
        } catch (Exception e) {
            // Handle errors
            model.addAttribute("message", "Activation failed: " + e.getMessage());
            return "activate";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginCredentials", new LoginCredentials());
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@ModelAttribute("loginCredentials") LoginCredentials credentials,
                               Model model) {
        try {
            String resultMsg = userService.login(credentials);

            if (resultMsg.startsWith("Valid")) { // Successful login
                return "redirect:/dashboard"; // Redirect to your dashboard
            } else {
                model.addAttribute("message", resultMsg); // Login error
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            return "login";
        }
    }


    @GetMapping("/dashboard") // Mapping for the dashboard (view users page)
    public String viewUsers(Model model) {
        try {
            List<UserAccount> users = userService.listUsers(); // Fetch user data from service
            model.addAttribute("users", users);
            return "view-users"; // Return the Thymeleaf template name
        } catch (Exception e) {
            // Handle exceptions (e.g., add an error message to the model)
            return "error"; // Or redirect to an error page
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            UserAccount user = userService.showUserByUserId(id);
            model.addAttribute("userAccount", user); // Use the same model attribute name as registration
            return "registration"; // Reuse the registration form for editing
        } catch (Exception e) {
            // Handle exceptions (add error message to the model)
            return "error"; // Or redirect to an error page
        }
    }


@GetMapping("/report")
    public ResponseEntity<?>showUsers(){
       try{
           List<UserAccount> list=userService.listUsers();
           return new ResponseEntity<List<UserAccount>>(list,HttpStatus.OK);
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<?>showUserById(@PathVariable Integer id){
        try{
            UserAccount account=userService.showUserByUserId(id);
            return new ResponseEntity<UserAccount>(account,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find/{email}/{name}")
    public  ResponseEntity<?>showUserByMailAndName(@PathVariable String email,@PathVariable String name){
        try{
            UserAccount account=userService.showUserByEmailAndName(email, name);
            return new ResponseEntity<UserAccount>(account,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String>updateUserDeatails(@RequestBody UserAccount account){
      try{
            String resultMsg=userService.updateUser(account);
            return new ResponseEntity<String>(resultMsg,HttpStatus.OK);
      }catch (Exception e){
          e.printStackTrace();
          return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
        try{
            String resultMsg=userService.deleteUserById(id);
            return new ResponseEntity<String>(resultMsg,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changeStatus/{id}/{status}")
    public String changeStatus(@PathVariable Integer id, @PathVariable String status, Model model){
        try {
            String resultMsg = userService.changeUserStatus(id, status);
            model.addAttribute("message", resultMsg);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/recoverPassword")
    public ResponseEntity<String> recoverPassword(@RequestBody RecoverPassword recover){
        try {
            String resultMsg=userService.recoverPassword(recover);
            return new ResponseEntity<String>(resultMsg,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}//class
