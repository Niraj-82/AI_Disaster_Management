package com.example.resqai.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.resqai.model.EmergencyContact
import com.example.resqai.model.Incident

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2 // Incremented version
        private const val DATABASE_NAME = "ResQAI_Database.db"

        // Incident Table
        private const val TABLE_INCIDENTS = "incidents"
        private const val KEY_ID = "id"
        private const val KEY_TYPE = "type"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE_URL = "image_url"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LOCATION_STRING = "location_string"
        private const val KEY_REPORTER_NAME = "reporter_name"

        // Emergency Contacts Table
        private const val TABLE_EMERGENCY_CONTACTS = "emergency_contacts"
        private const val KEY_CONTACT_ID = "id"
        private const val KEY_CONTACT_NAME = "name"
        private const val KEY_CONTACT_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createIncidentsTable = ("CREATE TABLE " + TABLE_INCIDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TYPE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_URL + " TEXT,"
                + KEY_TIMESTAMP + " INTEGER,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL,"
                + KEY_LOCATION_STRING + " TEXT,"
                + KEY_REPORTER_NAME + " TEXT" + ")")
        db.execSQL(createIncidentsTable)

        val createContactsTable = ("CREATE TABLE " + TABLE_EMERGENCY_CONTACTS + "("
                + KEY_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CONTACT_NAME + " TEXT,"
                + KEY_CONTACT_PHONE + " TEXT" + ")")
        db.execSQL(createContactsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createContactsTable = ("CREATE TABLE " + TABLE_EMERGENCY_CONTACTS + "("
                    + KEY_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_CONTACT_NAME + " TEXT,"
                    + KEY_CONTACT_PHONE + " TEXT" + ")")
            db.execSQL(createContactsTable)
        }
    }

    // ... Incident methods ...
    fun addIncident(incident: Incident): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TYPE, incident.type)
        values.put(KEY_DESCRIPTION, incident.description)
        values.put(KEY_IMAGE_URL, incident.imageUrl)
        values.put(KEY_TIMESTAMP, incident.timestamp)
        values.put(KEY_LATITUDE, incident.latitude)
        values.put(KEY_LONGITUDE, incident.longitude)
        values.put(KEY_LOCATION_STRING, incident.locationString)
        values.put(KEY_REPORTER_NAME, incident.reporterName)

        val id = db.insert(TABLE_INCIDENTS, null, values)
        db.close()
        return id
    }

    fun getAllIncidents(): List<Incident> {
        val incidentList = ArrayList<Incident>()
        val selectQuery = "SELECT  * FROM $TABLE_INCIDENTS ORDER BY $KEY_TIMESTAMP DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val incident = Incident(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TIMESTAMP)),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE)),
                    locationString = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION_STRING)),
                    reporterName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPORTER_NAME))
                )
                incidentList.add(incident)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return incidentList
    }


    // ... Emergency Contact methods ...

    fun addEmergencyContact(contact: EmergencyContact): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CONTACT_NAME, contact.name)
        values.put(KEY_CONTACT_PHONE, contact.phoneNumber)
        val id = db.insert(TABLE_EMERGENCY_CONTACTS, null, values)
        db.close()
        return id
    }

    fun getAllEmergencyContacts(): List<EmergencyContact> {
        val contactList = ArrayList<EmergencyContact>()
        val selectQuery = "SELECT  * FROM $TABLE_EMERGENCY_CONTACTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val contact = EmergencyContact(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CONTACT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_NAME)),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTACT_PHONE))
                )
                contactList.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contactList
    }

    fun deleteEmergencyContact(contactId: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_EMERGENCY_CONTACTS, "$KEY_CONTACT_ID = ?", arrayOf(contactId.toString()))
        db.close()
    }
}
