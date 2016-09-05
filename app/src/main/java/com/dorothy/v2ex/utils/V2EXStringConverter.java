package com.dorothy.v2ex.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by dorothy on 16/8/29.
 */
public class V2EXStringConverter extends Converter.Factory {

    public static V2EXStringConverter create() {
        return new V2EXStringConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {


        return new Converter<ResponseBody, String>() {
            @Override
            public String convert(ResponseBody value) throws IOException {
                return value.string();
            }
        };
    }
}


