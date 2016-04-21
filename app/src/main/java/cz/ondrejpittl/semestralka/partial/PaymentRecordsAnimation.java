package cz.ondrejpittl.semestralka.partial;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.view.animation.AlphaAnimation;

import java.util.ArrayList;

import cz.ondrejpittl.semestralka.layout.PaymentRecord;

/**
 * Created by OndrejPittl on 15.04.16.
 */
public class PaymentRecordsAnimation extends AsyncTask<String, Void, String> {

    private ArrayList<PaymentRecord> records;



    public PaymentRecordsAnimation(ArrayList<PaymentRecord> records) {
        super();
        this.records = records;
    }

    protected String doInBackground(String... params) {
        for(PaymentRecord rec : records) {

            /*AlphaAnimation a = new AlphaAnimation(0.2f, 1.0f);
            a.setDuration(1000);
            a.setStartOffset(5000);
            a.setFillAfter(true);
            rec.startAnimation(a);*/

            ObjectAnimator anim = ObjectAnimator.ofFloat(rec, "alpha", 0f, 1f);
            anim.setDuration(1000);
            anim.start();


            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
