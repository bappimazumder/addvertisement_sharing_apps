package com.advertise.entity.advertisement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMPANY")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "LOGO_LINK")
    private String logoLink;
    @Column(name = "BUDGET")
    private BigDecimal budget;

    @Column(name = "DESCRIPTION_OF_COMPANY")
    private String companyDescription;
    @OneToMany(mappedBy = "company")
    private Set<Advertising> advertisements = new HashSet<>();

}
