package com.fvjapps.fpass.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.views.HueSliderView;
import com.fvjapps.fpass.views.SaturationBrightnessView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorPickerDialog extends DialogFragment {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?([0-9A-Fa-f]{6})$");

    private SaturationBrightnessView satBrightView;
    private HueSliderView hueSliderView;
    private View colorPreview;
    private EditText hexText;
    private OnColorSelectedListener listener;
    private boolean isUpdatingFromText = false;

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_color_picker, null);

        satBrightView = view.findViewById(R.id.view_sat_bright);
        hueSliderView = view.findViewById(R.id.view_hue);
        colorPreview = view.findViewById(R.id.view_color_preview);
        hexText = view.findViewById(R.id.text_hex_value);

        updatePreview(Color.HSVToColor(new float[]{0f, 1f, 1f}));

        satBrightView.setOnColorChangeListener(color -> {
            updatePreview(color);
        });

        hueSliderView.setOnHueChangeListener(hue -> {
            satBrightView.setHue(hue);
        });

        hexText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingFromText) return;
                String input = s.toString().trim();
                Matcher matcher = HEX_PATTERN.matcher(input);
                if (matcher.matches()) {
                    hexText.setError(null);
                    int color = Color.parseColor("#" + matcher.group(1));
                    applyHexColor(color);
                } else if (input.isEmpty()) {
                    hexText.setError(null);
                } else {
                    hexText.setError("Invalid hex color");
                }
            }
        });

        view.findViewById(R.id.btn_select_color).setOnClickListener(v -> {
            if (listener != null) {
                listener.onColorSelected(satBrightView.getCurrentColor());
            }
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }

    private void applyHexColor(int color) {
        isUpdatingFromText = true;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hueSliderView.setHue(hsv[0]);
        satBrightView.setHue(hsv[0]);
        satBrightView.setSaturationBrightness(hsv[1], hsv[2]);
        updatePreview(color);
        isUpdatingFromText = false;
    }

    private void updatePreview(int color) {
        colorPreview.setBackgroundColor(color);
        if (!isUpdatingFromText) {
            isUpdatingFromText = true;
            hexText.setText(String.format("#%06X", (0xFFFFFF & color)));
            isUpdatingFromText = false;
        }
    }
}
