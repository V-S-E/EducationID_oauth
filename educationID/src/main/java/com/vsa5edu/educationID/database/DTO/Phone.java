package com.vsa5edu.educationID.database.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "phones")
public class Phone {
    @Column(name = "phone_id")
    @Id
    public long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
    @Column(name = "phone_number")
    public String phoneNumber;
}
