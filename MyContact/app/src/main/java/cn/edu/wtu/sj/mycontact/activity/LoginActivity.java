package cn.edu.wtu.sj.mycontact.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import cn.edu.wtu.sj.mycontact.R;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText nameEdit;
    private EditText passwordEdit;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameEdit=(EditText) findViewById(R.id.account);
        passwordEdit=(EditText) findViewById(R.id.passwordEdit);
        login=(Button) findViewById(R.id.Login_button);

        pref= (SharedPreferences) getSharedPreferences("data",MODE_PRIVATE);

            String account=pref.getString("account","");
            String password=pref.getString("password","");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=nameEdit.getText().toString();
                String password=passwordEdit.getText().toString();

                if(account.equals(pref.getString("account","admin"))&&password.equals(pref.getString("password","123456"))){
                    editor=pref.edit();
                    editor.apply();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"密码或用户名输入错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
