package com.omerilhanli.tmdbmoviekotlin.DI.component;

import com.omerilhanli.tmdbmoviekotlin.DI.module.ApiModule;
import com.omerilhanli.tmdbmoviekotlin.UI.BaseActivity;
import com.omerilhanli.tmdbmoviekotlin.UI.BaseFragment;
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ApiModule.class)
public interface AppComponent {

    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    TmdbApi tmdbApi();
}
