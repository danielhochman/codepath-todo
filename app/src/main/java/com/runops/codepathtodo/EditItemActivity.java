package com.runops.codepathtodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class EditItemActivity extends ActionBarActivity {

    String itemText;
    Integer itemPosition;

    EditText editTextExistingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editTextExistingView = (EditText) findViewById(R.id.editTextExistingItemView);

        itemText = getIntent().getStringExtra("itemText");
        itemPosition = getIntent().getIntExtra("itemPosition", -1);

        editTextExistingView.setText(itemText);
    }

    public void onSaveItem(View view) {
        Intent intent = new Intent();
        intent.putExtra("itemText", editTextExistingView.getText().toString());

        setResult(RESULT_OK, intent);
        finish();
    }
}
