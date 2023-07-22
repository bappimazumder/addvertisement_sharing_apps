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
@Table(name = "MEDIA")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "LINK_HTTP",nullable = false)
    private String linkHttp;
    @Column(name = "IMAGE_CATEGORY",nullable = false)
    private Integer imageCategory;

    @Column(name = "FORMAT_IMAGE")
    private String formatImage;

    @Column(name = "FORMAT_VIDEO")
    private String formatVideo;

    @Column(name = "RESOLUTION")
    private String resolution;

    @Column(name = "CUT")
    private String cut;

    @Column(name = "FILE_TYPE")
    private String fileType;

    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "DURATION")
    private String duration;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "ADVERTISING_ID", nullable = false, updatable = false, insertable = false)
    private Advertising advertising;
}
