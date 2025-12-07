package com.barbershopsim.app;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.barbershopsim.app.databinding.FragmentMainBinding;

import java.util.List;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;

    private MainViewModel mViewModel;

    private List<TextView> waitingCustomerViews, barberViews, barberCustomerViews;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        waitingCustomerViews =
                List.of(binding.waitingCustomerOne.getRoot(), binding.waitingCustomerTwo.getRoot(), binding.waitingCustomerThree.getRoot(), binding.waitingCustomerFour.getRoot());
        barberViews =
                List.of(binding.barberOne.getRoot(), binding.barberTwo.getRoot(), binding.barberThree.getRoot(), binding.barberFour.getRoot());
        barberCustomerViews =
                List.of(binding.barberCustomerOne.getRoot(), binding.barberCustomerTwo.getRoot(), binding.barberCustomerThree.getRoot(), binding.barberCustomerFour.getRoot());

        binding.shopName.setText(mViewModel.getShopName());
        binding.timescaleFactor.setText(mViewModel.getTimescaleFactor());

        mViewModel.isShopOpen.observe(getViewLifecycleOwner(), isShopOpen -> {
            if (isShopOpen == true) {
                binding.closedSign.setVisibility(View.INVISIBLE);
                binding.openSign.setVisibility(View.VISIBLE);
            } else {
                binding.closedSign.setVisibility(View.VISIBLE);
                binding.openSign.setVisibility(View.INVISIBLE);
            }
        });
        mViewModel.customerEnter.observe(getViewLifecycleOwner(), customer -> {
            if (customer == null) {
                binding.incomingCustomer.getRoot().setText("");
            } else {
                binding.incomingCustomer.getRoot().setText(String.format("%02d", customer));
            }
        });
        mViewModel.customerExit.observe(getViewLifecycleOwner(), customer -> {
            if (customer == null) {
                binding.outgoingCustomer.getRoot().setText("");
            } else {
                binding.outgoingCustomer.getRoot().setText(String.format("%02d", customer));
            }
        });
        mViewModel.waitingArea.observe(getViewLifecycleOwner(), this::updateWaitingArea);
        mViewModel.haircutArea.observe(getViewLifecycleOwner(), this::updateBarberChairs);
        mViewModel.clock.observe(getViewLifecycleOwner(), time -> binding.clock.setText(time));

        binding.restartButton.setOnClickListener(_ -> mViewModel.start());
        binding.playPauseButton.setOnClickListener(_ -> {
            // TODO Pause
            mViewModel.start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        waitingCustomerViews = barberViews = barberCustomerViews = null;
    }

    private void updateWaitingArea(List<Integer> customers) {
        for (var waitingCustomerView : waitingCustomerViews) {
            waitingCustomerView.setText("");
        }
        for (int i = 0; i < customers.size(); i++) {
            int customer = customers.get(i);
            waitingCustomerViews.get(i).setText(String.format("%02d", customer));
        }
    }

    private void updateBarberChairs(List<Pair<String, Integer>> chairs) {
        for (var barberView : barberViews) {
            barberView.setText("");
        }
        for (var barberCustomerView : barberCustomerViews) {
            barberCustomerView.setText("");
        }
        for (int i = 0; i < chairs.size(); i++) {
            Pair<String, Integer> chair = chairs.get(i);
            if (chair.first != null) {
                barberViews.get(i).setText(chair.first);
            }
            if (chair.second != -1) {
                barberCustomerViews.get(i).setText(String.format("%02d", chair.second));
            }
        }
    }
}