package com.douncoding.readingsalon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.douncoding.readingsalon.controller.ConfigInteractor;
import com.douncoding.readingsalon.data.ConfigMenu;
import com.douncoding.readingsalon.data.Owner;

import java.util.ArrayList;
import java.util.List;

public class ConfigMenuFragment extends Fragment {
    public static final String TAG = ConfigMenuFragment.class.getSimpleName();

    RecyclerView mCommonMenuView;
    RecyclerView mPersonalMenuView;
    ConfigAdapter mCommonAdapter;
    ConfigAdapter mPersonalAdapter;

    Owner mOwner;
    ConfigInteractor mInteractor;

    public static ConfigMenuFragment newInstance() {
        ConfigMenuFragment fragment = new ConfigMenuFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOwner = new Owner(context);
        mInteractor = new ConfigInteractor(context);
        mCommonAdapter = new ConfigAdapter();
        mPersonalAdapter = new ConfigAdapter();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOwner = null;
        mInteractor = null;
        mCommonAdapter = null;
        mPersonalAdapter = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_config_menu, container, false);

        mCommonMenuView = (RecyclerView)rootView.findViewById(R.id.common_config_view);
        mCommonMenuView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommonMenuView.setAdapter(mCommonAdapter);

        List<ConfigMenu> menu1 = new ArrayList<>();
        menu1.add(new ConfigMenu(R.drawable.ic_mail_cycle, "문의/제안"));
        menu1.add(new ConfigMenu(R.drawable.ic_info_cycle, "버전정보"));
        menu1.add(new ConfigMenu(R.drawable.ic_alarm_cycle, "알람설정", ConfigMenu.TYPE_TOGGLE));
        mCommonAdapter.addMenus(menu1.toArray(new ConfigMenu[menu1.size()]));
        mCommonAdapter.setOnItemClickListener(new ConfigAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Nothing..
            }
        });
        // PUSH On/Off Change Event
        mCommonAdapter.setOnSwitchedCallbacks(new ConfigAdapter.OnSwitchedCallbacks() {
            @Override
            public void onSwitched(boolean isChecked) {
                mInteractor.changePushService(isChecked);
            }
        });

        mPersonalMenuView = (RecyclerView)rootView.findViewById(R.id.personal_config_view);
        mPersonalMenuView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPersonalMenuView.setAdapter(mPersonalAdapter);

        List<ConfigMenu> menu2 = new ArrayList<>();
        menu2.add(new ConfigMenu(R.drawable.ic_logout_cycle, "로그아웃"));
        menu2.add(new ConfigMenu(R.drawable.ic_pen_cycle, "글작성"));
        mPersonalAdapter.addMenus(menu2.toArray(new ConfigMenu[menu2.size()]));
        mPersonalAdapter.setOnItemClickListener(new ConfigAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (position) {
                    case 0:
                        mInteractor.logout();
                        Animation out = AnimationUtils.makeOutAnimation(getActivity(), true);
                        mPersonalMenuView.startAnimation(out);
                        mPersonalMenuView.setVisibility(View.GONE);
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), WriterActivity.class);
                        intent.putExtra(Constants.WRITER_PARAMS, Constants.WRITER_TYPE_WRITE);
                        startActivity(intent);
                        break;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCommonMenuView = null;
        mPersonalMenuView = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        ConfigMenu config = null;

        config = mCommonAdapter.get(R.drawable.ic_info_cycle);
        config.setExpend(mInteractor.getAppVersion());
        mCommonAdapter.update(config);

        config = mCommonAdapter.get(R.drawable.ic_alarm_cycle);
        config.setPush(mInteractor.isPushService());
        mCommonAdapter.update(config);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
