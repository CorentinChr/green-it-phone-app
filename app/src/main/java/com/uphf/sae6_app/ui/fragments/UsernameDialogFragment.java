package com.uphf.sae6_app.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.uphf.sae6_app.data.local.UserPrefs;

/**
 * DialogFragment demandant le nom de l'utilisateur au premier lancement.
 */
public class UsernameDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

        // Wrap EditText to add padding on some devices
        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) (16 * requireContext().getResources().getDisplayMetrics().density);
        params.leftMargin = margin;
        params.rightMargin = margin;
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Bienvenue — Entrez votre prénom");
                builder.setView(container)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Overridden below to avoid auto-dismiss on empty input
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = input.getText() == null ? "" : input.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Sauvegarde
                UserPrefs.setUserName(requireContext(), name);
                // Recréer l'Activity pour forcer la mise à jour de l'UI
                if (getActivity() != null) getActivity().recreate();
                // Fermer le dialog
                dismiss();
            });
        });

        // Empêche annulation via back button
        setCancelable(false);

        return dialog;
    }

    // Dialog non annulable (setCancelable(false)) — pas d'action onCancel nécessaire
}


