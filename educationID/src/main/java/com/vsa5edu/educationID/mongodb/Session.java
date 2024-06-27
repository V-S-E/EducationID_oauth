package com.vsa5edu.educationID.mongodb;

import jakarta.persistence.Id;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Session {
    @Id
    public String id; //objectid
    public String sessionID;
    public String refreshToken;
    public String userAgent;
    public Long userID;
    public Integer serviceID;
    public String authorizationToken;
    public Instant startDateTime;
}
