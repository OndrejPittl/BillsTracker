package cz.ondrejpittl.semestralka;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import cz.ondrejpittl.semestralka.partial.Designer;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Designer.setFullscreenActivity(this);
        setContentView(R.layout.activity_about);
        Designer.updateDesign(this);

        TextView tv = (TextView) findViewById(R.id.tvLink);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
