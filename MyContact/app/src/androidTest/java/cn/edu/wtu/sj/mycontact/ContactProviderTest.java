package cn.edu.wtu.sj.mycontact;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import cn.edu.wtu.sj.mycontact.dbhelper.ContactOpenHelper;
import cn.edu.wtu.sj.mycontact.provider.ContactProvider;

@RunWith(AndroidJUnit4.class)
public class ContactProviderTest {
    Context appContext= InstrumentationRegistry.getTargetContext();
    @Test
    public void insertTest(){
        ContentValues cv=new ContentValues();
        cv.put(ContactOpenHelper.ContactTable.NAME,"sy3");
        cv.put(ContactOpenHelper.ContactTable.PHONE,"342342342");
        cv.put(ContactOpenHelper.ContactTable.EMAIL,"SY3@WTU.EDU.CN");
        cv.put(ContactOpenHelper.ContactTable.QQ,"2323232332");
        appContext.getContentResolver().insert(ContactProvider.URI_CONTACT,cv);

    }

}
