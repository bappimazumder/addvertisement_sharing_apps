package com.advertise.response.user;

import lombok.Data;

@Data
public class AccessDto {
    private Boolean manageGroupMembership;
    private Boolean view;
    private Boolean mapRoles;
    private Boolean impersonate;
    private Boolean manage;
}
