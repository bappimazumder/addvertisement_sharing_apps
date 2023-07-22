package com.advertise.repository;

import com.advertise.entity.user.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetails,Long> {
    UserDetails findByUsername(String username);
    UserDetails findById(long id);

}
