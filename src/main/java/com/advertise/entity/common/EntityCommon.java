package com.advertise.entity.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class EntityCommon implements Serializable {
    @Column(name = "CREATED_BY")
    protected String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME")
    protected Date createTime;

    @Column(name = "EDITED_BY")
    protected String editedBy;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EDIT_TIME")
    protected Date editTime;

    @Version
    @Column(name = "INTERNAL_VERSION")
    protected Long version;

    public abstract Long getId();

    public abstract void setId(Long id);

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @PrePersist
    public void beforeSave() {
        if (this.createTime == null) {
            this.createTime = new Date();
        }
    }

    @PreUpdate
    public void beforeEdit() {
        this.editTime = new Date();
    }

    @PreRemove
    public void beforeDelete() {
        this.editTime = new Date();
    }
}