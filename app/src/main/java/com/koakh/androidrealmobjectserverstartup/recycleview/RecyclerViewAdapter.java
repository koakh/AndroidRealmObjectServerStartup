package com.koakh.androidrealmobjectserverstartup.recycleview;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koakh.androidrealmobjectserverstartup.R;
import com.koakh.androidrealmobjectserverstartup.model.TimeStamp;
import com.koakh.androidrealmobjectserverstartup.ui.fragments.RecycleViewFragment;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by mario on 17/01/2017.
 */

public class RecyclerViewAdapter extends RealmRecyclerViewAdapter<TimeStamp, RecyclerViewAdapter.InnerViewHolder> {

  private final RecycleViewFragment mFragment;

  public RecyclerViewAdapter(RecycleViewFragment fragment, OrderedRealmCollection<TimeStamp> data) {
    super(fragment.getContext(), data, true);

    mFragment = fragment;
  }

  @Override
  public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.row, parent, false);
    return new InnerViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(InnerViewHolder holder, int position) {
    TimeStamp obj = getData().get(position);
    holder.data = obj;
    holder.title.setText(obj.getTimeStamp());
  }

  class InnerViewHolder extends RecyclerView.ViewHolder
      implements View.OnLongClickListener, View.OnClickListener{

    public TextView title;
    public TimeStamp data;

    public InnerViewHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.textview);
      view.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
      mFragment.deleteItem(data);
      return true;
    }

    @Override
    public void onClick(View v) {
      Snackbar.make(v, "onClick", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
    }
  }
}