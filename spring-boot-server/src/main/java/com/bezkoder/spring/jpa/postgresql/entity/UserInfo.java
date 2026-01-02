package com.bezkoder.spring.jpa.postgresql.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="org_id", nullable=false)
    private Long orgId;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String uname;

    @Column(nullable=false)
    private String firstname;

    @Column(nullable=false)
    private String lastname;

    @Column(name="is_company_user", nullable=false)
    private boolean isCompanyUser;

    @Column(name="created_time", nullable=false)
    private OffsetDateTime createdTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public boolean isCompanyUser() { return isCompanyUser; }
    public void setCompanyUser(boolean companyUser) { isCompanyUser = companyUser; }
    public OffsetDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(OffsetDateTime createdTime) { this.createdTime = createdTime; }
}
