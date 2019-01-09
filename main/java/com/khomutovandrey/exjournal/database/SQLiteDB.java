package com.khomutovandrey.exjournal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.khomutovandrey.exjournal.R;
import com.khomutovandrey.exjournal.entry.Target;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SQLiteDB extends SQLiteOpenHelper {
    Context context;
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ExJournal";
    public static final String TABLE_JOURNAL = "journal";
    public static final String TABLE_ZAPIS = "zapis";
    public static final String TABLE_TARGET = "target";
    public static final String TABLE_TARGET_TYPE = "target_type";
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
        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + "(id integer primary key, name text) ");// Таблица журналов
        db.execSQL("CREATE TABLE " + TABLE_ZAPIS + "(id integer primary key, id_jour integr, date text, time text, count integer) ");// Таблица упражнений
        db.execSQL("CREATE TABLE " + TABLE_TARGET_TYPE + "(id integer primary key, name text) ");// Таблица - справочник типов целей
        db.execSQL("CREATE TABLE " + TABLE_TARGET + "(id integer primary key, id_type integer, count integer, id_jour integr) ");// Таблица целей

        //добавляем стандартные значения журналов
        ContentValues values = new ContentValues();
        List<String> names = Arrays.asList(context.getResources().getStringArray(R.array.jour_names));
        for (String name : names) {
            values.put("name", name);
            db.insert(TABLE_JOURNAL, null, values);
        }
        //добавляем стандартные значения типов целей
        values.clear();
        ArrayList<String> types = new ArrayList<String>();
        types.add(context.getResources().getString(R.string.total_rep_day));
        types.add(context.getResources().getString(R.string.total_set_day));
        for(String type : types){
            values.put("name", type);
            db.insert(TABLE_TARGET_TYPE, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion){
            case 2: // Добавляем таблицу Цель и справочник типов целей
                this.db = db;
                db.execSQL("CREATE TABLE " + TABLE_TARGET_TYPE + "(id integer primary key, name text) ");// Таблица - справочник типов целей
                db.execSQL("CREATE TABLE " + TABLE_TARGET + "(id integer primary key, id_type integer, count integer, id_jour integr) ");// Таблица целей
                //добавляем стандартные значения типов целей
                ContentValues values = new ContentValues();
                ArrayList<String> types = new ArrayList<String>();
                types.add(context.getResources().getString(R.string.total_rep_day));
                types.add(context.getResources().getString(R.string.total_set_day));
                for(String type : types){
                    values.put("name", type);
                    db.insert(TABLE_TARGET_TYPE, null, values);
                }
                break;
        }
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

    /**
     * Возвращает идентификатор и название типа Целей в виде массива, содержащего элементы ContentValues
     * @return ArrayList<ContentValues> - каждый элемент содержит пару: идентификатор - название типа Цели
      */
    public ArrayList<ContentValues> getTypeTarget(){
        ArrayList<ContentValues> result = new ArrayList<>();
        ContentValues values;
        if (db == null) {
            db = getWritableDatabase();
        }
        Cursor cursor = db.query(TABLE_TARGET_TYPE, null, null,null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                values = new ContentValues();
                values.put("id",cursor.getLong(0));
                values.put("name", cursor.getString(1));
                result.add(values);
            }while (cursor.moveToNext());
        }
        return result;
    }

    /**
     * Возвращает набор значений Цели  для указанного журнала упражнений
     * @return ContentValues каждый элемент - пара (ключ-значение) : название поля - значение поля
     * id - идентификатор цели
     * id_type - идентификатор типа цели
     * count - количество выполнений
     * typeName - название типа цели
     */
    public ContentValues getTargetByJournal(long id_journal){
        if (db == null) {
            db = getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        // Получаем цель из БД, может быть только одна
        Cursor cursor = db.query(TABLE_TARGET, null,"id_jour=?",
                new String[]{String.valueOf(id_journal)},null,null,null,"1");
        if(cursor.moveToFirst()){
            do {
                values.put("id", cursor.getLong(0));
                values.put("id_type", cursor.getLong(1));
                values.put("count", cursor.getInt(2));
                values.put("id_jour", cursor.getLong(3));
            }while (cursor.moveToNext());
        }
        if(values.size()>0){
            cursor = db.query(TABLE_TARGET_TYPE, new String[]{"name"}, "id=?", new String[]{values.getAsString("id_type")},
                    null,null,null);
            if(cursor.moveToFirst()){
                values.put("typeName", cursor.getString(0));
            }
        }
        return values;
    }

    // TODO: передавать вместо объекта Target значения полей
    public long saveTarget(Target target){
        long result=-1;
        if (db == null) {
            db = getWritableDatabase();
        }
        // -- Находим идентификатор типа в таблице, если не находится, то добавляем новый тип
        long id_type = -1; // Идентификатор типа Цели
        String targetType = target.getName(); // Название типа Цели
        Cursor cursor = db.query(TABLE_TARGET_TYPE, null, "name=?", new String[]{targetType}, null, null, null);
        if(cursor.moveToFirst()){
            id_type = cursor.getLong(0);
        }else{ // Название не найдено в списке типов Целей, добавляем новый тип целей
            id_type = addTargetType(targetType);
        }

        // -- Добавляем или обновляем Цель
        //TODO: сохранить поле id_jour
        ContentValues values = new ContentValues();
        values.put("id_type", id_type);
        values.put("count", target.getCount());
        values.put("id_jour", target.getId_jour());
        if(target.getId()<0){// Новый, не сохранённый ранее объект, тогда добавляем новую запись в таблицу
            result = db.insert(TABLE_TARGET, null, values);
        }else { // Объект уже существует, есть идентификатор, тогда обновляем запись
            values.put("id", target.getId());
            result = db.update(TABLE_TARGET, values, "id=?", new String[]{String.valueOf(target.getId())});
        }
        return result;
    }

    public long addTargetType(String targetType){
        long result = -1;
        ContentValues values = new ContentValues();
        values.put("name", targetType);
        result = db.insert(TABLE_TARGET_TYPE, null, values);
        return result;
    }


}
