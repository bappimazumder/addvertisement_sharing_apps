package com.advertise.repository;

import com.advertise.entity.user.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository  extends JpaRepository<UserData, Long> {
    @Query("select model from UserData model join model.userDetails ud where ud.id =:userId")
    UserData findByUserId(Long userId);
}
