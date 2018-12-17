package com.khomutovandrey.exjournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogTraining extends DialogFragment {
    Button btnTime;
    TextView tvCount;
    String sTime;
    private FirebaseAnalytics mFirebaseAnalytics;

    public interface DialogListener {
        //public String name=null;
        /**
         * Обратный вызов обработки нажатия кнопки Сохранить в диалоге.
         * @param count количество повторений упражнения за раз
         * @param sTime время выполнения подхода в текстовом виде 00:55
         */
        public void onDialogPositiveClick(int count, String sTime);
    }

    DialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_training, null);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        btnTime = (Button) view.findViewById(R.id.btnTime);
        // Диалог выбора времени - btnTime
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                // Диалоговое окно выбора времени
                TimePickerDialog tpd = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Устанавливаем выбранное время в переменные
                                sTime = String.valueOf(hourOfDay)+"."+String.valueOf(minute);
                                btnTime.setText(sTime);
                            }
                        }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
                tpd.show();
            }
        });

        // Выводим текущее время на кнопку. Время выполнения упражнения
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        sTime = dateFormat.format(new Date());
        btnTime.setText(sTime);

        // Настраиваем диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.dialog_training_title)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogTraining.this.getDialog().cancel();
                    }
                })
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String test = tvCount.getText().toString();
                        int count =0;
                        if(!test.isEmpty()) {
                            count = Integer.parseInt(tvCount.getText().toString());
                        }else  count = 0;
                        sTime = btnTime.getText().toString();
                        if (mListener != null) {
                            mListener.onDialogPositiveClick(count, sTime);
                        }
                    }
                });
        /*
        //AdMobe
        MobileAds.initialize(getActivity(), getString(R.string.admob_appid));
        AdView adView = (AdView) view.findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TrainingAddDialog");
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (DialogListener) activity;
    }

}
