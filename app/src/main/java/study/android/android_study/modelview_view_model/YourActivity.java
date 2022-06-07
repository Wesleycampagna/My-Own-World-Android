package study.android.android_study.modelview_view_model;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import study.android.android_study.databinding.ActivityYourBinding;
import study.android.android_study.modelview_view_model.ui.camera.YourFragment;

public class YourActivity extends AppCompatActivity {

    private ActivityYourBinding binding;
    private static final String SEU_FRAGMENT = "SEU_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityYourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // flagNumber é um atributo aleatório que criei, poderia ser qualquer informação desejada
        // para transferência para dentro do fragment
        int flagNumber = 1;

        // this part will load content of Fragment
        // se for o segundo fragmento não é necessãrio checar savedInstanceState
        if (savedInstanceState == null) {
            // crie o fragmento
            YourFragment fragment = YourFragment.newInstance(flagNumber);

            // chame o fragmento e dê commit para ser adicionado a esta Activicty
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.container.getId(), fragment)
                    .addToBackStack(SEU_FRAGMENT)
                    .commit();
        }
    }

    public void someMethodPublicOfActivity() {
        // can be accessed by fragment
    }
}