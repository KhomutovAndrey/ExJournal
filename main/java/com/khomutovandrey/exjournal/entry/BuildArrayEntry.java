package com.khomutovandrey.exjournal.entry;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Интерфейс описывает метод, котормы сущность может вернуть массив объектов типа самой себя,
 * с со значением полей, заданными во входящем массиве - каждый элемент которого,
 * содержит аргументы (ContentValue), для каждой создаваемой сущности.
 * Т.е. входящий аргумент, это массив со значеними полей для каждой создаваемой сущности.
 * На выходе получаем массив сущностей с соответствующими значениями полей
 */
public interface BuildArrayEntry<T> {
    //T - обощённый аргумент, предполагается, что может быть типом Journal или Zapis;
    public ArrayList<T> buildArray(ArrayList<ContentValues> values);
}
