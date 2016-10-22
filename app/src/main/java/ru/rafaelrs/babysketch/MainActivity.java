package ru.rafaelrs.babysketch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import ru.rafaelrs.babysketch.adapter.ColorSelectorAdapter;

/**
 * Created with Android Studio
 * User: rafaelrs
 * Date: 21.10.16
 * To change this template use File | Settings | File Templates.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //Images for this activity taken from http://www.freeiconspng.com
        setContentView(R.layout.main_activity);

        initializeWidgets();
    }

    private void initializeWidgets() {

        final PaintSurface paintArea = (PaintSurface) findViewById(R.id.paint_area);
        GridView colorSelector = (GridView) findViewById(R.id.color_selector);
        final ColorSelectorAdapter colorSelectorAdapter = new ColorSelectorAdapter(this, R.layout.adapter_color_selector,
                new Integer[] {
                        Color.RED,
                        Color.GREEN,
                        Color.BLUE,
                        Color.YELLOW,
                        Color.CYAN,
                        Color.MAGENTA,
                        0xFFFF9300,
                        0xFFA96100
                });
        colorSelector.setAdapter(colorSelectorAdapter);
        colorSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                paintArea.assignToBrushPaintColor(colorSelectorAdapter.getItem(position));
            }
        });

        findViewById(R.id.eraser_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintArea.assignToBrushEraser();
            }
        });

        findViewById(R.id.clear_button).setOnTouchListener(new VeryLongClickListener() {
            @Override
            public void doAction() {
                paintArea.erasePaint();
            }
        });

        findViewById(R.id.exit_button).setOnTouchListener(new VeryLongClickListener() {
            @Override
            public void doAction() {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        final PaintSurface paintArea = (PaintSurface) findViewById(R.id.paint_area);
        paintArea.savePaint();
    }
}
