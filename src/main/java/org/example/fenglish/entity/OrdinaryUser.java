package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "OrdinaryUser")
@PrimaryKeyJoinColumn(name = "userID")
public class OrdinaryUser extends User {

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "userMailbox")
    private String userMailbox;

    @Column(name = "userName")
    private String userName;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "RegisterTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;
}