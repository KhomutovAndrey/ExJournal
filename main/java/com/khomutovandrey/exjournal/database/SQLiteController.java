package com.khomutovandrey.exjournal.database;

import android.content.ContentValues;
import android.content.Context;

import com.khomutovandrey.exjournal.entry.Journal;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

/**
 * Взаимодействие с источником данных - БД SQLite
 */
public class SQLiteController implements Controller{
    private Context context;
    private SQLiteDB db;

    public SQLiteController(Context context){
        this.context = context;
        db = new SQLiteDB(context);
    }

    @Override
    public long addJournal(Journal journal) {
        ContentValues values = new ContentValues();
        values.put("name",journal.getName());
        return db.addToJournal(values);
    }

    @Override
    public ArrayList<Journal> getJournals(String name) {
        return null;
    }

    @Override
    public Journal getJournal(long id) {
        ContentValues cv = db.getJournal(id).get(0);
        Journal journal = new Journal(cv.getAsLong("id"), cv.getAsString("name"));
        return journal;
    }

    @Override
    public ArrayList<Journal> getAllJournals() {
        ArrayList<ContentValues> values = db.getAllJournals();
        Journal journal = new Journal(0, null);

        //Получаем массив журналов с заданными значениями полей
        ArrayList<Journal> journals = journal.buildArray(values);
        return journals;
    }

    @Override
    public long deleteJournal(long id) {
        return db.deleteJournal(id);
    }

    @Override
    public Journal renameJournal(Journal journal) {

        return null;
    }

    @Override
    public Journal renameJournal(long id, String name) {
        long result = db.renameJournal(id, name);
        if(result>0){
            return new Journal(id, name);
        }
        return null;
    }

    @Override
    public Zapis getZapis(long id) {
        return null;
    }

    @Override
    public ArrayList<Zapis> getZapisByJornal(long journalID, String sDate) {
        ArrayList<ContentValues> cvArray = db.getZapisByJournal(journalID, sDate);
        Zapis zapis = new Zapis.Builder(0,0).build();
        ArrayList<Zapis> result = zapis.buildArray(cvArray);
        return result;
    }

    @Override
    public ArrayList<ArrayList<Zapis>> getGroupZapis(long journalId) {
        //ArrayList<Zapis> groupZapis = new ArrayList<>();
        ArrayList<ArrayList<Zapis>> arrayZapis = new ArrayList<>();
        ArrayList<String> dateArray = new ArrayList<>();

        // Получаем список дат указанного журнала
        dateArray = db.getAllDateZapis(journalId);
        // Получаем массив зуписей за каждую дату
        if(dateArray.size()>0) {
            for (String sDate : dateArray) {
                arrayZapis.add(getZapisByJornal(journalId, sDate));
            }
        }
        return arrayZapis;
    }

    @Override
    public long addZapis(Zapis zapis) {
        if(zapis.getJournalId()>0) {
            return db.addZapis(-1, zapis.getJournalId(), zapis.getDate(), zapis.getTime(), zapis.getCount());
        } else return -1;
    }

    @Override
    public long deleteZapis(long id) {
        return db.deleteZapis(id);
    }

    @Override
    public long editZapis(int count, String sTime) {
        return 0;
    }

    @Override
    public void close() {
        if(db!=null) {
            db.close();
        }
    }
}
