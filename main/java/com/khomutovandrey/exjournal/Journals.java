package com.khomutovandrey.exjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.khomutovandrey.exjournal.adapters.JournalAdapter;
import com.khomutovandrey.exjournal.database.SQLiteController;
import com.khomutovandrey.exjournal.database.SQLiteDB;
import com.khomutovandrey.exjournal.entry.Journal;

import java.util.ArrayList;

public class Journals extends AppCompatActivity implements DialogJournalAdd.NoticeDialogListener, DialogAccept.NoticeDialogListener, OptionAct<Journal>, ItemOnClickListener<Journal>{
    SQLiteController controller;
    RecyclerView journal_recView; // RecyclerView содержащий журналы
    ArrayList<Journal> journals; // Масив журналов
    JournalAdapter adapterJournal; // Адаптер для списка журналов
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionAdd(null);
            }
        });

        //Настраиваем список RecyclerView, содержащий список журналов
        journal_recView = (RecyclerView)findViewById(R.id.journal_recView);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        journal_recView.setLayoutManager(lm);

        controller = new SQLiteController(this);

        //AdMobe
        MobileAds.initialize(this, getString(R.string.admob_appid));
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, this.getTitle().toString());
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        journals = controller.getAllJournals();
        adapterJournal = new JournalAdapter(this,journals);
        journal_recView.setAdapter(adapterJournal);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_journals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Обработчик нажатия кнопки в диалоговом окне добавления журнала
     * @param journalName название нового журнала
     */
    @Override
    public void onDialogPositiveClick(long id, String journalName) {
        if(id==-1) {// -1 значит операция добавления нового журнала
            controller.addJournal(new Journal(0, journalName));
        } else controller.renameJournal(id, journalName);

        journals.clear();
        journals.addAll(controller.getAllJournals());
        adapterJournal.notifyDataSetChanged();
    }

    @Override
    public void onDialogPositiveClick(long id) {
        controller.deleteJournal(id);
        journals.clear();
        journals.addAll(controller.getAllJournals());
        adapterJournal.notifyDataSetChanged();
    }

    @Override
    public void actionAdd(Journal journal) {
        DialogFragment dialog = new DialogJournalAdd();
        dialog.show(getSupportFragmentManager(),"addJournal");
    }

    @Override
    public void actionRename(Journal journal) {
        DialogJournalAdd dialog = new DialogJournalAdd();
        dialog.setJournal(journal);
        dialog.show(getSupportFragmentManager(),"addJournal");
    }

    @Override
    public void actionDelete(Journal journal) {
        DialogAccept dialog = new DialogAccept();
        Bundle arg = new Bundle();
        arg.putString(dialog.nameKey, journal.getName()); // Для передачи названия в диалог
        arg.putLong(dialog.idKey, journal.getId());
        dialog.setArguments(arg);
        //dialog.onAttach(this);
        dialog.show(getSupportFragmentManager(),"aceptDelete");
    }

    /**
     * Реализация реакции на нажатие итема в списке журналов journal_recView
     * @param v - view на котором произошло событие
     * @param i - позиция в списке адаптера
     * @param element - элемент списка в адаптере, описывающий сущность (Журнал, запись)
     */
    @Override
    public void onClick(View v, int i, Journal element) {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra(TrainingActivity.JournalId, element.getId());
        startActivity(intent);
        //Toast.makeText(this, element.getName(),Toast.LENGTH_SHORT).show();
    }
}
