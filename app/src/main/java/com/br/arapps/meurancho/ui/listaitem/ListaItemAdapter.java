package com.br.arapps.meurancho.ui.listaitem;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.global.AppGlobal;

import java.util.List;

public class ListaItemAdapter extends RecyclerView.Adapter<ListaItemAdapter.ViewHolder>{
    private ListaItemAdapterListener listener;
    private Context context;
    private List<ContentValues> data;

    public interface ListaItemAdapterListener{
        void onClickItem(String nomeItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvItem;
        private FrameLayout redLine;
        private SwitchCompat swComprado;
        private ImageView ivNext;
        private TextView tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvItem = itemView.findViewById(R.id.tvItem);
            this.redLine = itemView.findViewById(R.id.redLine);
            this.swComprado = itemView.findViewById(R.id.swComprado);
            this.ivNext = itemView.findViewById(R.id.ivNext);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }

    public ListaItemAdapter(ListaItemAdapterListener listener, Context context, List<ContentValues> data) {
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

        holder.tvItem.setText(String.format("%s - %.1f %s", c.getAsString("nomeItem"), c.getAsDouble("qtd").doubleValue(), c.getAsString("unidade")));

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
                listener.onClickItem(c.getAsString("nomeItem"));
            }
        });

        if (c.getAsString("comprado") != null && c.getAsString("comprado").equals("S")) {
            holder.redLine.setVisibility(View.VISIBLE);
            holder.swComprado.setChecked(true);
        }else {
            holder.redLine.setVisibility(View.GONE);
            holder.swComprado.setChecked(false);
        }

        holder.swComprado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comprado;
                if (holder.swComprado.isChecked())
                    comprado = "S";
                else
                    comprado = "N";

                new AsyncTask<Void ,String, Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try{
                            AppGlobal.getDatabase().listaItemDao().setComprado(comprado, c.getAsString("nomeLista"), c.getAsString("nomeItem"));
                            return true;
                        }catch (SQLiteException e){
                            publishProgress(e.getMessage());
                            return false;
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        super.onProgressUpdate(values);
                        AppGlobal.messageInfo(context, values[0]);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);

                        if (aBoolean){
                            c.put("comprado", comprado);
                            notifyDataSetChanged();
                        }
                    }
                }.execute();
            }
        });

        holder.ivNext.setVisibility(View.GONE);
        holder.tvTotal.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
