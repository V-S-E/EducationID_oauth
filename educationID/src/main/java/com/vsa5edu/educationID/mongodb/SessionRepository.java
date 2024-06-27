package com.vsa5edu.educationID.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    Session findOneByUserIDAndServiceIDAndUserAgent(Long userID, Integer serviceID, String userAgent);
    Session findOneBySessionID(String sessionID);
    Session findOneByRefreshToken(String refreshToken);
    Session findOneByAuthorizationToken(String authorizationToken);
    void deleteBySessionID(String session_id);
}
