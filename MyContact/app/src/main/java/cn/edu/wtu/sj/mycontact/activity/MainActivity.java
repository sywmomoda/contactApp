package cn.edu.wtu.sj.mycontact.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.edu.wtu.sj.mycontact.R;
import cn.edu.wtu.sj.mycontact.provider.ContactProvider;
import cn.edu.wtu.sj.mycontact.utils.ThreadUtils;
import cn.edu.wtu.sj.mycontact.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {
    private CursorAdapter mCursorAdapter;
    private ListView mMainLv;
    private MyContentObserver mMyContentObserver=new MyContentObserver(new Handler());
    private Cursor mcurrentCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainLv=findViewById(R.id.mainLv);
        initData();
        initListener();

    }

    private void initListener() {
        registerContentObserver();
        registerForContextMenu(mMainLv);
        mMainLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mcurrentCursor= (Cursor) adapterView.getItemAtPosition(i);
                return false;// false 表示这个事件继续向外传播，这样才能让ContextMenu显示出来
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegistContentObserver();
    }

    private void initData() {
        setOrUpdateAdapter();
    }

    private void setOrUpdateAdapter() {
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if(mCursorAdapter!=null){
                    mCursorAdapter.getCursor().requery();
                    return;
                }
                Cursor cursor=getContentResolver().query(ContactProvider.URI_CONTACT,null,null,null,null);
                if (cursor.getCount()==0){
                    return;
                }
                mCursorAdapter=new CursorAdapter(MainActivity.this,cursor,true) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                        View view=View.inflate(context, R.layout.data_list,null);
                        return view;
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        TextView nameTv=view.findViewById(R.id.nameTv_datalist);
                        TextView phoneTv=view.findViewById(R.id.phoneTv_datalist);
                        TextView emailTv=view.findViewById(R.id.emailTv_datalist);
                        TextView qqTv=view.findViewById(R.id.qqTv_datalist);
                        nameTv.setText(cursor.getString(1));
                        phoneTv.setText(cursor.getString(2));
                        emailTv.setText(cursor.getString(3));
                        qqTv.setText(cursor.getString(4));

                    }
                };
                mMainLv.setAdapter(mCursorAdapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,Menu.FIRST,1,"新建联系人").setIcon(R.drawable.new_contact);
        menu.add(1,Menu.FIRST+1,2,"查找联系人").setIcon(R.drawable.search_contact);
        menu.add(1,Menu.FIRST+2,3,"导入联系人").setIcon(R.drawable.import_contact);
        menu.add(1,Menu.FIRST+3,4,"导出联系人").setIcon(R.drawable.export_contact);
        menu.add(1,Menu.FIRST+4,4,"修改密码").setIcon(R.drawable.modify_password);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case Menu.FIRST:addContact();break;
            case Menu.FIRST+1:searchContact();break;
            case Menu.FIRST+2:importContact();break;
            case Menu.FIRST + 4: repassWord();break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void repassWord() {
        final EditText rePassWord = new EditText(MainActivity.this);
        //组件之一提示对话框AlertDialog.Builder
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("请输入新密码！")
                .setView(rePassWord)
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        String password=rePassWord.getText().toString().trim();
                        editor.putString("password",password);
                        editor.commit();
                        Toast.makeText(MainActivity.this,"succeed",Toast.LENGTH_SHORT).show();

                    }
                }).show();
    }

    private void importContact() {
        Intent intent=new Intent(MainActivity.this,ImportContactActivity.class);
        startActivity(intent);
    }

    private void searchContact() {
         final EditText inputNameTv=new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog=new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("请输入要查找的姓名")
                .setView(inputNameTv)
                .setPositiveButton("查找", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        searchResultShow(inputNameTv.getText().toString().trim());
                    }
                }).show();
    }

    private void searchResultShow(final String name) {
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor=getContentResolver().query(ContactProvider.URI_CONTACT,
                        null,
                        "name like ?",
                        new String[]{"%"+name+"%"},
                        null);
                if (cursor.getCount()==0){
                    ToastUtils.showToastSafe(MainActivity.this,"没有找到该联系人！");
                    return;
                }
                CursorAdapter searchAdapter=new CursorAdapter(MainActivity.this,cursor,true) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                        View view=View.inflate(context, R.layout.data_list,null);
                        return view;
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        TextView nameTv=view.findViewById(R.id.nameTv_datalist);
                        TextView phoneTv=view.findViewById(R.id.phoneTv_datalist);
                        TextView emailTv=view.findViewById(R.id.emailTv_datalist);
                        TextView qqTv=view.findViewById(R.id.qqTv_datalist);
                        nameTv.setText(cursor.getString(1));
                        phoneTv.setText(cursor.getString(2));
                        emailTv.setText(cursor.getString(3));
                        qqTv.setText(cursor.getString(4));

                    }
                };
                mMainLv.setAdapter(searchAdapter);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("联系人操作");
        menu.add(Menu.NONE,Menu.FIRST,1,"编辑联系人");
        menu.add(Menu.NONE,Menu.FIRST+1,2,"删除联系人");
        menu.add(Menu.NONE,Menu.FIRST+2,3,"拨打电话");
        menu.add(Menu.NONE,Menu.FIRST+3,4,"发送短信");
        menu.add(Menu.NONE,Menu.FIRST+4,5,"发送邮件");
        menu.add(Menu.NONE,Menu.FIRST+5,6,"显示全部");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case Menu.FIRST:editContact();break;
            case Menu.FIRST+1:delContact();break;
            case Menu.FIRST+2:dial();break;
            case Menu.FIRST+3:sms();break;
            case Menu.FIRST+4:email();break;
            case Menu.FIRST+5:mMainLv.setAdapter(mCursorAdapter);setOrUpdateAdapter();break;
        }
        return super.onContextItemSelected(item);
    }

    private void editContact() {
         Intent intent=new Intent(MainActivity.this,EditContactActivity.class);
         intent.putExtra("name",mcurrentCursor.getString(1));
         intent.putExtra("phone",mcurrentCursor.getString(2));
         intent.putExtra("email",mcurrentCursor.getString(3));
         intent.putExtra("qq",mcurrentCursor.getString(4));
         startActivity(intent);
    }

    private void delContact() {
        getContentResolver().delete(ContactProvider.URI_CONTACT,
                "name=?",
                new String[]{mcurrentCursor.getString(1)});
        ToastUtils.showToastSafe(this,"该联系人已删除！");
    }

    private void dial() {
        Intent intent = new Intent(Intent.ACTION_CALL);
       intent = intent.setData(Uri.parse("tel:" + mcurrentCursor.getString(2)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void sms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+mcurrentCursor.getString(2)));
        startActivity(intent);
    }

    private void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("emailto:"+mcurrentCursor.getString(3)));
        startActivity(intent);
    }

    private void addContact() {
        Intent intent=new Intent(MainActivity.this,AddContactActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu!=null){
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")){
                try {
                    Method method=menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu,true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
             setOrUpdateAdapter();
        }
    }
    private void registerContentObserver(){
        getContentResolver().registerContentObserver(ContactProvider.URI_CONTACT,
                true,
                mMyContentObserver);
    }
    private void unRegistContentObserver(){
        getContentResolver().unregisterContentObserver(mMyContentObserver);
    }
}
