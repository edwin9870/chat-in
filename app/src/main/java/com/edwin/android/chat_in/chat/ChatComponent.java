package com.edwin.android.chat_in.chat;

import com.edwin.android.chat_in.mainview.MainViewActivity;
import com.edwin.android.chat_in.util.FragmentScoped;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

@FragmentScoped
@Component(modules = {ChatPresenterModule.class})
public interface ChatComponent {
    ChatPresenter getChatPresenter();
}
