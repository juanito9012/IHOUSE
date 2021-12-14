package com.thefactory.ihouse.ui.opciones;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thefactory.ihouse.databinding.FragmentOpcionesBinding;

public class OpcionesFragment extends Fragment {

    private OpcionesViewModel opcionesViewModel;
    private FragmentOpcionesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        opcionesViewModel =
                new ViewModelProvider(this).get(OpcionesViewModel.class);

        binding = FragmentOpcionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textOpciones;
        opcionesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}