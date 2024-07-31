package org.sun.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.sun.binding.ActivateUser;
import org.sun.binding.LoginCredentials;
import org.sun.binding.RecoverPassword;
import org.sun.binding.UserAccount;
import org.sun.entity.UserMaster;
import org.sun.repository.IUserMasterRepository;
import org.sun.utils.EmailUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserMgmtServiceImpl implements IUserMgmtService {
    // Inject Repository
    @Autowired
    private IUserMasterRepository userMasterRepo;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private Environment env;

    @Override
    public String registerUser(UserAccount user) throws Exception {
        // Convert user Account Obj data to UserMaster Obj(Entity Object Data )
        UserMaster master = new UserMaster();
        // Important:  Copy the username to the name field 
        master.setName(user.getUsername()); // Make sure name is populated
        master.setUsername(user.getUsername());
        BeanUtils.copyProperties(user, master); 
        // set the Random String of 6 chars as Password
        String tempPwd = (generateRandomPassword(6));
        // IMPORTANT: Assign the temporary password to the entity
        master.setPassword(tempPwd);
        // i should make Register user as a inactive user
        master.setActive_sw("Active");
        // save the Object
        UserMaster savedMaster = userMasterRepo.save(master);

        // Send the Email
        String subject = "User Registration Success";
        String body = readEmailMEssageBody(env.getProperty("mailbody.registeruser.location"), user.getUsername(), tempPwd);
        emailUtils.sendEmailMessage(user.getEmail(), subject, body); // Send the email here

        // Return the Mesage
        return savedMaster.getUserId() != null ? "User is Registerd Check Your Mail-ID And Click the below click and Register The Form : " + savedMaster.getUserId() : "Problem is user Registration";
    }

    @Override
    public String activateUser(ActivateUser user) {
        // Use findBy Method
        UserMaster entity = userMasterRepo.findByEmailAndPassword(user.getEmail(), user.getTempPassword());
        if (entity == null) {
            return "User Not Found for the Activation";
        } else {
            // Update both the password and active_sw status
            entity.setPassword(user.getNewPassword()); // Store the new password
            entity.setActive_sw("Active");
            // Update the Object
            UserMaster updateEntity = userMasterRepo.save(entity);
            return "User is  Activated Successfully with new Password";
        }
    }

    @Override
    public String login(LoginCredentials credentials) {
        UserMaster master = userMasterRepo.findByEmailAndPassword(
            credentials.getEmail(),
            credentials.getPassword()
        );
        if (master == null) {
            return "Invalid Credentials";
        } else {
            if (master.getActive_sw().equalsIgnoreCase("Active")) {
                return "Valid Credentials Log in Successfull";
            } else {
                return "UserAccount is not Activated";
            }
        }
    }

    @Override
    public List<UserAccount> listUsers() {
        // load all entities and converted them to UserAccount Object
        List<UserAccount> listUsers = userMasterRepo.findAll().stream().map(entity -> {
            UserAccount user = new UserAccount();
            BeanUtils.copyProperties(entity, user);
            return user;
        }).toList();

        return listUsers;
    }

    @Override
    public UserAccount showUserByUserId(Integer id) {
        // Load the user by user Id
        Optional<UserMaster> opt = userMasterRepo.findById(id);
        UserAccount account = null;
        if (opt.isPresent()) {
            account = new UserAccount();
            BeanUtils.copyProperties(opt.get(), account);
        }
        return account;
    }

    @Override
    public UserAccount showUserByEmailAndName(String email, String name) {
        // Use the custom findBy(-) method
        UserMaster master = userMasterRepo.findByNameAndEmail(name, email);
        UserAccount accout = null;
        if (master != null) {
            accout = new UserAccount();
            BeanUtils.copyProperties(master, accout);
        }
        return accout;
    }

    @Override
    public String updateUser(UserAccount user) {
        // Get UserAccount Object(nothing but Entity Object)based on name and Mail
        Optional<UserMaster> opt = userMasterRepo.findById(user.getUserId());
        if (opt.isPresent()) {
            // get Entity Object
            UserMaster master = opt.get();
            // Update the password field
            master.setPassword(user.getPassword()); // Access password directly
            // Check if the user is not yet active and update active_sw
            if (!master.getActive_sw().equalsIgnoreCase("Active")) { 
                master.setActive_sw("Active"); 
            }
            // You don't need to copy properties again since you are updating only the password
            // BeanUtils.copyProperties(user, master); 

            userMasterRepo.save(master);
            return "User Details Updated Successfully";
        }

        return "User Not Found for Updation";
    }

    @Override
    public String deleteUserById(Integer id) {
        // Load the Object
        Optional<UserMaster> opt = userMasterRepo.findById(id);
        if (opt.isPresent()) {
            userMasterRepo.delete(opt.get());
            return "User Details Deleted Successfully";
        }
        return "User Not Found for Deletion";
    }

    @Override
    public String changeUserStatus(Integer id, String status) {
        // Load the Object
        Optional<UserMaster> opt = userMasterRepo.findById(id);
        if (opt.isPresent()) {
            // get Entity Object
            UserMaster master = opt.get();
            // Change the status
            master.setActive_sw(status);
            // update the object
            userMasterRepo.save(master);
            return "User Status Changed Successfully";
        }
        return "User not Found for Changing Status";
    }

    @Override
    public String recoverPassword(RecoverPassword recover) throws Exception {
        UserMaster master = userMasterRepo.findByNameAndEmail(recover.getName(), recover.getEmail());
        if (master != null) {
            String pwd = master.getPassword();
            // send the recovered password to the email account
            String subject = " mail for password recovery";
            String mailBody = readEmailMEssageBody("recover_pwd_body.txt", recover.getName(), pwd); // private method
            emailUtils.sendEmailMessage(recover.getEmail(), subject, mailBody);
            return pwd + " Mail is sent having the recovered password";
        }
        return "User And Gmail Not Found";
    }

    // Create one method for the random String generation
    private String generateRandomPassword(int length) {

        // a list of characters to choose from in form of a string
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        // Creating a String Buffer Size of AlphaNumeric String
        StringBuilder randomWord = new StringBuilder(length);
        int i;
        for (i = 0; i < length; i++) {
            // Genrate Random Number using math.Random()(gives Psuedo  random Number 0.0 to 10.0)
            int ch = (int) (AlphaNumericStr.length() * Math.random());
// adding Random character one by one at the end of randpnword
            randomWord.append(AlphaNumericStr.charAt(ch));
        }
        return randomWord.toString();
    }

    private String readEmailMEssageBody(String fileName, String fullName, String pwd) throws Exception {
        String mailBody = null;
        String url = "http://localhost:8082/activate";
        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            StringBuffer buffer = new StringBuffer();
            String line = null;
            do {
                line = br.readLine();
                buffer.append(line);
            } while (line != null);

            mailBody = buffer.toString();
            // Correctly replace placeholders
            mailBody = mailBody.replace("{FULL-NAME}", fullName);
            mailBody = mailBody.replace("{PWD}", pwd);
            mailBody = mailBody.replace("{URL}", url);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return mailBody;

    }

    @Override
    public List<UserMaster> listUsersMaster() {
        return userMasterRepo.findAll();
    }
}