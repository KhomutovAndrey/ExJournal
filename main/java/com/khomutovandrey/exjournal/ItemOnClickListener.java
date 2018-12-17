package com.khomutovandrey.exjournal;

import android.view.View;

/**
 * ИНтерфейс обратного вызова для обработки события onClick по итему в списке CardView
 * @param <T> элемент списка в адаптере, описывающий сущность (Журнал, запись)
 */
public interface ItemOnClickListener<T> {
    /**
     * Событие onClick
     * @param v - view на котором произошло событие
     * @param i - позиция в списке адаптера
     * @param element - элемент списка в адаптере, описывающий сущность (Журнал, запись)
     */
    void onClick(View v, int i, T element);
}
