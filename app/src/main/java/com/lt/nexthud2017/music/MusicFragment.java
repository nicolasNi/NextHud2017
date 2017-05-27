package com.lt.nexthud2017.music;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.lt.nexthud2017.R;
import com.lt.nexthud2017.databinding.MusicFragmentBinding;

/**
 * Created by Administrator on 2017/5/3.
 */

public class MusicFragment extends Fragment{
    private View musicView;
    private MusicViewModel musicViewModel;
    private MusicFragmentBinding musicFragmentBinding;
    private EditText edtKey;
    private ListView musicListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createBinding(inflater, container);
        edtKey = musicFragmentBinding.edtSearch;
        edtKey.setCursorVisible(false);
        musicListView = musicFragmentBinding.lvSearchList;
        if(musicViewModel != null){
            musicFragmentBinding.setViewModel(musicViewModel);
        }
        return musicFragmentBinding.getRoot();
    }

    private void createBinding(LayoutInflater inflater, ViewGroup container){
        musicFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.music_fragment,container,false);

    }


    public void setMusicViewModel(MusicViewModel viewModel){
        musicViewModel = viewModel;
        if(musicFragmentBinding != null){
        musicFragmentBinding.setViewModel(musicViewModel);}
    }

    public void changeAction(String param) {
        if (param.contains("up")) {
            musicViewModel.moveToPreviousSong();

        } else if (param.contains("down")) {
            musicViewModel.moveToNextSong();
        } else if (param.contains("center")) {
            musicViewModel.pressCenter();
        } else if (param.contains("right")) {
        }
    }

    public void undisplayCursor() {
        if (edtKey != null) {
            edtKey.setFocusable(false);
            edtKey.setCursorVisible(false);
            edtKey.setHintTextColor(Color.WHITE);
            edtKey.setTextColor(Color.WHITE);
        }
    }

    public void displayCursor() {
        if (edtKey != null) {
            edtKey.setFocusable(true);
            edtKey.setHintTextColor(Color.rgb(23, 169, 200));
            edtKey.setTextColor(Color.rgb(23, 169, 200));
        }
    }

    public void scrollListView(int position){
        musicListView.smoothScrollToPosition(position);
    }

}
