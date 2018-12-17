package com.khomutovandrey.exjournal;

/**
 * Описывает действия\реакцию для элементов опций(меню, подменю и т.п.),
 * вызывающих операции добавления, удаления, редактирования сущностей (например, журналы и записи)
 */
public interface OptionAct<T> {
    public void actionAdd(T element);
    public void actionRename(T element);
    public void actionDelete(T element);
}
