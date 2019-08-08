package com.gavin.com.stickydecoration.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gavin.com.stickydecoration.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
    }

    public void toSticky(View view) {
        startActivity(new Intent(this, StickyActivity.class));
    }

    public void toStickyGrid(View view) {
        Intent intent = new Intent(this, StickyGridActivity.class);
        startActivity(intent);
    }

    public void toPowerfulSticky(View view) {
        startActivity(new Intent(this, PowerfulStickyActivity.class));
    }

    public void toPowerfulStickyGrid(View view) {
        Intent intent = new Intent(this, PowerfulStickyGridActivity.class);
        startActivity(intent);
    }

    public void toPowerfulSticky2(View view) {
        startActivity(new Intent(this, BeautifulActivity.class));
    }

    public void toExpandableList(View view) {
        startActivity(new Intent(this, ExpandableActivity.class));
    }
}
