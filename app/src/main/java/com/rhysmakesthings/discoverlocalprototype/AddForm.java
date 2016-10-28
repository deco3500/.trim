package com.rhysmakesthings.discoverlocalprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class AddForm extends AppCompatActivity {
    private CheckAdapter friendCheck;
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
                final Intent i = new Intent(AddForm.this, MainActivity.class);
                if (url.getText().length() ==0 || title.getText().length() == 0 || tags.getText().length() ==0){
                    Toast.makeText(AddForm.this, "Fill in every field before placing an article",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                i.putExtra("url", url.getText().toString());
                i.putExtra("title", title.getText().toString());
                i.putExtra("tags", tags.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(AddForm.this);
                builder.setTitle("Select Friends To Challenge");
                ArrayList<String> temp = MainActivity.friends;
                String[] friendL = temp.toArray(new String[0]);
                CheckAdapter adapter = new CheckAdapter(AddForm.this, friendL,getIntent().getIntExtra("friend",-1));
                friendCheck = adapter;
                ListView friend = new ListView(AddForm.this);
                builder.setView(friend);
                friend.setAdapter(adapter);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String f = "";
                        for (int c = 0; c <friendCheck.selectedFriends.size(); c++){
                            String change = MainActivity.friends.get(friendCheck.selectedFriends.get(c));
                            change = change.substring(0,change.indexOf("\t"));
                            f += change;
                            if (c < friendCheck.selectedFriends.size()-1){
                                f+=", ";
                            }
                        }
                        if (friendCheck.selectedFriends.size() ==0){
                            Toast.makeText(AddForm.this, "Select someone to challenge first!",
                                    Toast.LENGTH_LONG).show();
                        } else{
                            i.putExtra("challenged", f);
                            startActivity(i);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

}
