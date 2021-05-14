package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);
    List<UserModel> findAllByPhoneNumber(String phoneNumber);
    List<UserModel> findAllByEmail(String email);

    @Query("select " +
            "count(roles), roles from UserModel group by roles")
    List<Object[]> findUserNumbers();
}
