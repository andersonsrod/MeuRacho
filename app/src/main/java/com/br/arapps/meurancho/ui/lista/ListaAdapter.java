package com.br.arapps.meurancho.ui.lista;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.global.AppGlobal;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder>{
    private ListaAdapterListener listener;
    private Context context;
    private List<ContentValues> data;

    public interface ListaAdapterListener{
        void onClickItem(String nomeLista);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvItem;
        private FrameLayout redLine;
        private AppCompatCheckBox cbComprado;
        private TextView tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvItem = itemView.findViewById(R.id.tvItem);
            this.redLine = itemView.findViewById(R.id.redLine);
            this.cbComprado = itemView.findViewById(R.id.cbComprado);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }

    public ListaAdapter(ListaAdapterListener listener, Context context, List<ContentValues> data) {
        this.listener = listener;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ContentValues c = data.get(position);

        holder.tvItem.setText(c.getAsString("nomeLista"));

        if (c.getAsBoolean("selecionado"))
            holder.itemView.setBackgroundResource(R.drawable.background_schedule_selected);
        else
            holder.itemView.setBackgroundResource(R.drawable.action_schedule);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                c.put("selecionado", !c.getAsBoolean("selecionado"));
                notifyDataSetChanged();
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(c.getAsString("nomeLista"));
            }
        });

        holder.redLine.setVisibility(View.GONE);
        holder.cbComprado.setVisibility(View.GONE);

        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                try{
                    int total = AppGlobal.getDatabase().listaDao().totalItensFaltantes(c.getAsString("nomeLista"));
                    return Integer.valueOf(total);
                }catch (SQLiteException e){
                    return 0;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);

                holder.tvTotal.setText(String.format("%d itens não comprados", integer.intValue()));

                if (integer.intValue() > 0)
                    holder.tvTotal.setTextColor(context.getResources().getColor(R.color.red));
                else
                    holder.tvTotal.setTextColor(context.getResources().getColor(R.color.backgroundLine));
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
