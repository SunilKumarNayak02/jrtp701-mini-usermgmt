//UserAccount
package org.sun.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
    private Integer userId; // will not be filled during user registration
    private String username;
    private String name;
    private String email;
    private Long mobileNo;
    private String gender ="Female"; //by default Female will come
    private LocalDate dob=LocalDate.now();//when dob launched it should point to system date and time
    private Long aadharNo;
    private String active_sw;


}
