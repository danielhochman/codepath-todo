package com.runops.codepathtodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class EditItemActivity extends ActionBarActivity {

    String itemText;
    Integer itemPosition;

    EditText editTextExistingView;
    TextView textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editTextExistingView = (EditText) findViewById(R.id.editTextExistingItemView);
        textViewDate = (TextView) findViewById(R.id.textViewDate);

        itemText = getIntent().getStringExtra("itemText");
        itemPosition = getIntent().getIntExtra("itemPosition", -1);

        editTextExistingView.setText(itemText);
//        textViewDate.setText("Tue, May 3, 2014" + "     ");

    }

    public void onSaveItem(View view) {
        Intent intent = new Intent();
        intent.putExtra("itemText", editTextExistingView.getText().toString());
        intent.putExtra("itemPosition", itemPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onTextViewDateClick(View view) {
    }
}
