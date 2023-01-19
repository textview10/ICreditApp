package com.loan.icreditapp.ui.profile.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loan.icreditapp.R;

public class EditTextContainer extends FrameLayout {

    private TextInputEditText editText;
    private ImageView ivClear;
    private String hint;
    private TextInputLayout layout;

    public EditTextContainer(Context context) {
        super(context);
        initializeView(context, null);
    }

    public EditTextContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context, attrs);
    }

    public EditTextContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context, attrs);
    }

    private void initializeView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.select_view);
            if (typedArray != null) {
                hint = typedArray.getString(R.styleable.select_view_select_view_hint);
            }
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_text, this, false);
        layout = view.findViewById(R.id.edit_view_input_layout);
        editText = view.findViewById(R.id.edit_view_edit_text);
        ivClear = view.findViewById(R.id.edit_view_clear);

        layout.setHint(hint);

        ivClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    editText.setText("");
                }
            }
        });
        editText.addTextChangedListener(mTextWatcher);
        addView(view);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s) && s.length() > 0) {
                if (ivClear != null) {
                    ivClear.setVisibility(View.VISIBLE);
                }
            } else {
                if (ivClear != null) {
                    ivClear.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
        post(new Runnable() {
            @Override
            public void run() {
                if (editText != null){
                    editText.setText(editTextStr);
                    editText.setSelection(editTextStr.length());
                }
            }
        });
    }
    public void setSelectionLast(){
        if (editText != null){
            String editTextStr = editText.getText().toString();
            if (!TextUtils.isEmpty(editTextStr)) {
                editText.setText(editTextStr);
                editText.setSelection(editTextStr.length());
            }
        }
    }

    public void setPassWordMode(boolean isPasswordMode){
        if (editText != null){
            editText.setTransformationMethod(isPasswordMode ? PasswordTransformationMethod.getInstance()
                    : HideReturnsTransformationMethod.getInstance());
        }
    }

    public void setInputNum(){
        if (editText != null){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (editText != null) {
            editText.addTextChangedListener(mTextWatcher);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (editText != null) {
            editText.removeTextChangedListener(mTextWatcher);
        }
    }
}
