package cz.ondrejpittl.semestralka.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import cz.ondrejpittl.semestralka.HomeActivity;
import cz.ondrejpittl.semestralka.R;
import cz.ondrejpittl.semestralka.SettingsActivity;
import cz.ondrejpittl.semestralka.StatisticsActivity;
import cz.ondrejpittl.semestralka.controllers.HomeUIController;
import cz.ondrejpittl.semestralka.models.Statistics;
import cz.ondrejpittl.semestralka.partial.LoadingButtonType;

/**
 * Created by OndrejPittl on 29.04.16.
 */
public class LoadingImgButton extends FrameLayout {

    private Activity activity;

    private LoadingButtonType type;

    private ImageButton btn;

    private ProgressBar bar;



    public LoadingImgButton(Context context) {
        super(context);
    }

    public LoadingImgButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(LoadingButtonType type, Activity activity){
        this.type = type;
        this.activity = activity;
        this.btn = (ImageButton) findViewById(R.id.loadingImgBtn);
        this.bar = (ProgressBar) findViewById(R.id.imgBtnLoader);

        this.setSourceImgs();
        this.setBtnOnClick();
    }

    private void setSourceImgs(){
        switch (type) {
            case SETTINGS:
                this.btn.setImageResource(R.drawable.img_btn_src_settings);
                this.btn.setScaleType(ImageView.ScaleType.FIT_START);
                break;

            default:
            case STATISTICS:
                this.btn.setImageResource(R.drawable.img_btn_src_stats);
                this.btn.setScaleType(ImageView.ScaleType.FIT_END);
                break;
        }
    }

    private void setBtnOnClick(){
        this.btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                final int i;
                final Intent intent;

                btn.setVisibility(INVISIBLE);
                bar.setVisibility(VISIBLE);

                HomeActivity.clearControlsFocus(activity);

                switch (type) {
                    case SETTINGS:
                        i = SettingsActivity.INTENT_INDEX;
                        intent = new Intent(activity, SettingsActivity.class);
                        break;

                    default:
                    case STATISTICS:
                        i = StatisticsActivity.INTENT_INDEX;
                        intent = new Intent(activity, StatisticsActivity.class);
                        break;
                }

                postDelayed(new Runnable() {
                    public void run() {
                        activity.startActivityForResult(intent, i);
                    }
                }, 500);



            }
        });
    }

    public void reset(){
        this.btn.setVisibility(VISIBLE);
        this.bar.setVisibility(INVISIBLE);
    }

}
