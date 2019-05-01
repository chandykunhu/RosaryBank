package com.cksolutions.rosary.prayers.rosary.normalenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cksolutions.rosary.R;
import com.cksolutions.rosary.prayers.rosary.Rosary;
import java.util.ArrayList;
import java.util.List;
import static com.cksolutions.rosary.prayers.rosary.normalenglish.NormalEnglishRosary.RosaryType;

public class EnglishRosary extends AppCompatActivity {

    private List<Rosary> rosaryList;
    private int mysteryNo;
    private String mysteryStory;
    private String mysteryName;
    private String mysteryImage;

    TextView tvMysteryStory;
    TextView tvMysteryNo;
    ImageView ivMysteryImage;
    Button btnPrevious;
    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_rosary);

        tvMysteryStory = findViewById(R.id.tvMysteryStory);
        tvMysteryStory.setMovementMethod(new ScrollingMovementMethod());
        tvMysteryNo = findViewById(R.id.tvMysteryNo);
        ivMysteryImage = findViewById(R.id.ivMysteryImage);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        rosaryList = new ArrayList<>();


        prepareRosary();
    }


    private void prepareRosary() {
        if (RosaryType == 1) {

            this.mysteryNo = 1;
            this.mysteryStory = "rosary_english_annunciation";
            this.mysteryImage = "rosary_english_annunciation";
            this.mysteryName = "THE ANNUNCIATION";

            tvMysteryNo.setText(Integer.toString(this.mysteryNo)+"\n"+this.mysteryName);
            tvMysteryStory.setText(getResources().getIdentifier(this.mysteryStory, "string", getPackageName()));
            ivMysteryImage.setImageResource(getResources().getIdentifier(this.mysteryImage, "drawable", getPackageName()));

            btnPrevious.setEnabled(false);

            Rosary rosary = new Rosary(1, "THE ANNUNCIATION", "rosary_english_annunciation", "rosary_english_annunciation");
            rosaryList.add(rosary);
            rosary = new Rosary(2, "THE VISITATION", "rosary_english_visitation", "rosary_english_visitation");
            rosaryList.add(rosary);
            rosary = new Rosary(3, "THE NATIVITY", "rosary_english_nativity", "rosary_english_nativity");
            rosaryList.add(rosary);
            rosary = new Rosary(4, "THE PRESENTATION", "rosary_english_presentation", "rosary_english_presentation");
            rosaryList.add(rosary);
            rosary = new Rosary(5, "THE FINDING OF JESUS IN THE TEMPLE", "rosary_english_findingjesus", "rosary_english_findingjesus");
            rosaryList.add(rosary);
        } else if (RosaryType == 2) {

            this.mysteryNo = 1;
            this.mysteryStory = "rosary_english_agony";
            this.mysteryImage = "rosary_english_agony";
            this.mysteryName = "THE AGONY IN THE GARDEN";

            tvMysteryNo.setText(Integer.toString(this.mysteryNo)+"\n"+this.mysteryName);
            tvMysteryStory.setText(getResources().getIdentifier(this.mysteryStory, "string", getPackageName()));
            ivMysteryImage.setImageResource(getResources().getIdentifier(this.mysteryImage, "drawable", getPackageName()));

            btnPrevious.setEnabled(false);

            Rosary rosary = new Rosary(1, "THE AGONY IN THE GARDEN", "rosary_english_agony", "rosary_english_agony");
            rosaryList.add(rosary);
            rosary = new Rosary(2, "THE SCOURGING AT THE PILLAR", "rosary_english_scourging", "rosary_english_scourging");
            rosaryList.add(rosary);
            rosary = new Rosary(3, "THE CROWNING WITH THORNS", "rosary_english_crowning", "rosary_english_crowning");
            rosaryList.add(rosary);
            rosary = new Rosary(4, "THE CARRYING OF THE CROSS", "rosary_english_carrying", "rosary_english_carrying");
            rosaryList.add(rosary);
            rosary = new Rosary(5, "THE CRUCIFIXION", "rosary_english_crucifixion", "rosary_english_crucifixion");
            rosaryList.add(rosary);

        } else if (RosaryType == 3) {
            this.mysteryNo = 1;
            this.mysteryStory = "rosary_english_resurrection";
            this.mysteryImage = "rosary_english_resurrection";
            this.mysteryName = "THE RESURRECTION";

            tvMysteryNo.setText(Integer.toString(this.mysteryNo)+"\n"+this.mysteryName);
            tvMysteryStory.setText(getResources().getIdentifier(this.mysteryStory, "string", getPackageName()));
            ivMysteryImage.setImageResource(getResources().getIdentifier(this.mysteryImage, "drawable", getPackageName()));

            btnPrevious.setEnabled(false);

            Rosary rosary = new Rosary(1, "THE RESURRECTION", "rosary_english_resurrection", "rosary_english_resurrection");
            rosaryList.add(rosary);
            rosary = new Rosary(2, "THE ASCENSION", "rosary_english_ascension", "rosary_english_ascension");
            rosaryList.add(rosary);
            rosary = new Rosary(3, "THE DESCENT OF THE HOLY SPIRIT", "rosary_english_descent", "rosary_english_descent");
            rosaryList.add(rosary);
            rosary = new Rosary(4, "THE ASSUMPTION", "rosary_english_assumption", "rosary_english_assumption");
            rosaryList.add(rosary);
            rosary = new Rosary(5, "THE CORONATION", "rosary_english_coronation", "rosary_english_coronation");
            rosaryList.add(rosary);

        } else if (RosaryType == 4) {

            this.mysteryNo = 1;
            this.mysteryStory = "rosary_english_baptism";
            this.mysteryImage = "rosary_english_baptism";
            this.mysteryName = "THE BAPTISM OF THE LORD";

            tvMysteryNo.setText(Integer.toString(this.mysteryNo)+"\n"+this.mysteryName);
            tvMysteryStory.setText(getResources().getIdentifier(this.mysteryStory, "string", getPackageName()));
            ivMysteryImage.setImageResource(getResources().getIdentifier(this.mysteryImage, "drawable", getPackageName()));

            btnPrevious.setEnabled(false);


            Rosary rosary = new Rosary(1, "THE BAPTISM OF THE LORD", "rosary_english_baptism", "rosary_english_baptism");
            rosaryList.add(rosary);
            rosary = new Rosary(2, "THE WEDDING OF CANA", "rosary_english_cana", "rosary_english_cana");
            rosaryList.add(rosary);
            rosary = new Rosary(3, "THE PROCLAMATION OF THE KINGDOM", "rosary_english_proclamation", "rosary_english_proclamation");
            rosaryList.add(rosary);
            rosary = new Rosary(4, "THE TRANSFIGURATION", "rosary_english_transfiguration", "rosary_english_transfiguration");
            rosaryList.add(rosary);
            rosary = new Rosary(5, "THE INSTITUTION OF THE EUCHARIST", "rosary_english_eucharist", "rosary_english_eucharist");
            rosaryList.add(rosary);

        }
    }

    public void onClickPrevious(View v) {
        if(this.mysteryNo > 1) {

            SetMystery(this.mysteryNo - 1);
        }
    }
    public void onClickNext(View v) {
        if(this.mysteryNo < 5) {

            SetMystery(this.mysteryNo + 1);
        }
    }

    public  void SetMystery(int position) {

        Rosary r = rosaryList.get(position - 1);

        this.mysteryNo = r.getmysteryNumber();
        this.mysteryImage = r.getmysteryImage();
        this.mysteryStory = r.getmysteryStory();
        this.mysteryName = r.getmysteryName();

        tvMysteryNo.setText(Integer.toString(this.mysteryNo)+"\n"+this.mysteryName);
        tvMysteryStory.setText(getResources().getIdentifier(this.mysteryStory, "string", getPackageName()));
        ivMysteryImage.setImageResource(getResources().getIdentifier(this.mysteryImage, "drawable", getPackageName()));

        if(this.mysteryNo <= 1) {
            btnPrevious.setEnabled(false);
        }else {
            btnPrevious.setEnabled(true);
        }
        if(this.mysteryNo >= 5) {
            btnNext.setEnabled(false);
        }else {
            btnNext.setEnabled(true);
        }
    }
}
