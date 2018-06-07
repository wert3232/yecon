package com.autochips.bluetooth.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.autochips.bluetooth.PbSyncManager.PBRecord;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;
public class SystemContactsManager {
	static final String tag = "SystemContactsManager";
	public static void cleanContacts(Context context){
		if (!iscontactsexist()) {
			return;
		}
		long starttime = System.currentTimeMillis();
		deleteContacts(context);
	    long endtime = System.currentTimeMillis();
	    Log.e("SystemContactsManager", "deleteContacts(context);" + (endtime - starttime));
		/*
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(RawContacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
        	cursor.moveToLast();
        	while (true) {
                String rawContactId = cursor.getString(cursor.getColumnIndex(RawContacts._ID));
                boolean iscursornull = !cursor.moveToPrevious();
                deleteContact(context, Integer.valueOf(rawContactId));
                if (iscursornull) {
					break;
				}
			}
		}
        */
	}
	public static void queryContacts(Context context) {
		if (!iscontactsexist()) {
			return;
		}
		long starttime = System.currentTimeMillis();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {  
                String rawContactId = "";
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor rawContactCur = cr.query(RawContacts.CONTENT_URI, null,  
                        RawContacts._ID + "=?", new String[] { id }, null);  
                if (rawContactCur.moveToFirst()) {
                    rawContactId = rawContactCur.getString(rawContactCur.getColumnIndex(RawContacts._ID));
                }
                rawContactCur.close();
                if (Integer.parseInt(cursor.getString(cursor  
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  
                                    ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?",  
                                    new String[] { rawContactId }, null);
                    while (phoneCur.moveToNext()) {
                        String number = phoneCur.getString(phoneCur  
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String type = phoneCur.getString(phoneCur  
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    }  
                    phoneCur.close();  
  
                }  
            }  
            cursor.close();  
        }
	    long endtime = System.currentTimeMillis();
	    Log.e("SystemContactsManager", "queryContacts(context);" + (endtime - starttime));
    } 
	public static boolean addContacts(Context context, List<PBRecord> list){
		if (!iscontactsexist()) {
			return false;
		}
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();  
		ContentProviderOperation.Builder bd; 
        ContentResolver cr = context.getContentResolver();  
		int rawContactInsertIndex = 0;
		int nlen = list.size();
		for (int i = 0; i < nlen; i++) {
			if (rawContactInsertIndex > 497) {
				break;
			}
			bd = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)  
	                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
			ops.add(bd.build());
			bd = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.GIVEN_NAME, list.get(i).getName());
			ops.add(bd.build());
			bd = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
					.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, list.get(i).getNumber())
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE);
			ops.add(bd.build());
			rawContactInsertIndex = ops.size();
		}
		try {
			long starttime = System.currentTimeMillis();
		    cr.applyBatch(ContactsContract.AUTHORITY, ops); 
		    long endtime = System.currentTimeMillis();
		    Log.e("SystemContactsManager", "addContacts" + (endtime - starttime));
		  } catch (RemoteException e) {
		    e.printStackTrace(); 
		  } catch (OperationApplicationException e) {
		    e.printStackTrace(); 
		  } 
		int insertnum = rawContactInsertIndex/3;
		if (insertnum < nlen) {
			addContacts(context, list.subList(insertnum, nlen));
		}
		return true;
	}
	public static void addContact(Context context, String name, String phoneNum) {  
		if (!iscontactsexist()) {
			return;
		}
        ContentValues values = new ContentValues();  
        ContentResolver cr = context.getContentResolver();  
        Uri rawContactUri = cr.insert(RawContacts.CONTENT_URI, values);  
        long rawContactId = ContentUris.parseId(rawContactUri);  
        // 向data表插入数据  
        if (name != "") {  
            values.clear();  
            values.put(Data.RAW_CONTACT_ID, rawContactId);  
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);  
            values.put(StructuredName.GIVEN_NAME, name);  
            cr.insert(ContactsContract.Data.CONTENT_URI, values);  
        }  
        // 向data表插入电话号码  
        if (phoneNum != "") {  
            values.clear();  
            values.put(Data.RAW_CONTACT_ID, rawContactId);  
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);  
            values.put(Phone.NUMBER, phoneNum);  
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);  
            cr.insert(ContactsContract.Data.CONTENT_URI, values);  
        }  
    }  
	public static void deleteContact(Context context, long rawContactId) {
		if (iscontactsexist()) {
	        context.getContentResolver().delete(  
	                ContentUris.withAppendedId(RawContacts.CONTENT_URI,rawContactId), null, null);	
		}  
    }
	public static void deleteContacts(Context context){
		if (iscontactsexist()) {
			context.getContentResolver().delete(RawContacts.CONTENT_URI, null, null);	
		}
	}
	private static boolean iscontactsexist(){
		return new File("/data/data/com.android.providers.contacts/databases/contacts2.db").exists();
	}
}
