package com.ycjw.happytoreciteword;

public class ImportantWord {
    private Integer id;
    private String word;
    private String mean;
    private int readIndex;

    public ImportantWord() {
    }

    public ImportantWord(Integer id, String word, String mean, int readIndex) {
        this.id = id;
        this.word = word;
        this.mean = mean;
        this.readIndex = readIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public int getReadIndex() {
        return readIndex;
    }

    public void setReadIndex(int readIndex) {
        this.readIndex = readIndex;
    }

    @Override
    public String toString() {
        return "ImportantWord{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", mean='" + mean + '\'' +
                ", readIndex=" + readIndex +
                '}';
    }
}
