package com.malalisy.flipviewapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.malalisy.flipview.FlipView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    FlipView flipView;
    Button btnTop, btnBottom, btnLeft, btnRight;
    EditText horizontalDuration, verticalDuration;
    Switch enableHorizontal, enableVertical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        flipView = (FlipView) findViewById(R.id.flipView);

        btnTop = (Button) findViewById(R.id.btnTop);
        btnBottom = (Button) findViewById(R.id.btnBottom);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        horizontalDuration = (EditText) findViewById(R.id.horizontalDuration);
        verticalDuration = (EditText) findViewById(R.id.verticalDuration);

        enableHorizontal = (Switch) findViewById(R.id.enableHorizontal);
        enableVertical = (Switch) findViewById(R.id.enableVetical);

        enableHorizontal.setChecked(flipView.isHorizontalSwiping());
        enableVertical.setChecked(flipView.isVerticalSwiping());

        horizontalDuration.setText(String.valueOf(flipView.getHorizontalDuration()));
        verticalDuration.setText(String.valueOf(flipView.getVerticalDuration()));

        btnTop.setOnClickListener(this);
        btnBottom.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnLeft.setOnClickListener(this);

        enableHorizontal.setOnCheckedChangeListener(this);
        enableVertical.setOnCheckedChangeListener(this);

        horizontalDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    flipView.setHorizontalDuration(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid Horizontal Duration !", Toast.LENGTH_LONG).show();
                }
            }
        });

        verticalDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    flipView.setVerticalDuration(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid Vertical Duration !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnBottom:
                flipView.flip(FlipView.Direction.TO_BOTTOM);
                break;
            case R.id.btnTop:
                flipView.flip(FlipView.Direction.TO_TOP);
                break;
            case R.id.btnRight:
                flipView.flip(FlipView.Direction.TO_RIGHT);
                break;
            case R.id.btnLeft:
                flipView.flip(FlipView.Direction.TO_LEFT);
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.enableHorizontal:
                flipView.setHorizontalSwiping(isChecked);
                break;
            case R.id.enableVetical:
                flipView.setVerticalSwiping(isChecked);
                break;
        }
    }
}
