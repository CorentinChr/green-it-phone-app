package com.uphf.sae6_app.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.uphf.sae6_app.R;
import com.uphf.sae6_app.ui.fragments.UsernameDialogFragment;
import com.uphf.sae6_app.data.local.UserPrefs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Si aucun nom d'utilisateur n'est enregistré, demander le prénom dès le premier lancement
        if (!UserPrefs.hasUserName(this)) {
            UsernameDialogFragment dlg = new UsernameDialogFragment();
            dlg.setCancelable(false);
            dlg.show(getSupportFragmentManager(), "username_dialog");
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}