package com.vsa5edu.educationID.database.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name="users")
public class User {
    @Column(name="user_id")
    @Id
    public Long id;

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "second_name")
    public String secondName;

    @Column(name = "patronymic")
    public String patronymic;

    @Column(name = "telegram")
    public String telegram;
    @Column(name = "tg_chat_id")
    public String tgChatID;
    @Column(name = "login")
    public String login;
    @JsonIgnore
    @Column(name = "password_hash")
    public String passwordHash;
    @JsonIgnore
    @Column(name = "password_salt")
    public String passwordSalt;
    @OneToOne
    @JoinColumn(name = "priority_address_id")
    public Address priorityAddress;
    @OneToOne
    @JoinColumn(name = "priority_mail_id")
    public Mail priorityMail;
    @OneToOne
    @JoinColumn(name = "priority_phone_id")
    public Phone priorityPhone;

    //mapped by key-object
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Address> addresses;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Mail> mails;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Phone> phones;
    @OneToMany
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(
                    name = "user_id", //join to users table - one
                    referencedColumnName = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "service_id", //join to service table - many
                    referencedColumnName = "service_id"
            )
    )
    public Set<TrustedService> trustedServices;
    @OneToMany
    @JoinTable(
            //transit table name
            name = "childrens",
            //one annotation
            joinColumns = @JoinColumn(
                    name = "parent_user_id",
                    referencedColumnName = "user_id"
            ),
            //many annotation
            inverseJoinColumns = @JoinColumn(
                    name = "child_user_id",
                    referencedColumnName = "user_id"
            )
    )
    public Set<User> childrens;

}
