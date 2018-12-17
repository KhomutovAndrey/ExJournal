package com.khomutovandrey.exjournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * Диалоговое окно подтверждения при операции удаления.
 * Поле name - содержит название удаляемого объекта, дл вывода в сообщении
 */
public class DialogAccept extends DialogFragment {
    public final String nameKey = "name";// Ключ аргумента Bundle для извлечения названия, переданного в диалог из основного экрана
    public final String idKey = "id";// Ключ аргумента Bundle для идентификатора объекта удаления, переданного в диалог из основного экрана

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(long id);
    }

    NoticeDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Bundle arg = getArguments();
            String name = getArguments().getString(nameKey);
            final long id = getArguments().getLong(idKey);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.question_delete)
                    .setMessage(name)
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogAccept.this.getDialog().cancel();
                        }
                    })
                    .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mListener!=null) {
                                mListener.onDialogPositiveClick(id);
                            }
                        }
                    });

            return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
            mListener = (NoticeDialogListener) activity;
    }
}
