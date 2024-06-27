package com.vsa5edu.educationID.database.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trusted_services")
public class TrustedService {
    @Id
    @Column(name = "service_id")
    public int id;
    @Column(name = "root_domain")
    public String domainName;
    @Column(name = "name")
    public String name;
    @Column(name = "redirect_url")
    public String redirectUrl;
    @JsonIgnore
    @Column(name = "service_token")
    public String token;
}
