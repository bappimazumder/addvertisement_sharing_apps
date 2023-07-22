package com.advertise.entity.lookup;

import com.advertise.entity.common.EntityCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REGION")
public class Region extends EntityCommon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME",nullable = false, length = 64)
    private String name;
    @Column(name = "CODE",length = 64)
    private String Code;

}