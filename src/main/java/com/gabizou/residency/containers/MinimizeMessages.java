package com.gabizou.residency.containers;

public class MinimizeMessages {

    private String enter = "";
    private String leave = "";

    //    private int repeat = 0;
    private int id = 0;

    public MinimizeMessages(int id, String enter, String leave) {
        this.id = id;
        this.enter = enter;
        this.leave = leave;
//	repeat++;
    }

    public boolean add(String enter, String leave) {
        if (!same(enter, leave)) {
            return false;
        }
        this.enter = enter;
        this.leave = leave;
//	repeat++;
        return true;
    }

    public boolean same(String enter, String leave) {
        return this.enter.equals(enter) && this.leave.equals(leave);
    }

//    public int getRepeat() {
//	return repeat;
//    }

    public int getId() {
        return this.id;
    }

    public String getEnter() {
        return this.enter;
    }

    public String getLeave() {
        return this.leave;
    }

}
