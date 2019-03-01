package com.steve.retrofit1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static int REQUEST_TIMEOUT = 60;
    String baseUrl = "https://api.github.com/";
    ListView listView;
    private CompositeDisposable disposable = new CompositeDisposable();
    Retrofit retrofit;
    GitHubClient gitHubClient;
    EntryListAdapter adapter;
    OkHttpClient okHttpClient;
    private ArrayList<GitHubEntry> gitHubEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.page_list);
        //showPublicGithutRepo();

        adapter = new EntryListAdapter(this, gitHubEntries);
        listView.setAdapter(adapter);
        gitHubClient = getRetrofit().create(GitHubClient.class);
        ConnectableObservable<List<GitHubEntry>> entriesObservable = getEntryObservable().replay();

        disposable.add(entriesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<GitHubEntry>>() {
                    @Override
                    public void onNext(List<GitHubEntry> entries) {
                        gitHubEntries.clear();
                        printEntries(entries);
                        gitHubEntries.addAll(entries);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                }));

        entriesObservable.connect();
    }

    private void printEntries(List<GitHubEntry> entries) {
        for (GitHubEntry entry : entries) {
            Log.e(TAG, "printEntries: id=" + entry.getId() + ", login="+entry.getLogin());
        }
    }
    public Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private  OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Request-Type", "Android")
                        .addHeader("Content-Type", "application/json");

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private Observable<List<GitHubEntry>> getEntryObservable() {
        return gitHubClient
                .getGTUsers()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // original retrofit api call
    private void showPublicGithutRepo() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = retrofitBuilder.build();

        // the interface class GitHubClient.class.
        GitHubClient client = retrofit.create(GitHubClient.class);
        Call<List<GitHubEntry>> myservice = client.getGitHubUsers();

        myservice.enqueue(new Callback<List<GitHubEntry>>() {
            @Override
            public void onResponse(Call<List<GitHubEntry>> call, retrofit2.Response<List<GitHubEntry>> response) {
                List<GitHubEntry> entries = response.body();
                listView.setAdapter(new EntryListAdapter(MainActivity.this, entries));
            }

            @Override
            public void onFailure(Call<List<GitHubEntry>> call, Throwable t) {
                Toast.makeText(getBaseContext(), "error :-(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
