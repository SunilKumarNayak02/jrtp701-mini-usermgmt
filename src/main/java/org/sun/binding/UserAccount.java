package org.sun.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    private Integer userId; 
    private String username; 
    private String email;
    private Long mobileNo;
    private String gender; 
    private LocalDate dob=LocalDate.now();
    private Long aadharNo;
    // Add the password field to UserAccount
    private String password; 
}