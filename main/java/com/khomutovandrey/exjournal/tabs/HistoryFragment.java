package com.khomutovandrey.exjournal.tabs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khomutovandrey.exjournal.R;
import com.khomutovandrey.exjournal.adapters.HistoryAdapter;
import com.khomutovandrey.exjournal.database.Controller;
import com.khomutovandrey.exjournal.database.SQLiteController;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private View view;
    private RecyclerView rv;
    Controller controller;
    ArrayList<ArrayList<Zapis>> zapisArray;
    long journalId = -1;
    HistoryAdapter hAdapter;
    public final static String JOURNAL_Id="id";


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(long journalId){
        HistoryFragment fragment = new HistoryFragment();
        //fragment.zapisArray = zapisArray;
        fragment.journalId = journalId;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            journalId = savedInstanceState.getLong(JOURNAL_Id);
        }
        controller = new SQLiteController(getActivity());
        zapisArray = controller.getGroupZapis(journalId);
        hAdapter = new HistoryAdapter(getContext(),zapisArray);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(JOURNAL_Id, journalId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            journalId = savedInstanceState.getLong(JOURNAL_Id, -1);
        }

        view = inflater.inflate(R.layout.fragment_history, container, false);
        // Настраиваем RecyclerView, отображающий список с историей занятий
        rv = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);
        hAdapter = new HistoryAdapter(getActivity(), zapisArray);
        rv.setAdapter(hAdapter);
        return view;
    }

    public void setData(ArrayList<ArrayList<Zapis>> zapisArray){
        if (this.zapisArray!=null) {
            this.zapisArray.clear();
            this.zapisArray.addAll(zapisArray);
        }else this.zapisArray = zapisArray;
        //hAdapter.notifyDataSetChanged();
    }

}
