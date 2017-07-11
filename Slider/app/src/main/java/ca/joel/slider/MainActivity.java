package ca.joel.slider;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ViewPager vpgSlider;
    FloatingActionButton flbAdd;

    CustomSwipeAdapter adapter;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpgSlider = (ViewPager) findViewById(R.id.vpgSlider);
        adapter = new CustomSwipeAdapter(this);

        vpgSlider.setAdapter(adapter);

        setupButtons();
    }

    private void setupButtons() {
        flbAdd = (FloatingActionButton) findViewById(R.id.flbAdd);
        flbAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                        gallery.setType("image/*");
                        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            adapter.add(imageUri);
            adapter.notifyDataSetChanged();
            vpgSlider.setCurrentItem(adapter.getCount());
        }
    }
}
