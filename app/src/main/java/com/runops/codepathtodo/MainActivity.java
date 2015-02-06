package com.runops.codepathtodo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private final int REQUEST_CODE = 10;
    private final String LOG_TAG = "todo";

    ArrayList<Document> items;
    ArrayAdapter<Document> itemsAdapter;
    ListView listViewItems;

    Manager manager;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<Document>();
        itemsAdapter = new DocumentAdapter(this, R.layout.listview_item_document, items);

        listViewItems = (ListView) findViewById(R.id.listViewItems);
        listViewItems.setAdapter(itemsAdapter);

        setupListViewListener();

        // Database setup
        try {
            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not create manager", e);
            return;
        }

        try {
            db = manager.getDatabase("codepathtodo");
        } catch (CouchbaseLiteException e) {
            Log.e(LOG_TAG, "Could not get database", e);
        }

        try {
            Query allDocumentsQuery = db.createAllDocumentsQuery();
            QueryEnumerator result = allDocumentsQuery.run();
            for (Iterator<QueryRow> iterator = result; iterator.hasNext(); ) {
                QueryRow row = iterator.next();
                Document document = row.getDocument();
                itemsAdapter.add(document);
            }
        } catch (CouchbaseLiteException e) {
            Log.e(LOG_TAG, "Could not load todo items from database", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Document document = items.get(intent.getIntExtra("itemPosition", -1));
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put("itemText", intent.getStringExtra("itemText"));
            try {
                document.putProperties(updatedProperties);
                itemsAdapter.notifyDataSetChanged();
            } catch (CouchbaseLiteException e) {
                Log.e(LOG_TAG, "Could not update document", e);
            }
        }
    }

    public void onAddItem(View view) {
        EditText editTextAddItem = (EditText) findViewById(R.id.editTextAddItem);
        String itemText = editTextAddItem.getText().toString();

        editTextAddItem.setText("");

        // Save the item to the database
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("itemText", itemText);
        docContent.put("itemPosition", items.size() - 1);
        Document document = db.createDocument();
        try {
            document.putProperties(docContent);
            itemsAdapter.add(document);
            Log.d(LOG_TAG, "Wrote document with ID " + document.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(LOG_TAG, "Unable to write to db", e);
        }
    }

    private void setupListViewListener() {
        AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Document document = items.get(position);
                try {
                    document.delete();
                    items.remove(position);
                    itemsAdapter.notifyDataSetChanged();
                } catch (CouchbaseLiteException e) {
                    Log.e(LOG_TAG, "Unable to delete from db", e);
                }
                return true;
            }
        };

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditView(items.get(position), position);
            }
        };

        listViewItems.setOnItemLongClickListener(itemLongClickListener);
        listViewItems.setOnItemClickListener(itemClickListener);
    }

    public void launchEditView(Document document, Integer itemPosition) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("itemText", (String) document.getProperty("itemText"));
        intent.putExtra("itemPosition", itemPosition);
        startActivityForResult(intent, REQUEST_CODE);
    }


}
