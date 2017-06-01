package com.lt.nexthud2017.address.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lt.nexthud2017.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AddressAdapter extends BaseAdapter
{
    private LayoutInflater mInflater = null;
    public ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,  Object>>();
    public AddressAdapter(Context context)
    {
        //根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //How many items are in the data set represented by this Adapter.
        //在此适配器中所代表的数据集中的条目数
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        // Get the data item associated with the specified position in the data set.
        //获取数据集中与指定索引对应的数据项
        return position;
    }

    @Override
    public long getItemId(int position) {
        //Get the row id associated with the specified position in the list.
        //获取在列表中与指定索引对应的行id
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    //获取一个在数据集中指定索引的视图来显示数据
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //如果缓存convertView为空，则需要创建View
        TextView address;
        //	TextView tv_search_list_airtist;
        //根据自定义的Item布局加载布局
        convertView = mInflater.inflate(R.layout.addressitem, null);
        address = (TextView)convertView.findViewById(R.id.addressname);
        //    tv_search_list_airtist = (TextView)convertView.findViewById(R.id.tv_search_list_airtist);
        address.setText((String)listItem.get(position).get("address"));

        convertView.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
    }


    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int  selectItem=0;

}