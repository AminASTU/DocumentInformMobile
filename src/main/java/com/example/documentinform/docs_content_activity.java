package com.example.documentinform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;

public class docs_content_activity extends AppCompatActivity {
    private TextView textdocs;
    private TextView namedocs;
    private int menu_index;
    private int position;
    public int[] array_docs = {R.string.doc1, R.string.doc2, R.string.doc3, R.string.doc4};
    private String[] array;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docs_content);
        textdocs = findViewById(R.id.text_doc);
        namedocs = findViewById(R.id.name_doc);
        resiveIntent();
    }
    private void resiveIntent(){
        Intent i = getIntent();
        if(i != null){
            menu_index = i.getIntExtra("menu_index", 0);
            position = i.getIntExtra("position", 0);
        }
        switch (menu_index){
            case 0:
                array = getResources().getStringArray(R.array.docs_array);
                textdocs.setText(array_docs[position]);
                namedocs.setText(array[position]);

                break;
            case 1:
                Button familiarized = findViewById(R.id.button_familiarized);
                familiarized.setVisibility(View.GONE);
                break;
        }
    }

    public void OnClickFamiliarized(View view) {
        Intent intent = new Intent(docs_content_activity.this, MainActivity.class);
        startActivity(intent);
    }
}
