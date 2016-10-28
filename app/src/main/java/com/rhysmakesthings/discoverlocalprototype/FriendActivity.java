package com.rhysmakesthings.discoverlocalprototype;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FriendActivity extends AppCompatActivity {
    ListView friend = null;
    String[] friendL = null;
    ArrayList<Integer> friendS = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        System.out.println(getIntent().getStringArrayExtra("friendL"));
        friendL = getIntent().getStringArrayExtra("friendL");
        friendS = getIntent().getIntegerArrayListExtra("friendS");
        ArrayAdapter adapter = new ArrayAdapter<String>(FriendActivity.this,R.layout.listview,R.id.label, friendL);
        friend = (ListView) findViewById(R.id.listView);
        friend.setAdapter(adapter);
        friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                final String item = ((TextView)view.findViewById(R.id.label)).getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle(item);
                TextView t = new TextView(FriendActivity.this);
                t.setText("Articles read:"+random(2,40)+"\nChallenges Issued:"+random(2,40));
                LinearLayout f = new LinearLayout(FriendActivity.this);
                f.addView(t);
                Button b = new Button(FriendActivity.this);
                b.setText("Challenge "+item.substring(0,item.lastIndexOf('\t')));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(FriendActivity.this, AddForm.class);
                        i.putExtra("friend",MainActivity.friends.indexOf(item));
                        startActivity(i);
                    }
                });
                f.addView(b);
                builder.setView(f);
                builder.show();
            }
        });
        Button btnP = (Button) findViewById(R.id.button2);
        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FriendActivity.this, MainActivity.class);
                i.putExtra("friendL", friendL);
                i.putExtra("friendS", friendS);
                startActivity(i);
            }
        });
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle("Enter Friend's Username");

                // Set up the input
                final EditText input = new EditText(FriendActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(MainActivity.friends.toArray(new String[0])));
                        Double a =  250*(Math.floor(Math.random()*40)+1);
                        while(friendS.contains(a.intValue())){
                            a =  250*(Math.floor(Math.random()*40)+1);
                        }
                        temp.add(((TextView) input).getText().toString() + "\t(Score: "+a.intValue()+")");
                        friendS.add(a.intValue());

                        HashMap<Integer, String> t = new HashMap<Integer,String>();
                        System.out.println(temp);
                        for (int i=0; i < temp.size();i++){
                            t.put(friendS.get(i), temp.get(i));
                        }
                        Collections.sort(friendS);
                        Collections.reverse(friendS);
                        temp = new ArrayList<String>();
                        for (int i=0; i < friendS.size();i++){

                            temp.add(t.get(friendS.get(i)));
                        }
                        MainActivity.friends = temp;
                        friendL = temp.toArray(new String[0]);
                        System.out.println(temp);
                        ArrayAdapter adapter = new ArrayAdapter<String>(FriendActivity.this,R.layout.listview,R.id.label, friendL);
                        friend.setAdapter(adapter);
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
    public int random(int start, int end){
        return new Double(Math.floor((Math.random()*end) + start)).intValue();
    }
}