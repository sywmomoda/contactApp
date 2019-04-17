package cn.edu.wtu.sj.mycontact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import cn.edu.wtu.sj.mycontact.R;
import cn.edu.wtu.sj.mycontact.VO.Contact;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<Contact> mContactList;

    public MyAdapter(Context mContext, List<Contact> mContactList) {
        this.mContext = mContext;
        this.mContactList = mContactList;
    }

    @Override
    public int getCount() {
        return mContactList.size();
    }

    @Override
    public Contact getItem(int i) {
        return mContactList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view==null){
           view= LayoutInflater.from(mContext).inflate(R.layout.contactlist_checkbox,viewGroup,false);
           viewHolder=new ViewHolder();
           viewHolder.mImportNameTv=view.findViewById(R.id.importNameTv);
           viewHolder.mImportPhoneTv=view.findViewById(R.id.importPhoneTv);
           viewHolder.mImportEmailTv=view.findViewById(R.id.importEmailTv);
           viewHolder.mImportCb=view.findViewById(R.id.importCb);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.mImportNameTv.setText(mContactList.get(i).getName());
        viewHolder.mImportPhoneTv.setText(mContactList.get(i).getPhone());
        viewHolder.mImportEmailTv.setText(mContactList.get(i).getEmail());
        if (mContactList.get(i).getChecked()==true){
            viewHolder.mImportCb.setChecked(true);
        }else {
            viewHolder.mImportCb.setChecked(false);
        }
        viewHolder.mImportCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.mImportCb.isChecked()==true){
                    mContactList.get(i).setChecked(true);
                }
                else{
                    mContactList.get(i).setChecked(false);
                }
            }
        });
        return view;
    }
    static class ViewHolder{
        TextView mImportNameTv,mImportPhoneTv,mImportEmailTv;
        CheckBox mImportCb;
    }
}
