package study.android.android_study.modelview_view_model.ui.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import study.android.android_study.R;
import study.android.android_study.databinding.FragmentYourBinding;
import study.android.android_study.modelview_view_model.YourActivity;

public class YourFragment extends Fragment {

    private FragmentYourBinding binding;

    // static final para ser um valor imutável do Bundle
    private static final String FRAG_NUMBER = "FRAG_NUMBER";
    private int fragNumber;

    // no método abaixo foi criado uma instancia dessa classe e aqui ele transfere de forma
    // segura os dados do bundle para os atributos da classe
    public YourFragment() {
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
    public static YourFragment newInstance(/* params */ int fragNumber) {
        Bundle args = new Bundle();
        args.putInt(FRAG_NUMBER, fragNumber);
        YourFragment yourFragment = new YourFragment();
        yourFragment.setArguments(args);
        return yourFragment;
    }

    private static final String SEU_FRAGMENT_2 = "SEU_FRAGMENT_2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentYourBinding.inflate(inflater, container, false);

        // coloque o código do fragmento nesta parte antes de returnar o inflate
        // clicks, mudanças de layout e outros

        // requireContext() te entrega o contexto da activity
        // requireActivity() te entrega a Activity
        this.requireContext();
        this.requireActivity();

        // tem como usar métodos da acivity que sustenta o fragmento
        // usando (([nome_acivity]) this.requireActivity()).<method>()
        // ex.:
        YourActivity yourActivity = (YourActivity) this.requireActivity();
        yourActivity.someMethodPublicOfActivity();

        binding.goToSecond.setOnClickListener(v -> {
            int flagNumber = 2;

            // ir para outro fragment add stack de back
            // crie o fragmento
            YourFragmentTwo fragment = YourFragmentTwo.newInstance(flagNumber);

            // chame o fragmento e dê commit para ser adicionado a esta Activicty
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(SEU_FRAGMENT_2)
                    .commit();
        });

        return binding.getRoot();
    }
}