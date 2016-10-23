package com.rhysmakesthings.discoverlocalprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null){
            EditText url = (EditText) findViewById(R.id.editText);
            url.setText(sharedText);
        }
        Button fab = (Button) findViewById(R.id.place);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView url = (TextView) findViewById(R.id.editText);
                TextView title = (TextView) findViewById(R.id.editText2);
                TextView tags = (TextView) findViewById(R.id.editText3);
                Intent i = new Intent(AddForm.this, MainActivity.class);
                i.putExtra("url", url.getText().toString());
                i.putExtra("title", title.getText().toString());
                i.putExtra("tags", tags.getText().toString());
                startActivity(i);
            }
        });
    }

}
