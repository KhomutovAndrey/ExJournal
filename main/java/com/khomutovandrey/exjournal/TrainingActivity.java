package com.khomutovandrey.exjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.khomutovandrey.exjournal.database.SQLiteController;
import com.khomutovandrey.exjournal.entry.Journal;
import com.khomutovandrey.exjournal.entry.Target;
import com.khomutovandrey.exjournal.entry.Zapis;
import com.khomutovandrey.exjournal.tabs.HistoryFragment;
import com.khomutovandrey.exjournal.tabs.ToDayFragment;
import com.khomutovandrey.exjournal.tabs.ViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrainingActivity extends AppCompatActivity
        implements OptionAct<Zapis>,
        DialogTraining.DialogListener,
        DialogAccept.NoticeDialogListener,
        ItemOnClickListener<Zapis>{

    public final static String JournalId="id";
    private long idJournal=-1;
    private Journal journal;
    private Target target;
    private SQLiteController controller;
    ArrayList<Zapis> zapisArray;
    ArrayList<ArrayList<Zapis>> groupZapisArray;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    ToDayFragment zfragment;
    HistoryFragment hfragment;
    private TextView tvJournalName;
    private ImageButton targetButton; // Кнопка обработки редактирования и сохранения цели
    private boolean targetFlag=false; // Флаг состояния доступности для редактирования панели Цели
    private String sDate; // Текущая дата в текстовом формате гггг.мм.дд - для фрмирования списка подходов за сегодня(наполнение списка первой вкладки)
    private String TAG="Testfragment";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Activity-onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd(null);
            }
        });

        // Получаем идентификатор журнала
        Intent intent = getIntent();
        idJournal = intent.getLongExtra(JournalId, -1);
        // Получаем идентификатор из сохранённого состояния, если активность была пересоздана
        if(savedInstanceState!=null){
            idJournal = savedInstanceState.getLong(JournalId);
        }
        tvJournalName = (TextView)findViewById(R.id.tvJournalName);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        targetButton = (ImageButton)findViewById(R.id.targetButton);
        targetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetClick();
            }
        });

        controller = new SQLiteController(this);

        // Определяем текущую дату, для фрмирования списка подходов за сегодня(наполнение списка первой вкладки)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        sDate = dateFormat.format(new Date());

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //AdMobe
        MobileAds.initialize(this, getString(R.string.admob_appid));
        AdView adView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, this.getTitle().toString());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    @Override
    protected void onResume() {
        Log.d(TAG,"Activity-onResume:");
        super.onResume();
        if(idJournal>-1){
            initData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(JournalId, idJournal);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(journal!=null){
            idJournal = journal.getId();
        }
        savedInstanceState.getLong(JournalId, idJournal);
    }

    private void initData(){
        //Log.d(TAG,"Activity-initData:");
        // Получаем журнал
        journal = controller.getJournal(idJournal);
        String title = getString(R.string.title_activity_training)+journal.getName();
        setTitle(title);
        tvJournalName.setText(journal.getName());
        // Получаем записи журнала
        groupZapisArray = controller.getGroupZapis(journal.getId());
        zapisArray = controller.getZapisByJornal(journal.getId(), sDate);

        //pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(zfragment == null) {
            zfragment = ToDayFragment.newInstance(journal.getId(), sDate);
            zfragment.setOptionListener(this);
            zfragment.setZapisArray(zapisArray);
            pagerAdapter.addFragment(zfragment, getString(R.string.to_day));
        }//zfragment.onAttach(this);
        if(hfragment == null) {
            hfragment = HistoryFragment.newInstance(journal.getId());//groupZapisArray
            hfragment.setData(groupZapisArray);
            //hfragment.setData(groupZapisArray);
            pagerAdapter.addFragment(hfragment, getString(R.string.history));
        }
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Настраиваем вид панели с целью
        targetFlag = false;
        RadioGroup rgTarget = (RadioGroup)findViewById(R.id.rgTarget);
        //rgTarget.setEnabled(targetFlag);
        ArrayList<String> radioList = controller.getTypeTarget();
        for(String name: radioList){
            RadioButton rb = new RadioButton(this);
            rb.setEnabled(targetFlag);
            rb.setTextSize(10);
            rb.setText(name);
            rgTarget.addView(rb);
        }
        //Отметить выбранную цель по данным из хранилища
        target = controller.getTargetByJournal(journal.getId());
        EditText etTarget = (EditText)findViewById(R.id.etTarget);
        etTarget.setEnabled(targetFlag);
        if(target.isInit()){
            etTarget.setText(String.valueOf(target.getCount()));
            // Отмечаем соответствующий radioButton
            RadioButton rb;
            for (int i = 0; i < rgTarget.getChildCount(); i++){
                rb = (RadioButton)rgTarget.getChildAt(i);
                if(rb.getText().equals(target.getName())){
                    rb.setChecked(true);
                    break;
                }
            }
        }else{//Цель не задана

        }
    }

    /**
     * Обработчик нажатия кнопки редактирования цели
     */
    private void targetClick() {
        RadioGroup rgTarget = (RadioGroup)findViewById(R.id.rgTarget);
        EditText etTarget = (EditText)findViewById(R.id.etTarget);
        targetFlag = !targetFlag;
        for (int i=0; i<rgTarget.getChildCount(); i++){
            rgTarget.getChildAt(i).setEnabled(targetFlag);
        }
        etTarget.setEnabled(targetFlag);
        if (targetFlag){// Разрешено редактирование
            targetButton.setImageResource(android.R.drawable.ic_menu_save);
        }else{// Запрещено редактирование, сохраняем параметры Цели
            targetButton.setImageResource(android.R.drawable.ic_menu_edit);
            // сохранить значения цели
            target.setCount(Integer.parseInt(etTarget.getText().toString()));
            target.setId_jour(journal.getId());
            if(rgTarget.getCheckedRadioButtonId()==-1){
                rgTarget.check(0);
            }// Получаем отмеченный тип цели
            RadioButton rb = (RadioButton)findViewById(rgTarget.getCheckedRadioButtonId());
            String s = rb.getText().toString();
            //RadioButton rb = (RadioButton)rgTarget.getChildAt(rgTarget.getCheckedRadioButtonId());
            target.setName(rb.getText().toString());

            // Передаём объект на сохранение
            controller.saveTarget(target);
        }
    }


    /**
     * Обработка пункта Добавить в контекстном меню
     * @param element
     */
    @Override
    public void actionAdd(Zapis element) {
        DialogFragment dialog = new DialogTraining();
        dialog.onAttach(this);
        dialog.show(getSupportFragmentManager(),"training");
    }

    @Override
    public void actionRename(Zapis element) {

    }

    /**
     * Обработка пункта Удалить в контекстном меню
     * @param element клемень для удаления
     */
    @Override
    public void actionDelete(Zapis element) {
        DialogAccept dialog = new DialogAccept();
        Bundle arg = new Bundle();
        StringBuilder name = new StringBuilder().append(getString(R.string.question_delete))
                .append(": ").append(element.getCount()).append(" ").append(element.getTime());
        arg.putLong(dialog.idKey, element.getId());
        arg.putString(dialog.nameKey, name.toString());
        dialog.setArguments(arg);
        dialog.show(getSupportFragmentManager(),"deleteToDay");
    }

    /**
     * Обработка нажатия кнопки Сохранить в диалоге добавления записи
     * @param count количество повторений упражнения за раз
     * @param sTime время выполнения подхода в текстовом виде 00:55
     */
    @Override
    public void onDialogPositiveClick(int count, String sTime) {
        if(journal!=null){
            Zapis.Builder builder = new Zapis.Builder(count, journal.getId())
                    .date(sDate)
                    .time(sTime);
            Zapis zapis = builder.build();
            controller.addZapis(zapis);
        }
        // Показать обновлённый список во вкладке и суммарные данные
        //pagerAdapter.notifyDataSetChanged();
        refreshToDay();
    }

    /**
     * Обработка нажатия кнопки согласия в диалоге подтверждения удаления
     * @param id идентификатор записи для удаления
     */
    @Override
    public void onDialogPositiveClick(long id) {
        controller.deleteZapis(id);
        refreshToDay();
    }

    /**
     * Костыль для обновления данных на вкладках,
     * отрабатывающий и при пересоздании активности и фрагмента, например при поворте экрана
     */
    private void refreshToDay(){
        Log.d(TAG,"Activity-refreshToDay:");
        //zfragment.reFreshView(zapisArray);
        zapisArray = controller.getZapisByJornal(journal.getId(), sDate);
        groupZapisArray = controller.getGroupZapis(journal.getId());
        zfragment = ToDayFragment.newInstance(journal.getId(), sDate);
        zfragment.setOptionListener(this);
        zfragment.setZapisArray(zapisArray);

        hfragment = HistoryFragment.newInstance(journal.getId());//groupZapisArray
        hfragment.setData(groupZapisArray);
        //pagerAdapter.addFragment(hfragment, getString(R.string.history));
        pagerAdapter.replaceFragment(0,zfragment,getString(R.string.to_day));
        pagerAdapter.replaceFragment(1,hfragment, getString(R.string.history));
        viewPager.setAdapter(pagerAdapter);
    }


    @Override
    public void onClick(View v, int i, Zapis element) {

    }


}
