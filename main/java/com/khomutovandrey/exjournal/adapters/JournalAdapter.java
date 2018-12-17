package com.khomutovandrey.exjournal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.khomutovandrey.exjournal.ItemOnClickListener;
import com.khomutovandrey.exjournal.OptionAct;
import com.khomutovandrey.exjournal.R; // Разобраться, R - без импорта
import com.khomutovandrey.exjournal.entry.Journal;

import java.util.List;

/**
 * В адаптере реализует интерфейс View.OnCreateContextMenuListener для поддержки вызова контекстного меню
 * элементов списка
 */
public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    private List<Journal> journals; // массив журналов для вывода
    private Context mContext;
    private OptionAct<Journal> optionAct;// Обратный вызов, для обработки реакции по нажатию на пункт контекстного меню
    private ItemOnClickListener<Journal> clickListener; // Обратный вызов, для обработки реакции по нажатию на элемент списка

    public static class JournalViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tvJName;
        TextView tvOption;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_item);
            tvJName = (TextView)itemView.findViewById(R.id.tvName);
            tvOption = (TextView)itemView.findViewById(R.id.tvOption);
        }

    }

    public JournalAdapter(Context context, List<Journal> journals) {
        mContext = context;
        this.journals = journals;
        optionAct = (OptionAct<Journal>) context;
        clickListener = (ItemOnClickListener<Journal>) context;
    }

    @NonNull
    @Override
    public JournalAdapter.JournalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_layout, viewGroup, false);
        JournalViewHolder viewHolder = new JournalViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final JournalAdapter.JournalViewHolder journalViewHolder, int i) {
        //Связываем данные с виджетами макета, и настраиваем отображение
        final int index = i;
        journalViewHolder.tvJName.setText(journals.get(i).getName());
        journalViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Journal journal = journals.get(index);
                clickListener.onClick(v, index, journal);
                //Toast.makeText(mContext, journal2.getName(),Toast.LENGTH_SHORT).show();
            }
        });

        journalViewHolder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, journalViewHolder.tvOption);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Journal journal;
                        switch (item.getItemId()){
                            case R.id.addItem:
                                journal = journals.get(index);
                                //Toast.makeText(mContext, journal.getName(),Toast.LENGTH_SHORT).show();
                                optionAct.actionAdd(journal);
                                break;
                            case R.id.renameItem:
                                journal = journals.get(index);
                                //Toast.makeText(mContext, journal.getName(),Toast.LENGTH_SHORT).show();
                                optionAct.actionRename(journal);
                                break;
                            case R.id.deleteItem:
                                journal = journals.get(index);
                                //Toast.makeText(mContext, journal.getName(),Toast.LENGTH_SHORT).show();
                                optionAct.actionDelete(journal);
                                break;
                                default:break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
