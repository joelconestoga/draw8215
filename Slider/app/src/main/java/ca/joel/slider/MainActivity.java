package ca.joel.slider;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ViewPager vpgSlider;
    CustomSwipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpgSlider = (ViewPager) findViewById(R.id.vpgSlider);
        adapter = new CustomSwipeAdapter(this);

        vpgSlider.setAdapter(adapter);
    }
}
