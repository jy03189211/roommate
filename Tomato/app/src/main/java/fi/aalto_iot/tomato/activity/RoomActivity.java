package fi.aalto_iot.tomato.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fi.aalto_iot.tomato.R;

public class RoomActivity extends AppCompatActivity {

    ImageView roomTitleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomTitleImage = (ImageView) findViewById(R.id.room_title_image);

        // Set room picture to card header
        Picasso.with(this.getBaseContext())
                .load("http://i.imgur.com/We6zFAz.jpg")
                .fit().centerCrop()
                .into(roomTitleImage);

    }


}
