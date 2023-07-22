package com.advertise.entity.advertisement;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ADVERTISING")
public class Advertising{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(name = "REFERENCE",nullable = false)
    private String reference;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    @Column(name = "START_DATE",nullable = false)
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    @Column(name = "END_DATE")
    private LocalDateTime endDate;
    @Column(name = "BUDGET")
    private BigDecimal budget;
    @Column(name = "RATE")
    private BigDecimal rate;
    @Column(name = "STATUS")
    private String status="Done";
    @Column(name = "TARGETING")
    private String targeting;
    @Column(name = "DESCRIPTION_POST")
    private String descriptionPost="";
    @Column(name = "NB_CLICKS_GENERATED")
    private Integer nbClicksGenerated=0;
    @Column(name = "DESCRIPTION_ADD")
    private String descriptionAdd="";
    @Column(name = "REMUNERATION")
    private Double remuneration=0.0;
    @Column(name = "LINK_URL_ENTERPRISE_PRODUCT")
    private String linkUrlEnterpriseProduct="";
    @Column(name = "IS_AVAILABLE")
    private Boolean isAvailable=false;
    @Column(name = "IS_HEADER_LOCATION")
    private Boolean isHeaderLocation=false;
    @Column(name = "IS_DELETED")
    private Boolean isDeleted=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "ADVERTISING_ID", nullable = false)
    private Set<Media> mediaList = new HashSet<>();
    @Column(name = "TITLE_OF_ADVERTISEMENT")
    private String title;

    @Column(name = "NAME_OF_COMPANY")
    private String companyName;
    @Column(name = "DESCRIPTION_OF_COMPANY")
    private String companyDescription;
    @Column(name = "SAMPLE_TITLE")
    private String sampleTitle;
    @Column(name = "SAMPLE_DESCRIPTION")
    private String sampleDescription;

    @Transient
    private MultipartFile primaryFile;

    @Transient
    private MultipartFile secondaryFile;

    @Transient
    private MultipartFile samplePostFile;

    public Advertising(String reference, BigDecimal budget) {
        this.reference = reference;
        this.budget = budget;
    }

    public Advertising(Long id, String reference, LocalDateTime startDate, LocalDateTime endDate, BigDecimal budget, BigDecimal rate, Boolean isHeaderLocation) {
        this.id = id;
        this.reference = reference;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.rate = rate;
        this.isHeaderLocation = isHeaderLocation;
    }
    public Advertising(String reference, LocalDateTime startDate, LocalDateTime endDate, BigDecimal budget, BigDecimal rate, Boolean isHeaderLocation,String targeting) {
        this.reference = reference;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.rate = rate;
        this.isHeaderLocation = isHeaderLocation;
        this.targeting = targeting;
    }

}
