//IUserMasterRepository
package org.sun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sun.entity.UserMaster;

import java.util.Optional;

public interface IUserMasterRepository extends JpaRepository<UserMaster, Integer> {
public UserMaster findByEmailAndPassword(String mail, String pwd);
public UserMaster findByNameAndEmail(String name, String email);
public Optional<UserMaster> findByUsername(String username);
}
