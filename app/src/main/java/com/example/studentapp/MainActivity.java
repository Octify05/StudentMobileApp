package com.example.studentapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_AGE = 17;

    private DatabaseHelper db;
    private EditText etName, etId;
    private TextView tvAge;
    private SeekBar seekAge;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private List<Student> students = new ArrayList<>();
    private final List<String> listData = new ArrayList<>();

    // Age from the SeekBar: progress (0..33) + 17 => 17..50
    private int getCurrentAge() {
        return seekAge.getProgress() + MIN_AGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        tvAge = findViewById(R.id.tvAge);
        seekAge = findViewById(R.id.seekAge);
        listView = findViewById(R.id.listStudents);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        // Update the age label whenever the slider moves
        seekAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAge.setText("Age: " + getCurrentAge());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Tapping a list item fills the form (makes EDIT / DELETE easier)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Student s = students.get(position);
            etName.setText(s.getName());
            etId.setText(s.getId());
            seekAge.setProgress(s.getAge() - MIN_AGE);
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        btnAdd.setOnClickListener(v -> addStudent());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());
        btnView.setOnClickListener(v -> viewStudents());

        viewStudents();
    }

    private void addStudent() {
        String name = etName.getText().toString().trim();
        String id = etId.getText().toString().trim();
        if (name.isEmpty() || id.isEmpty()) {
            toast("Name and ID are required");
            return;
        }
        if (db.insertStudent(new Student(id, name, getCurrentAge()))) {
            toast("Student added successfully");
            clearForm();
            viewStudents();
        } else {
            toast("Failed: Student ID already exists");
        }
    }

    private void editStudent() {
        String name = etName.getText().toString().trim();
        String id = etId.getText().toString().trim();
        if (name.isEmpty() || id.isEmpty()) {
            toast("Name and ID are required");
            return;
        }
        if (db.updateStudent(new Student(id, name, getCurrentAge()))) {
            toast("Student updated successfully");
            clearForm();
            viewStudents();
        } else {
            toast("Failed: Student ID not found");
        }
    }

    private void deleteStudent() {
        String id = etId.getText().toString().trim();
        if (id.isEmpty()) {
            toast("Enter the Student ID to delete");
            return;
        }
        if (db.deleteStudent(id)) {
            toast("Student deleted successfully");
            clearForm();
            viewStudents();
        } else {
            toast("Failed: Student ID not found");
        }
    }

    private void viewStudents() {
        students = db.getAllStudents();
        listData.clear();
        for (Student s : students) {
            listData.add(s.getId() + " - " + s.getName() + " (Age: " + s.getAge() + ")");
        }
        adapter.notifyDataSetChanged();
    }

    private void clearForm() {
        etName.getText().clear();
        etId.getText().clear();
        seekAge.setProgress(0);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}