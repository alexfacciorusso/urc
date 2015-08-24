package com.alexfacciorusso.urc.sample;

import com.alexfacciorusso.urc.Urc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        String result = Urc.with("http://mysite.com")
                .fromEndpoint("something/:id/page/:pageNumber")
                .addParameter("id", 2)
                .addParameter("pageNumber", 1)
                .build();

        TextView resultTextView = (TextView) findViewById(R.id.results);
        resultTextView.setText(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
