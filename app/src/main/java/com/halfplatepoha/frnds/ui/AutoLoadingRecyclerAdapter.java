package com.halfplatepoha.frnds.ui;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public abstract class AutoLoadingRecyclerAdapter<Elem, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private ArrayList<Elem> mElementList;

    public AutoLoadingRecyclerAdapter() {
        mElementList = new ArrayList<>();
    }

    public void addNewElement(Elem e) {
        mElementList.add(e);
    }

    public ArrayList<Elem> getItems() {
        return mElementList;
    }

    public Elem getItem(int position) {
        return mElementList.get(position);
    }

    @Override
    public int getItemCount() {
        return mElementList.size();
    }
}
