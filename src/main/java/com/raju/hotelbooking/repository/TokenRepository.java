package com.raju.hotelbooking.repository;

import com.raju.hotelbooking.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Query("""
            Select t from Token t inner join User u on t.user.id u.id
            where t.user.id = :userId and t.logout=false
            """)
    List<Token> findAllTokenByUser(Long userId);
}
