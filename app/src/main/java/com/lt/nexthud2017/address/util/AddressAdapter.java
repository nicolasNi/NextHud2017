package com.lt.nexthud2017.address.util;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lt.nexthud2017.R;
import com.lt.nexthud2017.address.Address;
import com.lt.nexthud2017.databinding.AddressitemBinding;
import com.lt.nexthud2017.music.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AddressAdapter extends BaseAdapter
{
    private Context context;
    private List<Address> listItem;

    public AddressAdapter(Context context, List<Address> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem == null ? 0 : listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddressitemBinding binding = null;
        if(convertView != null){
            binding = DataBindingUtil.getBinding(convertView);
        }else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.addressitem,parent,false);
        }
        binding.setAddress((Address)getItem(position));
        if (position == selectItem) {
            binding.getRoot().setBackgroundColor(Color.CYAN);
        } else {
            binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
        }
        return binding.getRoot();
    }

    public int selectItem = 0;

}