package com.gabizou.residency.text.help;

public class PageInfo {

    private int totalEntries = 0;
    private int totalPages = 0;
    private int start = 0;
    private int end = 0;
    private int currentPage = 0;

    private int currentPlace = -1;

    private int perPage = 6;

    public PageInfo(int perPage, int totalEntries, int currentPage) {
        this.perPage = perPage;
        this.totalEntries = totalEntries;
        this.currentPage = currentPage;
        calculate();
    }

    private void calculate() {
        this.start = (this.currentPage - 1) * this.perPage;
        this.end = this.start + this.perPage - 1;
        if (this.end + 1 > this.totalEntries) {
            this.end = this.totalEntries - 1;
        }
        this.totalPages = (int) Math.ceil((double) this.totalEntries / (double) this.perPage);
    }

    public int getPositionForOutput(int place) {
        return this.start + place + 1;
    }

    public int getPositionForOutput() {
        return this.currentPlace + 1;
    }

    public boolean isInRange() {
        this.currentPlace++;
        return isInRange(this.currentPlace);
    }

    public boolean isInRange(int place) {
        if (place >= this.start && place <= this.end) {
            return true;
        }
        return false;
    }

    public boolean isPageOk() {
        return isPageOk(this.currentPage);
    }

    public boolean isPageOk(int page) {
        if (this.totalPages < page) {
            return false;
        }
        if (page < 1) {
            return false;
        }
        return true;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getTotalEntries() {
        return this.totalEntries;
    }

    public int getCurrentPlace() {
        return this.currentPlace;
    }

    public void setCurrentPlace(int currentPlace) {
        this.currentPlace = currentPlace;
    }
}
