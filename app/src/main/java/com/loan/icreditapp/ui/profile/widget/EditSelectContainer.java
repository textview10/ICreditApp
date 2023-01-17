package com.loan.icreditapp.ui.profile.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loan.icreditapp.R;

public class EditSelectContainer extends FrameLayout {

    private String hintStr;
    private @DrawableRes
    int drawableRes;

    private TextInputLayout inputLayout;
    private TextInputEditText editText;
    private ImageView ivClear;

    public EditSelectContainer(@NonNull Context context) {
        super(context);
        initializeView(context, null);
    }

    public EditSelectContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context, attrs);
    }

    public EditSelectContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context, attrs);
    }

    private void initializeView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.select_view);
            if (typedArray != null) {
                hintStr = typedArray.getString(R.styleable.select_view_select_view_hint);
                drawableRes = typedArray.getResourceId(R.styleable.select_view_select_view_icon, R.drawable.ic_edit_text_clear);
            }
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_select_text, this, false);
        inputLayout = view.findViewById(R.id.edit_select_view_input_layout);
        editText = view.findViewById(R.id.edit_select_view_edit_text);
        ivClear = view.findViewById(R.id.edit_select_view_clear);

        inputLayout.setHint(hintStr);
        if (drawableRes != 0) {
            ivClear.setImageResource(drawableRes);
        }
        view.setOnClickListener(v -> {
            if (mListener != null){
                mListener.onClick();
            }
        });
        addView(view);
    }

    public boolean isEmptyText() {
        if (editText == null) {
            return true;
        }
        return TextUtils.isEmpty(getText());
    }

    public String getText(){
        if (editText == null) {
            return "";
        }
        return editText.getText().toString();
    }

    public void setEditTextAndSelection(String editTextStr){
        if (editText != null && !TextUtils.isEmpty(editTextStr)){
            editText.setText(editTextStr);
            editText.setSelection(editTextStr.length());
        }
    }

    private OnBgClickListener mListener;

    public void setOnBgClickListener(OnBgClickListener listener){
        this.mListener = listener;
    }

    public interface OnBgClickListener {
        void onClick();
    }
}
