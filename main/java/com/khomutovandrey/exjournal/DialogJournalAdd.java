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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.khomutovandrey.exjournal.entry.Journal;

public class DialogJournalAdd extends DialogFragment {
    EditText etName; // Требуется как глобальная переменная, что бы взять текст, при нажатии копки "Сохранить"
    Journal journal = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    public interface NoticeDialogListener{
        //public String name=null; 27/10/2018
        /**
         * Обратный вызов обработки нажатия кнопки Сохранить в диалоге.
         * @param id идентификатор журнала, переданного в диалог
         *           если -1, значит журнал в диалог не передавался, добавление нового
         * @param journalName новое имя для переданного урнала (в случае переименования),
         *                    либо имя для нового журнала
         */
        public void onDialogPositiveClick(long id, String journalName);
    }

    NoticeDialogListener mListener;

    /**
     * Устанавливает журнал в диалог, если диалог открывается для редактирования журнала
     * @param journal - журнал для редактирования
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_journal, null);
        etName = (EditText)view.findViewById(R.id.etName);
        if(journal!=null) {
            etName.setText(journal.getName());
        }
        builder.setView(view);
        builder.setTitle(R.string.journal_title);
        builder.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(journal==null) {// журнал в диалог не передан, значит создаём новый
                    mListener.onDialogPositiveClick(-1, etName.getText().toString());
                } else mListener.onDialogPositiveClick(journal.getId(), etName.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogJournalAdd.this.getDialog().cancel();
            }
        });

        /*
        //AdMobe
        MobileAds.initialize(getContext(), getString(R.string.admob_appid));
        AdView adView = (AdView) view.findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "JournalAddDialog");
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (NoticeDialogListener) activity;
    }

}
