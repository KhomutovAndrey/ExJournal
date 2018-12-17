package com.khomutovandrey.exjournal.entry;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Класс-сущность, описывающий ону запись в журнале (дневнике)
 * Содержит ссылку на журнал (дневник), дату упражнения, время, номер подхода и количество повторений упражнения
 */
public class Zapis implements BuildArrayEntry<Zapis>{
    private long id;
    private String sDate;
    private String sTime;
    private int count;
    private long journalId;

    @Override
    public ArrayList<Zapis> buildArray(ArrayList<ContentValues> valuesArray) {
        ArrayList<Zapis> arrayZapis = new ArrayList<>();
        Zapis.Builder builder = new Zapis.Builder(0, 0);
        for (ContentValues value: valuesArray) {
            builder.id(value.getAsLong("id"))
                    .journalId(value.getAsLong("id_jour"))
                    .date(value.getAsString("date"))
                    .time(value.getAsString("time"))
                    .count(value.getAsInteger("count"));
            arrayZapis.add(builder.build());
        }
        return arrayZapis;
    }

    public static class Builder{
        // Обязательные поля
        private long journalId;
        private int count;
        //Необязательные поля
        private long id;
        private String sDate;
        private String sTime;

        public Builder( int count, long journalId){
            this.count = count;
            this.journalId = journalId;
        }

        public Builder journalId(long journalId) {
            this.journalId = journalId;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder date(String sDate) {
            this.sDate = sDate;
            return this;
        }

        public Builder time(String sTime) {
            this.sTime = sTime;
            return this;
        }

        public Zapis build(){
            return new Zapis(this);
        }

    }

    private Zapis(Builder builder){
        this.id = builder.id;
        this.sDate = builder.sDate;
        this.sTime = builder.sTime;
        this.count = builder.count;
        this.journalId = builder.journalId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return sDate;
    }

    public void setDate(String sDate) {
        this.sDate = sDate;
    }

    public String getTime() {
        return sTime;
    }

    public void setTime(String sTime) {
        this.sTime = sTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getJournalId() {
        return journalId;
    }

    public void setJournal(long journalId) {
        this.journalId = journalId;
    }
}
