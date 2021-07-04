package com.hugosilva.curso.ws.repository;

import com.hugosilva.curso.ws.domain.User;
import com.hugosilva.curso.ws.domain.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
