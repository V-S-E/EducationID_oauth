package com.vsa5edu.educationID.database.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mails")
public class Mail {
    @Column(name = "mail_id")
    @Id
    public long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
    @Column(name = "mail")
    public String mail;
}
