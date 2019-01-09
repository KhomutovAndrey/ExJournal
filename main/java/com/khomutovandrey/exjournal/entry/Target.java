package com.khomutovandrey.exjournal.entry;

public class Target {
    private long id; // Идентификатор (первичный ключ в БД)
    private String name; // Наименование типа Цели
    private int count; // Количество выполнений
    private long id_jour; // Идентификатор журнала упражнений к которому относится цель

    public Target(long id, String name, int count, long id_jour) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.id_jour = id_jour;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getId_jour() {
        return id_jour;
    }

    public void setId_jour(long id_jour) {
        this.id_jour = id_jour;
    }

    /**
     * Проверяет является ли объект сохранённым или вновь созданным, по полю id/
     * Если id=-1, значит объект новый, не сохранённый в хранилище
     * @return true - если объект сохранён в хранилище
     *         false - если объект не сохранён в хранилище
     */
    public boolean isInit(){
        if(this.id!=-1){
            return true;
        }else return false;
    }
}
