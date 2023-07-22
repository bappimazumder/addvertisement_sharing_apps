package com.advertise.entity.advertisement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONSUMER")
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "ADVERTISING_ID", nullable = false, updatable = false, insertable = false)
    private Advertising advertising;
}
