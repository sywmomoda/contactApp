package cn.edu.wtu.sj.mycontact.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.wtu.sj.mycontact.R;
import cn.edu.wtu.sj.mycontact.VO.Contact;
import cn.edu.wtu.sj.mycontact.adapter.MyAdapter;
import cn.edu.wtu.sj.mycontact.dbhelper.ContactOpenHelper;
import cn.edu.wtu.sj.mycontact.provider.ContactProvider;
import cn.edu.wtu.sj.mycontact.utils.ThreadUtils;
import cn.edu.wtu.sj.mycontact.utils.ToastUtils;

public class ImportContactActivity extends AppCompatActivity {
     private ListView mImportLv;
     private List<Contact> mContactList=new ArrayList<>();
     private MyAdapter mMyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contact);
        mImportLv=findViewById(R.id.importLv);
        initData();
        initView();
        registerForContextMenu(mImportLv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("导入联系人操作");
        menu.add(0, Menu.FIRST,0,"全选");
        menu.add(0, Menu.FIRST+1,1,"全不选");
        menu.add(0, Menu.FIRST+2,1,"导入");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case Menu.FIRST:selectAll();break;
            case Menu.FIRST+1:unSelectAll();break;
            case Menu.FIRST+2:importContact();break;
        }
        return super.onContextItemSelected(item);
    }

    private void selectAll() {
        for (Contact contact:mContactList) {
            contact.setChecked(true);
        }
        initView();
    }

    private void unSelectAll() {
        for (Contact contact:mContactList) {
            contact.setChecked(false);
        }
        initView();
    }

    private void importContact() {
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                int count=0;
                for (Contact contact:mContactList) {
                    if (contact.getChecked()==true){
                       Cursor cursor=getContentResolver().query(ContactProvider.URI_CONTACT,
                               null,
                               "name=?",
                               new String[]{contact.getName()},
                               null);
                       if (!cursor.moveToNext()){
                           ContentValues cv=new ContentValues();
                           cv.put(ContactOpenHelper.ContactTable.NAME,contact.getName());
                           cv.put(ContactOpenHelper.ContactTable.PHONE,contact.getPhone());
                           cv.put(ContactOpenHelper.ContactTable.EMAIL,contact.getEmail());
                           cv.put(ContactOpenHelper.ContactTable.QQ,"");
                           getContentResolver().insert(ContactProvider.URI_CONTACT,cv);
                           count++;
                       }else ToastUtils.showToastSafe(ImportContactActivity.this,"该联系人已存在！");
                       cursor.close();
                    }
                }
                ToastUtils.showToastSafe(ImportContactActivity.this,"已导入"+count+"个联系人！");
                finish();
            }
        });
    }


    private void initData() {
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //查找联系人的数据
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                // 遍历查询结果，获取系统中所有联系人
                while (cursor.moveToNext()) {
                    //获取联系人ID
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //获取联系人名字
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //使用ContentResolver查找联系人的电话号码
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null);
                    //只获取第一个电话号码
                    String phoneNumber = "";
                    if (phones.moveToFirst())
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //关闭phone记录集
                    phones.close();
                    //使用ContentResolver查找联系人的email
                    Cursor emails = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                            null, null);
                    //只获取第一个email
                    String emailAddress = "";
                    if (emails.moveToFirst())
                        emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    //关闭emails记录集
                    emails.close();
                    //创建一个联系人对象
                    Contact contact = new Contact(name, phoneNumber, emailAddress, false);
                    //将contact加入到contacts集合中
                    mContactList.add(contact);
                }
                cursor.close();
            }
        });
    }


    private void initView() {
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (mMyAdapter!=null){
                    mMyAdapter=null;
                }
                mMyAdapter=new MyAdapter(ImportContactActivity.this,mContactList);
                mImportLv.setAdapter(mMyAdapter);
            }
        });
    }
}
