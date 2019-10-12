package com.ycjw.happytoreciteword;

public class Information {
    private int remianDays;
    private int recited_words;
    private int all_words;

    public Information() {
    }

    public Information(int remianDays, int recited_words, int all_words) {
        this.remianDays = remianDays;
        this.recited_words = recited_words;
        this.all_words = all_words;
    }

    public int getRemianDays() {
        return remianDays;
    }

    public void setRemianDays(int remianDays) {
        this.remianDays = remianDays;
    }

    public int getRecited_words() {
        return recited_words;
    }

    public void setRecited_words(int recited_words) {
        this.recited_words = recited_words;
    }

    public int getAll_words() {
        return all_words;
    }

    public void setAll_words(int all_words) {
        this.all_words = all_words;
    }

    @Override
    public String toString() {
        return "Information{" +
                "remianDays=" + remianDays +
                ", recited_words=" + recited_words +
                ", all_words=" + all_words +
                '}';
    }
}
