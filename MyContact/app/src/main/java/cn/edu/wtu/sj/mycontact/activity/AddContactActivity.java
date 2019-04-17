package cn.edu.wtu.sj.mycontact.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.edu.wtu.sj.mycontact.R;
import cn.edu.wtu.sj.mycontact.dbhelper.ContactOpenHelper;
import cn.edu.wtu.sj.mycontact.provider.ContactProvider;
import cn.edu.wtu.sj.mycontact.utils.ThreadUtils;
import cn.edu.wtu.sj.mycontact.utils.ToastUtils;

public class AddContactActivity extends AppCompatActivity {
  private EditText maddName,maddPhone,maddEmail,maddQq;
  private Button maddBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        maddName=findViewById(R.id.addNameEt);
        maddPhone=findViewById(R.id.addPhoneEt);
        maddEmail=findViewById(R.id.addEmailEt);
        maddQq=findViewById(R.id.addQqEt);
        maddBtn=findViewById(R.id.addContactBtn);
        maddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        addContact();
                        finish();
                    }
                });
            }
        });
    }

    private void addContact() {
        ContentValues cv=new ContentValues();
        cv.put(ContactOpenHelper.ContactTable.NAME,maddName.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.PHONE,maddPhone.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.EMAIL,maddEmail.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.QQ,maddQq.getText().toString());
        Cursor cursor=getContentResolver().query(ContactProvider.URI_CONTACT,
                null,
                "name=?",
                new String[]{maddName.getText().toString()},
                null);
        if (cursor.getCount()>0){
            ToastUtils.showToastSafe(this,"该联系人已存在！");
        }else{
           getContentResolver().insert(ContactProvider.URI_CONTACT,cv);
           ToastUtils.showToastSafe(this,"联系人已添加！");
        }
    }
}
