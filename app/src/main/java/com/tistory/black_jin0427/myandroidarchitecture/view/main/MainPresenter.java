package com.tistory.black_jin0427.myandroidarchitecture.view.main;

import android.content.Context;
import android.database.ContentObserver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tistory.black_jin0427.myandroidarchitecture.api.GithubApi;
import com.tistory.black_jin0427.myandroidarchitecture.api.GithubApiProvider;
import com.tistory.black_jin0427.myandroidarchitecture.api.JSONUtil;
import com.tistory.black_jin0427.myandroidarchitecture.api.model.User;
import com.tistory.black_jin0427.myandroidarchitecture.constant.Constant;
import com.tistory.black_jin0427.myandroidarchitecture.room.UserDao;
import com.tistory.black_jin0427.myandroidarchitecture.room.UserDatabaseProvider;
import com.tistory.black_jin0427.myandroidarchitecture.rxEventBus.RxEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainPresenter implements MainContract.Presenter {

    GithubApi api;
    MainContract.View view;
    Context context;

    private CompositeDisposable disposable;

    MainPresenter() {
        this.api = GithubApiProvider.provideGithubApi();
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }


    @Override
    public void loadData(Context context) {
        this.context = context;
        //JSONUtil generatePoi = new JSONUtil(this.context);
        //JSONArray array = generatePoi.jsonParsing(generatePoi.getJsonFromStorage());
        Gson gson = new Gson();
        ArrayList<User> userList = gson.fromJson(getJsonString(), new TypeToken<List<User>>(){}.getType());
        view.setItems(userList);
    }

    @Override
    public void loadData() {
        disposable.add(api.getUserList(Constant.RANDOM_USER_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(__ -> {
                    view.showProgress();
                })
                .doOnTerminate(() -> {
                    view.hideProgress();
                })
                .subscribe(userResponse -> {
                    view.setItems((ArrayList<User>)userResponse.userList);
                }, error -> {
                    Log.e("MyTag",error.getMessage());
                })
        );
    }


    public String getJsonString() {
        String json = "";

        try {
            InputStream is = context.getAssets().open("data.json");
            int fileSize = is.available();
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d("MainPresernter", "getJsonString Success from poi_object_sample_data.json in assets (RobotPlatformLibrary)");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Log.d("MainPresernter", "getJsonString : " + json);

        JSONObject jsonObject = null;
        JSONArray newArray = null;
        try {
            jsonObject = new JSONObject(json);
            newArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newArray.toString();
    }

    @Override
    public void addUser(UserDao userDao, User user) {

        disposable.add(
                Observable.just(user)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        item -> {
                            Log.d("MyTag","item : " + item + " 저장");
                            userDao.add(item);
                        },
                        error -> {
                            Log.d("MyTag","저장 onError");
                        },
                        () -> {
                            Log.d("MyTag","저장 onCompleted");
                        }
                )
        );

    }

    @Override
    public void setRxEvent() {

        disposable.add(
                RxEvent.getInstance()
                        .getObservable()
                        .subscribe(
                                object -> {
                                    if(object instanceof User) {
                                        view.updateView((User) object);
                                    }
                                },
                                error -> {
                                    Log.d("MyTag","onError");
                                },
                                () -> {
                                    Log.d("MyTag","onCompleted");
                                }
                        )
        );
    }
}
