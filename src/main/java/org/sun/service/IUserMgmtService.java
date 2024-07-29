package org.sun.service;

import org.sun.binding.ActivateUser;
import org.sun.binding.LoginCredentials;
import org.sun.binding.RecoverPassword;
import org.sun.binding.UserAccount;
import org.sun.entity.UserMaster;

import java.util.List;

public interface IUserMgmtService {
    public String registerUser(UserAccount user)throws Exception;
    public String activateUser(ActivateUser user);
    public String login(LoginCredentials credentials);
    public List<UserAccount> listUsers();
    public UserAccount showUserByUserId(Integer id);
    public UserAccount showUserByEmailAndName(String email, String name);
    public String updateUser(UserAccount user);
    public String deleteUserById(Integer id);
    public String changeUserStatus(Integer id, String status);
    public String recoverPassword(RecoverPassword recover) throws Exception;

}
