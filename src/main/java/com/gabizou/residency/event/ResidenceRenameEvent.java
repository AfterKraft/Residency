package com.gabizou.residency.event;

import com.gabizou.residency.api.event.ResidenceEvent;
import com.gabizou.residency.protection.ClaimedResidence;

public class ResidenceRenameEvent extends ResidenceEvent {

    protected String NewResName;
    protected String OldResName;
    protected ClaimedResidence res;

    public ResidenceRenameEvent(ClaimedResidence resref, String NewName, String OldName) {
        super("RESIDENCE_RENAME", resref);
        this.NewResName = NewName;
        this.OldResName = OldName;
        this.res = resref;
    }

    public String getNewResidenceName() {
        return this.NewResName;
    }

    public String getOldResidenceName() {
        return this.OldResName;
    }

    @Override
    public ClaimedResidence getResidence() {
        return this.res;
    }
}
