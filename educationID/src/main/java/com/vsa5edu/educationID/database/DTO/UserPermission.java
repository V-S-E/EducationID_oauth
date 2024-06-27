package com.vsa5edu.educationID.database.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "users_permissions")
public class UserPermission {
    @Id
    @Column(name = "permission_id")
    public long id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
    @ManyToOne
    @JoinColumn(name = "service_id")
    public TrustedService service;
}
