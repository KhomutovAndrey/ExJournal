package com.khomutovandrey.exjournal.tabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.khomutovandrey.exjournal.DialogAccept;
import com.khomutovandrey.exjournal.DialogTraining;
import com.khomutovandrey.exjournal.ItemOnClickListener;
import com.khomutovandrey.exjournal.OptionAct;
import com.khomutovandrey.exjournal.R;
import com.khomutovandrey.exjournal.adapters.ToDayAdapter;
import com.khomutovandrey.exjournal.database.Controller;
import com.khomutovandrey.exjournal.database.SQLiteController;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

/**
 *
 */
public class ToDayFragment extends Fragment {
    public static final String ARG_DATE = "date";
    public final static String JOURNAL_Id="id";

    private long journalId=-1;
    private String sDate="";
    private int summ=0;
    Controller controller;
    private ArrayList<Zapis> zapisArray;
    private View view;
    ListView listView;
    TextView tvDate;
    TextView tvSumm;
    ToDayAdapter dayAdapter;
    OptionAct<Zapis> mOptionListener;
    private static String TAG="Testfragment";


    public ToDayFragment() {
        // Required empty public constructor
        Log.d(TAG,"Frag-constructor:");
    }

    /**
     *
     * @param journalId
     * @param sDate
     * @return
     */
    public static ToDayFragment newInstance(long journalId, String sDate) {
        Log.d(TAG,"Frag-newInstance:");
        ToDayFragment fragment = new ToDayFragment();
        fragment.journalId = journalId;
        fragment.sDate = sDate;
        return fragment;
    }

    public void setZapisArray(ArrayList<Zapis> array){
        zapisArray = array;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Frag-onCreate:");
        Bundle arg = getArguments();
        if (arg != null) {
            journalId = arg.getLong(JOURNAL_Id, -1);
            sDate = arg.getString(ARG_DATE, "");
        }
        controller = new SQLiteController(getActivity());
        zapisArray = controller.getZapisByJornal(journalId, sDate);
        dayAdapter = new ToDayAdapter(getContext(), zapisArray);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(JOURNAL_Id, journalId);
        outState.putString(ARG_DATE, sDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"Frag-onCreateView:");
        if (savedInstanceState != null) {
            journalId = savedInstanceState.getLong(JOURNAL_Id, -1);
            sDate = savedInstanceState.getString(ARG_DATE, "");
            //zapisArray = controller.getZapisByJornal(journalId, sDate);
        }

        // Раздуваем вид
        view = inflater.inflate(R.layout.fragment_to_day, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        tvDate = (TextView)view.findViewById(R.id.tvDate);
        tvSumm = (TextView)view.findViewById(R.id.tvSumm);
        reFreshView(zapisArray);// Заполняем данными
        registerForContextMenu(listView);
        listView.setAdapter(dayAdapter);
        return view;
    }

    /**
     * Получаем актуальные данные и обновляем компоненты
     */
    public void reFreshView(ArrayList<Zapis> zArray){
        Log.d(TAG,"Frag-reFreshView:");

        if (zapisArray==null){
            zapisArray = zArray;
        } else {
            zapisArray.clear();
            zapisArray.addAll(controller.getZapisByJornal(journalId, sDate));
        }

        summ = 0;
        for (Zapis zapis:zapisArray) {
            summ = summ + zapis.getCount();
        }
        tvDate.setText(sDate);
        tvSumm.setText(getString(R.string.total)+":"+String.valueOf(summ));
        dayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = menuInfo.position;
        if(mOptionListener!=null){
            switch (item.getItemId()){
                case R.id.addItem:
                    mOptionListener.actionAdd((Zapis)dayAdapter.getItem(position));
                    return true;
                case R.id.renameItem:
                    mOptionListener.actionRename((Zapis)dayAdapter.getItem(position));
                    return true;
                case R.id.deleteItem:
                    mOptionListener.actionDelete((Zapis)dayAdapter.getItem(position));
                    return true;
                default: return super.onContextItemSelected(item);
            }
        } else return super.onContextItemSelected(item);
    }

    public void setOptionListener (OptionAct<Zapis> optionListener){
        mOptionListener = optionListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"Frag-onDetach:");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"Frag-onAttach:");
        mOptionListener = (OptionAct<Zapis>) activity;
    }

}
