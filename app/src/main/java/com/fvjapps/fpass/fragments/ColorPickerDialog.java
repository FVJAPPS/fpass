package com.fvjapps.fpass.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.fvjapps.fpass.R;
import com.fvjapps.fpass.views.HueSliderView;
import com.fvjapps.fpass.views.SaturationBrightnessView;

public class ColorPickerDialog extends DialogFragment {

    private SaturationBrightnessView satBrightView;
    private HueSliderView hueSliderView;
    private View colorPreview;
    private TextView hexText;
    private OnColorSelectedListener listener;

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

    private void updatePreview(int color) {
        colorPreview.setBackgroundColor(color);
        hexText.setText(String.format("#%06X", (0xFFFFFF & color)));
    }
}
