package com.advertise.entity.user;

import com.advertise.entity.advertisement.Activity;
import com.advertise.entity.advertisement.Civility;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER_DATA")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIVILITY_ID")
    private Civility civility;
    @Column(name = "PICTURE_LINK")
    private String pictureLink;
    @Column(name = "PSEUDO")
    private String pseudo;
    @Column(name = "IBAN")
    private String iBan;
    @Column(name = "TARGETING")
    private String targeting;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID")
    private Activity activity;
    @Column(name = "HOME_ADDRESS")
    private String homeAddress;

    @Column(name = "SOCIAL_MEDIA_LINKS")
    private String socialMediaLinks;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserDetails userDetails;

}

