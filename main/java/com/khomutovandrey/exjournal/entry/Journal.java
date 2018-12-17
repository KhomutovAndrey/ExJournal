package com.khomutovandrey.exjournal.entry;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс-сущность описывающий журнал (дневник).
 * Тренировки по одному упражнению
 */
public class Journal implements BuildArrayEntry<Journal>{
    private long id; // Идентификатор (первичный ключ в БД)
    private String name; // Наименование журнала\дневника

    public Journal(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Реализация интрефейса. Возвращает массив журналов, со значениями полей,
    // заданными во входящем аргументе
    @Override
    public ArrayList<Journal> buildArray(ArrayList<ContentValues> valuesArray) {
        ArrayList<Journal> journals = new ArrayList<Journal>();
        Journal journal;

        for (ContentValues value: valuesArray) {
            journal = new Journal(value.getAsInteger("id"), value.getAsString("name"));
            journals.add(journal);
        }
        return journals;
    }
}
