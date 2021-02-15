package com.salsicha.demo.LoginAuth.Repositoy;

import com.salsicha.demo.LoginAuth.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);
}
