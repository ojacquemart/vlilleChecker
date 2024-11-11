package com.vlille.checker.dataset.retrofit;

import com.vlille.checker.dataset.retrofit.model.ResultSet;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VlilleService {

    @GET("items")
    Call<ResultSet> getStations(
            @Query("f") String format,
            @Query("limit") int limit,
            @Query(value = "filter", encoded = true) String filter,
            @Query(value = "filter-lang") String filterLang
    );

    @GET("items")
    Call<ResultSet> getStation(
            @Query("f") String format,
            @Query("limit") int limit,
            @Query(value = "filter", encoded = true) String filter,
            @Query(value = "filter-lang") String filterLang
    );

    enum Factory {

        INSTANCE;

        public static final String API_URL = "https://data.lillemetropole.fr/data/ogcapi/collections/vlille_temps_reel/";

        private VlilleService service;

        Factory() {
            OkHttpClient client = getUnsafeOkHttpClient();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            this.service = retrofit.create(VlilleService.class);
        }

        public VlilleService getService() {
            return service;
        }

        private static OkHttpClient getUnsafeOkHttpClient() {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

                builder.addInterceptor(interceptor);

                return builder.build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

}