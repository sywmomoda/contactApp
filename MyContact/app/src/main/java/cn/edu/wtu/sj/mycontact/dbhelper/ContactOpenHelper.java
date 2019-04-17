package cn.edu.wtu.sj.mycontact.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ContactOpenHelper extends SQLiteOpenHelper {
    public static  final String T_CONTACT="t_contact";
    public class  ContactTable implements BaseColumns{ //_id
        public static final String NAME="name";
        public static final String PHONE="phone";
        public static final String EMAIL="email";
        public static final String QQ="qq";
    }
    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     String sql="Create table "+T_CONTACT+" ("
             +ContactTable._ID+" integer PRIMARY KEY AUTOINCREMENT,"
             +ContactTable.NAME+" text,"
             +ContactTable.PHONE+" text,"
             +ContactTable.EMAIL+" text,"
             +ContactTable.QQ+" text)";
     db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
