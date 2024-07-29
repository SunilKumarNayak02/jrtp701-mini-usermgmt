//UserMaster
package org.sun.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="JRTP_USER_MASTER_UI_DEVELOPMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMaster {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer userId;
    @Column(length = 40)
    private String name;
    @Column(length = 30)
    private String password;
    @Column(length = 50, unique = true,nullable = false)
    private String email;
    private Long mobileNo;
    private Long aadharNo;
    @Column(length = 20)
    private String gender;
    private LocalDate dob;
    @Column(length = 20)
    private String active_sw;
    @Column(length = 40, unique = true) // Assuming usernames should be unique
    private String username; // Add the username field

    //MetaData
    @CreationTimestamp
    @Column(updatable = false, insertable = true)
    private LocalDateTime createdOn;
    @UpdateTimestamp
    @Column(insertable =false, updatable = true)// at that record insertion dont insert but always update it
    private LocalDateTime updatedOn;
    @Column(length = 20)
    private String createdBy;
    @Column(length = 20)
    private String updatedBy;
}
