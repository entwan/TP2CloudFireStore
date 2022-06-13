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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    /** Les var globales **/
    private static final String TAG = "MainActivity";

    private static final String KEY_TITRE = "titre";
    private static final String KEY_NOTE = "note";

    // Attributs globaux
    private EditText etTitre2, etNote2;
    private TextView tvSaveNote2, tvShowNote2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference noteRef = db.document("listeDeNotes/Ma première note");

    public void initUI() {
        etTitre2 = findViewById(R.id.etTitre2);
        etNote2 = findViewById(R.id.etNote2);
        tvSaveNote2 = findViewById(R.id.tvSaveNote2);
        tvShowNote2 = findViewById(R.id.tvShowNote2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initUI();

    }

    @Override
    protected void onStart() {
        super.onStart();

        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity2.this, "Erreur au chargement !", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onEvent: " + error.toString());
                    return;
                }
                if(value.exists()) {
//                    String titre = value.getString(KEY_TITRE);
//                    String note = value.getString(KEY_NOTE);
                    Note contenuNote = value.toObject(Note.class);
                    String titre = contenuNote.getTitre();
                    String note = contenuNote.getNote();

                    tvShowNote2.setText("Titre de la note : " + titre + "\n" + "Note : " + note);
                }
                else {
                    tvShowNote2.setText("");
                }
            }
        });
    }

    public void saveNote(View view) {
        String titre = etTitre2.getText().toString();
        String note = etNote2.getText().toString();

//        Map<String, Object> contenuNote = new HashMap<>();
//        contenuNote.put(KEY_TITRE, titre);
//        contenuNote.put(KEY_NOTE, note);

        Note contenuNote = new Note(titre, note);

        /* envoi des donnees dans FireStore  */
        noteRef.set(contenuNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "Note enregistrée", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onSuccess: Note enregistrée");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur lors de l'envoi!", Toast.LENGTH_SHORT).show();
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

//                            String titre = documentSnapshot.getString(KEY_TITRE);
//                            String note = documentSnapshot.getString(KEY_NOTE);
                            Note contenuNote = documentSnapshot.toObject(Note.class);
                            String titre = contenuNote.getTitre();
                            String note = contenuNote.getNote();

                            tvSaveNote2.setText("Titre de la note : " + titre + "\n" + "Note: " + note );

                        } else {
                            Toast.makeText(MainActivity2.this, "Le document n'existe pas !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur de lecture !", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    public void updateNote(View view) {
        String textNote = etNote2.getText().toString();
        noteRef.update(KEY_NOTE,textNote);
    }

    public void deleteNote(View view) {
        noteRef.update(KEY_NOTE, FieldValue.delete())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "La note est bien supprimée !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur lors de la suppression !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    public void deleteAll(View view) {
        noteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity2.this, "Toute la note est bien supprimée (avec le titre) !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, "Erreur lors de la suppression !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

}