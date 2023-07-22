package com.advertise.entity.user;

import com.advertise.entity.user.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "INVOICE")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "LINK_HTTP")
    private String linkHttp;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "GENERER_FACTURE")
    private Boolean genererFacture=false;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false, insertable = false)
    private UserDetails userDetails;

}
