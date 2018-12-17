package com.khomutovandrey.exjournal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.khomutovandrey.exjournal.ItemOnClickListener;
import com.khomutovandrey.exjournal.OptionAct;
import com.khomutovandrey.exjournal.R;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter {
    ArrayList<ArrayList<Zapis>> mZapisArray; // Массив, содержащий массив объектов Zapis. Каждый вложенный массив содержит записи за день
    Context context;
    private OptionAct<Zapis> optionAct;// Обратный вызов, для обработки реакции по нажатию на пункт контекстного меню
    private ItemOnClickListener<Zapis> clickListener; // Обратный вызов, для обработки реакции по нажатию на элемент списка

    public static class ZViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView tvDate, tvCount, tvOption;
        ListView lvDay;

        public ZViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            tvCount = (TextView)itemView.findViewById(R.id.tvCount);
            tvOption = (TextView)itemView.findViewById(R.id.tvOption);
            lvDay = (ListView)itemView.findViewById(R.id.dynamic);
        }
    }

    public HistoryAdapter(Context context, ArrayList<ArrayList<Zapis>> zapisArray){
        this.context = context;
        this.mZapisArray = zapisArray;
        optionAct = (OptionAct<Zapis>)context;
        clickListener = (ItemOnClickListener<Zapis>)context;
    }

    @NonNull
    @Override
    public ZViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup,false);
        ZViewHolder viewHolder = new ZViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ZViewHolder vh = (ZViewHolder) viewHolder;
        ArrayList<Zapis> groupZapis = mZapisArray.get(i);
        long sum = 0;
        String sDate="";
        if(groupZapis.size()>0){// Получаем дату из первого элемента группы
            sDate = groupZapis.get(0).getDate();
        }
        // Подсчитываем сумму задень
        for (Zapis z:groupZapis) {
            sum = sum + z.getCount();
        }
        // Формируем строку для вывода даты, общего количества за день и количества подходов
        StringBuilder sbuild = new StringBuilder(sDate).append(" ")
                .append(context.getString(R.string.total))
                .append(": ")
                .append(String.valueOf(sum))
                .append("/")
                .append(groupZapis.size());
        vh.tvDate.setText(sbuild.toString());
        //vh.tvCount.setText(String.valueOf(sum));

        ToDayAdapter dayAdapter = new ToDayAdapter(context, groupZapis);
        vh.lvDay.setAdapter(dayAdapter);
    }

    @Override
    public int getItemCount() {
        if (mZapisArray!=null) {
            return mZapisArray.size();
        }else return 0;
    }
}
