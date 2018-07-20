package com.example.libraryapp.dto;

import javax.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue
    @Column(name = "a_id")
    private Integer aid;
    @Column(name = "a_account")
    private String aaccount;
    @Column(name = "a_password")
    private String aapassword;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getAapassword() {
        return aapassword;
    }

    public void setAapassword(String aapassword) {
        this.aapassword = aapassword;
    }

    public String getAaccount() {
        return aaccount;
    }

    public void setAaccount(String aaccount) {
        this.aaccount = aaccount;
    }
}
