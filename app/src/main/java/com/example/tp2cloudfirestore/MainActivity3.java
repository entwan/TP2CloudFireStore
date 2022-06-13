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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity3 extends AppCompatActivity {

    //var globales

    private static final String TAG = "MainActivity3";

    private EditText etTitre3, etNote3;
    private TextView tvShowNote3;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Ajout de la reference à la collection comprenant toutes les notes : notebook **/
    private CollectionReference notebookRef = db.collection("Notebook");

    public void initUI() {
        etTitre3 = findViewById(R.id.etTitre3);
        etNote3 = findViewById(R.id.etNote3);
        tvShowNote3 = findViewById(R.id.tvShowNote3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initUI();
    }

    // la methode pour ajouter des notes dans la base
    public void addNote(View view) {
        String titre = etTitre3.getText().toString();
        String note = etNote3.getText().toString();

        Note contenuNote = new Note(titre,note);

        notebookRef.add(contenuNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity3.this, "Enregistrement de " + titre, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity3.this, "Erreur lors de l'ajout !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

    }

    public void loadNotes(View view) {
        // Récuperation de l'ensemble des documents de notre collection
        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String notes = ""; //declaration d'un empty string pour remplir
                        //Le textView plutot que de declarer
                        //une liste et de remplir un recycler
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note contenuNote = documentSnapshot.toObject(Note.class);
                            //recuperation de l'Id
                            contenuNote.setDocumentId(documentSnapshot.getId());

                            String documentId = contenuNote.getDocumentId();
                            String titre = contenuNote.getTitre();
                            String note = contenuNote.getNote();

                            notes += documentId + "\nTitre : " + titre + "\nNote : " + note + "\n\n";
                        }
                        tvShowNote3.setText(notes);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //ne pas oublier d'ajouter this pour détacher le listener quand nous n'en avons plus besoin
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                String notes = "";
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Note contenuNote = documentSnapshot.toObject(Note.class);
                    //recuperation de l'Id
                    contenuNote.setDocumentId(documentSnapshot.getId());

                    String documentId = contenuNote.getDocumentId();
                    String titre = contenuNote.getTitre();
                    String note = contenuNote.getNote();

                    notes += documentId + "\nTitre : " + titre + "\nNote : " + note + "\n\n";
                }
                tvShowNote3.setText(notes);
            }
        });

    }
}