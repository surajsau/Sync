package com.halfplatepoha.frnds.network.servicegenerators;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by surajkumarsau on 24/08/16.
 */
public class ClientGenerator {

    public static class Builder {

        private Class mClientClass;
        private OkHttpClient.Builder httpClient;
        private Retrofit.Builder builder;

        public Builder() {
            httpClient = new OkHttpClient.Builder();
            httpClient.followRedirects(true);
            builder = new Retrofit.Builder()
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }

        public Builder setBaseUrl(String baseUrl) {
            builder.baseUrl(baseUrl);
            return this;
        }

        public Builder addRetryInterceptor() {
            Interceptor retryInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    if(response.code() == 307) {
                        String redirectUrl = response.header("location");
                        Request newRequest = chain.request();
                        newRequest = newRequest.newBuilder()
                                .url(redirectUrl)
                                .build();
                        return chain.proceed(newRequest);
                    }
                    return response;
                }
            };

            httpClient.addInterceptor(retryInterceptor);
            return this;
        }

        public Builder setConnectTimeout(int time, TimeUnit timeUnit) {
            httpClient.connectTimeout(time, timeUnit);
            return this;
        }

        public Builder setReadTimeout(int time, TimeUnit timeUnit) {
            httpClient.connectTimeout(time, timeUnit);
            return this;
        }

        public Builder setHeader(final String headerKey, final String headerValue) {
            Interceptor apiKeyInterceptor = new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request request = chain.request();
                    request = request.newBuilder().header(headerKey, headerValue).build();
                    return chain.proceed(request);
                }
            };

            httpClient.addInterceptor(apiKeyInterceptor);
            return this;
        }

        public Builder setApiKeyInterceptor(final String apiKeyParam, final String apiKeyValue) {
            Interceptor apiKeyInterceptor = new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder().addQueryParameter(apiKeyParam, apiKeyValue).build();
                    request = request.newBuilder().url(url).build();
                    return chain.proceed(request);
                }
            };

            httpClient.addInterceptor(apiKeyInterceptor);
            return this;
        }

        public Builder setLoggingInterceptor() {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.addInterceptor(loggingInterceptor);
            return this;
        }

        public Builder setClientClass(Class clientClass) {
            mClientClass = clientClass;
            return this;
        }

        public<T> T buildClient() {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return (T)retrofit.create(mClientClass);
        }
    }
}
