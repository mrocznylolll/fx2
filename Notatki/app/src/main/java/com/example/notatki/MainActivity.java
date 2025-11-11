package com.example.notatki;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleInput, noteInput;
    private Button saveButton;
    private TextView notesDisplay;

    private EditText deleteIdInput;
    private Button deleteButton;

    private EditText updateIdInput, updateNoteInput;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        noteInput = findViewById(R.id.noteInput);
        saveButton = findViewById(R.id.saveButton);
        notesDisplay = findViewById(R.id.notesDisplay);

        saveButton.setOnClickListener(v -> addNote());

        deleteIdInput = findViewById(R.id.deleteIdInput);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteNote());

        updateIdInput = findViewById(R.id.updateIdInput);
        updateNoteInput = findViewById(R.id.updateNoteInput);
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> updateNote());

        loadNotes();
    }

    private void addNote() {
        String title = titleInput.getText().toString();
        String note = noteInput.getText().toString();
        if (note.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();

        titleInput.setText("");
        noteInput.setText("");
        loadNotes();
    }

    private void loadNotes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null, null, null, null, null, null);

        StringBuilder notes = new StringBuilder();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));
            notes.append(id).append(". ").append(title).append(" - ").append(note).append("\n");
        }
        cursor.close();
        db.close();

        notesDisplay.setText(notes.toString());
    }

    private void deleteNote() {
        String idToDelete = deleteIdInput.getText().toString();
        if (idToDelete.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{idToDelete});
        db.close();
        loadNotes();
    }

    private void updateNote() {
        String id = updateIdInput.getText().toString();
        String newNote = updateNoteInput.getText().toString();
        if (id.isEmpty() || newNote.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, newNote);
        db.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{id});
        db.close();
        loadNotes();
    }
}