package study.android.android_study.modelview_view_model.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import study.android.android_study.R;
import study.android.android_study.databinding.FragmentYourTwoBinding;
import study.android.android_study.modelview_view_model.MainActivity;

public class YourFragmentTwo extends Fragment {

    private FragmentYourTwoBinding binding;

    // static final para ser um valor imutável do Bundle
    private static final String FRAG_NUMBER = "FRAG_NUMBER";
    private int fragNumber;

    // no método abaixo foi criado uma instancia dessa classe e aqui ele transfere de forma
    // segura os dados do bundle para os atributos da classe
    public YourFragmentTwo() {
        Bundle args = this.getArguments();
        if (args != null) {
            //        this.params = args.get...(...)   caso tenha outros params no Bundle
            this.fragNumber = args.getInt(FRAG_NUMBER);
        }
    }

    // quem for chamar cria a instancia do fragmento em tempo de compilação pelo newInstance,
    // note que newInstance não foi SeuFragment que invocou mas sim SuaActivity
    // esse método então retorna o objeto construido para ser add a stack de tela se ser invocado
    // quando a stack tiver pronta e for invocado chamara onCreateView
    public static YourFragmentTwo newInstance(/* params */ int fragNumber) {
        Bundle args = new Bundle();
        args.putInt(FRAG_NUMBER, fragNumber);
        YourFragmentTwo seuFragment = new YourFragmentTwo();
        seuFragment.setArguments(args);
        return seuFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentYourTwoBinding.inflate(inflater, container, false);

        binding.goToMain.setOnClickListener(v -> {
            // ir para a main Acivity
            startActivity(new Intent(requireActivity(), MainActivity.class));
        });
        return binding.getRoot();
    }
}