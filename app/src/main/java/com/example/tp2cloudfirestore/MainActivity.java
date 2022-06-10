package com.example.tp2cloudfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /** Les var globales **/
    private static final String TAG = "MainActivity";

    private static final String KEY_TITRE = "titre";
    private static final String KEY_NOTE = "note";

    // Attributs globaux
    private EditText etTitre, etNote;
    private TextView tvSaveNote, tvShowNote;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference noteRef = db.document("listeDeNotes/Ma première note");

    public void initUI() {
        etTitre = findViewById(R.id.etTitre);
        etNote = findViewById(R.id.etNote);
        tvSaveNote = findViewById(R.id.tvSaveNote);
        tvShowNote = findViewById(R.id.tvShowNote);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    @Override
    protected void onStart() {
        super.onStart();

        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "Erreur au chargement !", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onEvent: " + error.toString());
                    return;
                }
                if(value.exists()) {
                    String titre = value.getString(KEY_TITRE);
                    String note = value.getString(KEY_NOTE);
                    tvShowNote.setText("Titre de la note : " + titre + "\n" + "Note : " + note);
                }
                else {
                    tvShowNote.setText("");
                }
            }
        });
    }

    public void saveNote(View view) {
        String titre = etTitre.getText().toString();
        String note = etNote.getText().toString();

        Map<String, Object> contenuNote = new HashMap<>();
        contenuNote.put(KEY_TITRE, titre);
        contenuNote.put(KEY_NOTE, note);

        /* envoi des donnees dans FireStore  */
        noteRef.set(contenuNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Note enregistrée", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onSuccess: Note enregistrée");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur lors de l'envoi!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    public void showNote(View view) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            String titre = documentSnapshot.getString(KEY_TITRE);
                            String note = documentSnapshot.getString(KEY_NOTE);
                            tvSaveNote.setText("Titre de la note : " + titre + "\n" + "Note: " + note );

                        } else {
                            Toast.makeText(MainActivity.this, "Le document n'existe pas !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur de lecture !", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure: " + e.toString());
                    }
                });
    }
}