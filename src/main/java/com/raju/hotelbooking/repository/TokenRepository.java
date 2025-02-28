package com.raju.hotelbooking.repository;

import com.raju.hotelbooking.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

//    @Query("""
//        SELECT t FROM Token t
//        WHERE t.user.id = :userId AND t.logout = false
//        """)
//    List<Token> findAllTokenByUser(Long userId);


    @Query("""
        SELECT t FROM Token t 
        WHERE t.user.id = :userId AND t.logout = false
        """)
    List<Token> findAllTokenByUser(Long userId);

}
