package com.gabizou.residency.containers;

import com.gabizou.residency.permissions.PermissionGroup;

public class FlagEnum {

    private PermissionGroup group;
    private long time;

    public FlagEnum(PermissionGroup group, long time) {
        this.group = group;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public PermissionGroup getGroup() {
        return this.group;
    }
}
