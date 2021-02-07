package com.example.safenotes;

import java.io.File;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartLogo extends Activity {

private ImageView logo; // L'image repr�sentant le slogan de l'application.
private TextView touchTxt; // Le texte � afficher.
private Animation myAnimation; // L'animation de texte.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startlogo);
					// Liaison de cette activit� avec l'interface XML.
		
		touchTxt = (TextView) findViewById(R.id.textView1);
					// Liaison de texte avec la ressource XML.
		myAnimation = AnimationUtils.loadAnimation(this, R.anim.touch);
					// Liaison de l'animation avec la ressource XML.
		touchTxt.startAnimation(myAnimation);
					// D�marrer l'animation de texte.
		
		logo = (ImageView)findViewById(R.id.startlogo);
					// Liaison de l'image avec la ressource XML.
		logo.setOnClickListener(new View.OnClickListener(){
			
            @SuppressLint("SdCardPath")
			@Override
            public void onClick(View v){
            		// L'action � d�clencher suite � un clique sur l'objet (ici image).
            	
            	File f = new File("/data/data/"+StartLogo.this.getPackageName()+"/files/string.xml");
            						// Le fichier dans lequel se trouve le hash.
            	File g = new File("/data/data/"+StartLogo.this.getPackageName()+"/databases/");
            						// Le r�pertoire de la BD de l'application.
            	
            	File[] dbFiles = g.listFiles(); // Liste de Fichiers BD.
            	
            	if (f.exists()) // Si le ficher de hash existe.
            		startActivity(new Intent(getApplicationContext(),Login.class));
            						// D�marrer l'activit� Login.
            	else{            		
            		if(dbFiles != null) {
            			for(int j = 0; j < dbFiles.length; j++) {
            				dbFiles[j].delete();
            						// Suppression de BD (si le fichier de hash a �t� supprim�).
            			}
           			}            		
           			startActivity(new Intent(getApplicationContext(),FirstLogIn.class));
           							// D�marrer l'activit� FirstLogIn.
            	}
            	finish(); // Terminer cette activit�.
            }
        });		
	}
}