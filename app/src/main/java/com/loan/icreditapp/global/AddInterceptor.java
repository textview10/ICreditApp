package com.loan.icreditapp.global;

import androidx.annotation.NonNull;

import com.loan.icreditapp.util.MyAppUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        int verCode = MyAppUtils.INSTANCE.getAppVersionCode();
//        Log.e("Test", "2222222222 = " +verCode);
        Request newRequest = request.newBuilder()
                .addHeader("innerVersionCode", verCode + "")
                .build();
        return chain.proceed(newRequest);
    }
}
