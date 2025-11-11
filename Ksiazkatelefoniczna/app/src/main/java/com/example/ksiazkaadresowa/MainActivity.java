package com.example.ksiazkaadresowa;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelperContacts dbHelper;
    private EditText nameInput, phoneInput;
    private Button saveButton;
    private TextView contactsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelperContacts(this);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        saveButton = findViewById(R.id.saveButton);
        contactsDisplay = findViewById(R.id.contactsDisplay);

        saveButton.setOnClickListener(v -> addContact());
        loadContacts();
    }

    private void addContact() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();

        if (name.isEmpty() || phone.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelperContacts.COLUMN_NAME, name);
        values.put(DatabaseHelperContacts.COLUMN_PHONE, phone);
        db.insert(DatabaseHelperContacts.TABLE_CONTACTS, null, values);
        db.close();

        nameInput.setText("");
        phoneInput.setText("");
        loadContacts();
    }

    private void loadContacts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelperContacts.TABLE_CONTACTS, null, null, null, null, null, null);

        StringBuilder contacts = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelperContacts.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelperContacts.COLUMN_PHONE));
            contacts.append(name).append(": ").append(phone).append("\n");
        }
        cursor.close();
        db.close();

        contactsDisplay.setText(contacts.toString());
    }
}