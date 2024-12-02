package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Model.ToDoModel;
import com.example.myapplication.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText mEditText;
    private Button mSaveButton;

    private DataBaseHelper myDB;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task,  container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.editText);   // Make sure the ID is correct in XML
        mSaveButton = view.findViewById(R.id.addButton);  // Make sure this ID is correct in XML

        myDB = new DataBaseHelper(getActivity());

        // Initially disable the Save button if EditText is empty
        mSaveButton.setEnabled(false);
        mSaveButton.setBackgroundColor(Color.GRAY);  // Set initial button color

        // Retrieve task information if updating
        Bundle bundle = getArguments();
        boolean isUpdate = false;
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);

            // If there is a task, disable the save button until changes are made
            if (task.length() > 0) {
                mSaveButton.setEnabled(false); // Disable if there's no change
                mSaveButton.setBackgroundColor(Color.GRAY);
            }
        }

        // Watch for text changes to enable/disable Save button
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().isEmpty()) {
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.GRAY);  // Disable and gray out the button
                } else {
                    mSaveButton.setEnabled(true);
                    mSaveButton.setBackgroundColor(Color.parseColor("#4CAF50"));  // Enable and change to green (or any color)
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Save button click listener
        final boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
            if (finalIsUpdate) {
                myDB.updateTask(bundle.getInt("ID"), text);
            } else {
                ToDoModel item = new ToDoModel();
                item.setTask(text);
                item.setStatus(0);  // Assuming status 0 means pending
                myDB.insertTask(item);
            }
            dismiss();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogClosedListener) {
            ((OnDialogClosedListener) activity).onDialogClose(dialog);
        }
    }
}
