package com.advertise.entity.advertisement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LOCATION")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "DISPLAY_ADVERTISING_VEDIO_OR_IMAGE")
    private String displayAdvertising;
    @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    @Column(name = "START_DATE",nullable = false)
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    @Column(name = "END_DATE",nullable = false)
    private LocalDateTime endDate;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "ADVERTISING_ID", nullable = false, updatable = false, insertable = false)
    private Advertising advertising;
}
