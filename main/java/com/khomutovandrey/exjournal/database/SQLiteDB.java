package com.khomutovandrey.exjournal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.khomutovandrey.exjournal.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SQLiteDB extends SQLiteOpenHelper {
    Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ExJournal";
    public static final String TABLE_JOURNAL = "journal";
    public static final String TABLE_ZAPIS = "zapis";
    //public static final String TABLE_JOURNAL = "journal";
    SQLiteDatabase db;

    // TODO: доработать
    /**
     * Интерфейс построителя условия для формирования условия where к запросу
     */
    public interface BuilderWhere {
        public final String _AND = "and";
        public final String _OR = "or";


        /**
         * Метод добавляет условие
         * @param arg HashMap<String, String> данные для одного условия
         *            название столбца
         *            значение для этого столбца
         * @param operator оператор для данного условия (or\and)
         * @return
         */
        public BuilderWhere add(HashMap<String, String> arg, String operator);

        /**
         * Метод возвращает строку-условие для подстановки в метод query
         * @return строка-запрос
         */
        public String where();

        /**
         * Возвращает строковый массив аргументов для условия
         * @return
         */
        public String[] params();
    }


    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**Создаём БД, заполняем предустановленными журналами
         * названия журналов в ресурсах
         */
        this.db = db;
        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + "(id integer primary key, name text) ");
        db.execSQL("CREATE TABLE " + TABLE_ZAPIS + "(id integer primary key, id_jour integr, date text, time text, count integer) ");

        //добавляем стандартные значения
        ContentValues values = new ContentValues();
        List<String> names = Arrays.asList(context.getResources().getStringArray(R.array.jour_names));
        for (String name : names) {
            values.put("name", name);
            db.insert(TABLE_JOURNAL, null, values);
        }
        //insertToJournal(values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addToJournal(ContentValues values) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.insert(TABLE_JOURNAL, null, values);
    }

    public ArrayList<ContentValues> getAllJournals() {
        if (db == null) {
            db = getWritableDatabase();
        }
        ArrayList<ContentValues> result = new ArrayList<ContentValues>();
        ContentValues values;
        Cursor cursor = db.query(TABLE_JOURNAL, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                values = new ContentValues();
                values.put("id", cursor.getString(0));
                values.put("name", cursor.getString(1));
                result.add(values);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Удаляет журнал с идентификатором, переданным в параметра
     *
     * @param id идентификатор журнала для удаления
     * @return количество удалённых записей, либо 0, если ничего не удалил
     */
    public long deleteJournal(long id) {
        if (db == null) {
            db = getWritableDatabase();
        }

        long result = db.delete(TABLE_JOURNAL, "id=?", new String[]{String.valueOf(id)});
        if(result>0){
            // Удалить связанные записи из таблицы Zapis
            db.delete(TABLE_ZAPIS, "id_jour=?", new String[]{String.valueOf(id)});
        }
        return result;
    }

    public long renameJournal(long id, String name) {
        if (db == null) {
            db = getWritableDatabase();
        }
        ContentValues cv = new ContentValues();
        //cv.put("id",id);
        cv.put("name", name);
        return db.update(TABLE_JOURNAL, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<ContentValues> getJournal(long id) {
        if (db == null) {
            db = getWritableDatabase();
        }
        ArrayList<ContentValues> result = new ArrayList<>();
        ContentValues cv;
        Cursor cursor = db.query(TABLE_JOURNAL, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        //Cursor cursor = db.rawQuery("select * from "+ TABLE_JOURNAL + " where id=?",arg);
        if (cursor.moveToFirst()) {
            do {
                cv = new ContentValues();
                cv.put("id", cursor.getLong(0));
                cv.put("name", cursor.getString(1));
                result.add(cv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public long addZapis(long id, long idJournal, String sDate, String sTime, int count) {
        if (db == null) {
            db = getWritableDatabase();
        }
        ContentValues cv = new ContentValues();
        //cv.put("id", id);
        cv.put("id_jour", idJournal);
        cv.put("date", sDate);
        cv.put("time", sTime);
        cv.put("count", count);
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.insert(TABLE_ZAPIS, null, cv);
    }

    public ArrayList<ContentValues> getZapisByJournal(long idJournal, @NonNull String sDate) {
        if (db == null) {
            db = getWritableDatabase();
        }
        ArrayList<ContentValues> result = new ArrayList<>();
        ContentValues cv;
        //String[] columns = new String[]{"id" , "id_jour", "date", "time", "count", "sum(count) as sum"};
        Cursor cursor = db.query(TABLE_ZAPIS, null, "id_jour=? and date = ?",
                new String[]{String.valueOf(idJournal), sDate}, null, null, "time ASC");

        if(cursor.moveToFirst()){
            do {
                cv = new ContentValues();
                cv.put("id", cursor.getLong(0));
                cv.put("id_jour", cursor.getLong(1));
                cv.put("date", cursor.getString(2));
                cv.put("time", cursor.getString(3));
                cv.put("count", cursor.getLong(4));
                //cv.put("sum", cursor.getInt(5));
                result.add(cv);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public long deleteZapis(long id){
        long result = 0;
        if (db == null) {
            db = getWritableDatabase();
        }
        result = db.delete(TABLE_ZAPIS, "id=?", new String[]{String.valueOf(id)});
        return result;
    }

    public ArrayList<String> getAllDateZapis(long jourId){
        if (db == null) {
            db = getWritableDatabase();
        }
        ArrayList<String> result = new ArrayList<>();

        // Получаем список всех дат, по указанному журналу
        Cursor cursor = db.query(TABLE_ZAPIS, new String[]{"date"}, "id_jour=?",
                new String[]{String.valueOf(jourId)}, "date", null, "date DESC");
        if (cursor.moveToFirst()){
            do {
                result.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        return result;
    }


}
