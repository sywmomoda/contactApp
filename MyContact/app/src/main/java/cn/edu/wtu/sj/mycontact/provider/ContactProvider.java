package cn.edu.wtu.sj.mycontact.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.edu.wtu.sj.mycontact.dbhelper.ContactOpenHelper;

public class ContactProvider extends ContentProvider {
    public static  final  String AUTHORITIES=ContactProvider.class.getCanonicalName();
    private ContactOpenHelper mContactOpenHelper;
    static UriMatcher sUriMatcher;
    public static final Uri URI_CONTACT=Uri.parse("content://"+AUTHORITIES+"/contact");

    public static final int CONTACT = 1;

    static {
       sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
       sUriMatcher.addURI(AUTHORITIES,"/contact", CONTACT);
    }
    @Override
    public boolean onCreate() {
        mContactOpenHelper=new ContactOpenHelper(getContext());
        if (mContactOpenHelper!=null)
            return true;
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortby) {
        int match=sUriMatcher.match(uri);
        Cursor cursor=null;
        switch (match){
            case CONTACT:
                SQLiteDatabase db=mContactOpenHelper.getReadableDatabase();
                cursor= db.query(ContactOpenHelper.T_CONTACT,columns,selection,selectionArgs,"","", sortby);
                System.out.println("-------------------ContactProvider query success-----");
                break;
            default:break;

        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match=sUriMatcher.match(uri);
        switch (match){
            case CONTACT:
                SQLiteDatabase db=mContactOpenHelper.getWritableDatabase();
                long id = db.insert(ContactOpenHelper.T_CONTACT, "", contentValues);
                if (id!=-1){
                    System.out.println("-------------------ContactProvider insert success-----");
                    uri= ContentUris.withAppendedId(uri,id);
                   getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);
                }
                break;
                default:break;

        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int match=sUriMatcher.match(uri);
        int deletecount=0;
        switch (match){
            case CONTACT:
                SQLiteDatabase db=mContactOpenHelper.getWritableDatabase();
                deletecount = db.delete(ContactOpenHelper.T_CONTACT, s, strings);
                if (deletecount>0){
                    System.out.println("-------------------ContactProvider delete success-----");
                    getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);


                }
                break;
            default:break;

        }
        return deletecount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int match=sUriMatcher.match(uri);
        int updatecount=0;
        switch (match){
            case CONTACT:
                SQLiteDatabase db=mContactOpenHelper.getWritableDatabase();
                updatecount = db.update(ContactOpenHelper.T_CONTACT,contentValues, s, strings);
                if (updatecount>0){
                    System.out.println("-------------------ContactProvider update success-----");
                    getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);


                }
                break;
            default:break;

        }
        return updatecount;
    }
}
