package com.example.proj_moneymanager.activities.Plan;

import android.app.DatePickerDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj_moneymanager.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        ((ViewGroup) itemView.getParent()).removeView(itemView);
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
