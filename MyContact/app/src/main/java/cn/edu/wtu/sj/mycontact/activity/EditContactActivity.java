package cn.edu.wtu.sj.mycontact.activity;

import android.content.ContentValues;
import android.content.Intent;
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

public class EditContactActivity extends AppCompatActivity {
  private EditText mname,mphone,memail,mqq;
  private Button mupdateBtn;
  private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);
        mname=findViewById(R.id.updateNameEt);
        mphone=findViewById(R.id.updatePhoneEt);
        memail=findViewById(R.id.updateEmailEt);
        mqq=findViewById(R.id.updateQqEt);
        mupdateBtn=findViewById(R.id.updateBtn);
        intent=getIntent();
        mname.setText(intent.getStringExtra("name"));
        mphone.setText(intent.getStringExtra("phone"));
        memail.setText(intent.getStringExtra("email"));
        mqq.setText(intent.getStringExtra("qq"));
        mupdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        updateContact();
                        finish();
                    }
                });
            }
        });
    }

    private void updateContact() {
        ContentValues cv=new ContentValues();
        cv.put(ContactOpenHelper.ContactTable.NAME,mname.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.PHONE,mphone.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.EMAIL,memail.getText().toString());
        cv.put(ContactOpenHelper.ContactTable.QQ,mqq.getText().toString());
        getContentResolver().update(ContactProvider.URI_CONTACT,
                cv,
                "name=?",
                new String[]{intent.getStringExtra("name")});
        ToastUtils.showToastSafe(this,"联系人修改成功！");
    }
}
