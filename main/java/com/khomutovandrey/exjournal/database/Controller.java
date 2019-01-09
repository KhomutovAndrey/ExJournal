package com.khomutovandrey.exjournal.database;

import android.content.ContentValues;

import com.khomutovandrey.exjournal.entry.Journal;
import com.khomutovandrey.exjournal.entry.Target;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

/**
 * Интерфейс описывает взаимодействие с источником данных
 */
public interface Controller {

    /**
     * Добавляет журнал, возвращает идентификатор добавленного журнала, если удачно
     * @return идентификатор, если добавление удачно, либо -1
     */
    public long addJournal(Journal journal);

    /**
     * Возвращает массив всех журналов, с заданным именем
     * @param String name - имя журнала
     * @return Массив журналов
     */
    public ArrayList<Journal> getJournals(String name);

    /**
     * Возвращает журнал, с заданным идентификатором
     * @param long id - идентификатор журнала
     * @return журнал
     */
    public Journal getJournal(long id);

    /**
     * Возвращает массив всех журналов
     * @return Массив журналов
     */
    public ArrayList<Journal> getAllJournals();

    /**
     * Удаляет журнал, с указанным идентификатором
     * @param id идентификатор журнала
     * @return количество удалённых строк, либо 0, если ничего не удалил
     */
    public long deleteJournal(long id);

    /**
     * Переименовывает журнал в хранилище, и возвращает новый(переименованный).
     * Журнал в хранилище идентифицируется по идентификатору журнала, переданного в параметре
     * @return Возвращается новый журнал
     */
    public Journal renameJournal(Journal journal);

    /**
     * Переименовывает журнал в хранилище, и возвращает новый(переименованный).
     * Журнал в хранилище идентифицируется по идентификатору, переданному в параметре
     * @return Возвращается новый журнал
     */
    public Journal renameJournal(long id, String name);

    public Zapis getZapis(long id);

    public ArrayList<Zapis> getZapisByJornal(long journalID, String sDate);

    public ArrayList<ArrayList<Zapis>> getGroupZapis(long journalId);

    public long addZapis(Zapis zapis);

    public long deleteZapis(long id);

    public long editZapis(int count, String sTime);

    /**
     * Возвращает список названий типов Целей
     * @return
     */
    public ArrayList<String> getTypeTarget();

    /**
     * Возвращает объект Цель
     * @return Target
     */
    public Target getTargetByJournal(long id_journal);

    /**
     * Сохраняет Цель в хранилище, т.к. цель только одна, то, по сути обновляет запись в хранилище
     * @return Идентификатор сохранённой Цели
     */
    public long saveTarget(Target taarget);

    //Закрывает обращение к источнику данных
    public void close();
}
