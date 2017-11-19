package br.ufop.icea.encontrodesaberes.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.model.Trabalho;

/**
 * Adaptador responsável por inflar um item 'Trabalho' na lista de Trabalhos da 'SelectActivity'
 */

public class TrabalhoAdapter extends BaseAdapter {
    private Context mContext;
    List<Trabalho> trabalhos;

    public TrabalhoAdapter(Context context, List<Trabalho> trabalhos){
        mContext = context;
        this.trabalhos = new ArrayList<>(trabalhos);
    }
    @Override
    public int getCount() {
        return trabalhos.size();
    }

    @Override
    public Object getItem(int position) {
        return trabalhos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return trabalhos.get(position).getIdtrabalho();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_trabalho, null);
        }
        TextView id = convertView.findViewById(R.id.textId),
                author = convertView.findViewById(R.id.textAutor),
                title = convertView.findViewById(R.id.textTitle);
        ImageView img = convertView.findViewById(R.id.imageVoted);
        Trabalho t = trabalhos.get(position);
        Log.d("ID", ""+t.getIdtrabalho());
        Log.d("intVotado", ""+t.getVotado());
        id.setText(Integer.toString(t.getIdtrabalho()));
        author.setText(t.getAutorprincipal());
        title.setText(t.getTitulo());
        img.setVisibility(View.INVISIBLE);
        if(t.isVoted())
            img.setVisibility(View.VISIBLE);
        return convertView;
    }
}
