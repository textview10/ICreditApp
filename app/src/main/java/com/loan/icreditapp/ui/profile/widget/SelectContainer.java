package com.loan.icreditapp.ui.profile.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loan.icreditapp.R;

public class SelectContainer extends FrameLayout {

    private TextView tvTitle, tvDesc;
    private ImageView ivIcon;
    private String hint;
    private @DrawableRes
    int drawableRes;

    public SelectContainer(@NonNull Context context) {
        super(context);
        initializeView(context, null);
    }

    public SelectContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context, attrs);
    }

    public SelectContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context, attrs);
    }

    private void initializeView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.select_view);
            if (typedArray != null) {
                hint = typedArray.getString(R.styleable.select_view_select_view_hint);
                drawableRes = typedArray.getResourceId(R.styleable.select_view_select_view_icon, R.drawable.ic_edit_text_clear);
            }
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_select, this, false);
        tvTitle = view.findViewById(R.id.view_select_tv_title);
        tvDesc = view.findViewById(R.id.view_select_tv_desc);
        ivIcon = view.findViewById(R.id.view_select_iv_icon);

        tvTitle.setText(hint);
        if (drawableRes != 0) {
            ivIcon.setImageResource(drawableRes);
        }
        addView(view);
    }

    public void setData(String data){
        tvDesc.setText(data);
    }

    public String getData(){
        return tvDesc == null ? "" : tvDesc.getText().toString();
    }

    public boolean isEmptyText(){
        String data = getData();
        return TextUtils.isEmpty(data);
    }
}
