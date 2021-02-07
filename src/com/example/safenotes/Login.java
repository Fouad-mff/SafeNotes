package com.example.safenotes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	
	private Button bIdentifier; // Bouton identifier.
	private EditText user, pass; // Champs d'entrée ( Nom d'utilisateur + Mot de passe).
	private CheckBox showPasswordCheckBox; // La case à cocher "afficher le mot de passe".
	private AESEncryptAdapter AESAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		AESAdapter = new AESEncryptAdapter();
					// Instanciation AESEncryptAdapter.
		
		user=(EditText)findViewById(R.id.editText1);
		pass=(EditText)findViewById(R.id.editText2);
		
		showPasswordCheckBox = (CheckBox)findViewById(R.id.checkBox1);
		showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
		    	
		        if (showPasswordCheckBox.isChecked()){
		            pass.setTransformationMethod(null);
		            		// Si la case est cochée alors rendre le mot de passe en état affichable.
		        }else{
		            pass.setInputType(129);
		            		// Sinon le mot de passe est caché de nouveau.
		        }
		    }
		});
		
		bIdentifier=(Button)findViewById(R.id.button1);
		bIdentifier.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            	
            	try {
            		if(FirstLogIn.SHA1(FirstLogIn.SHA1(user.getText().toString())
            				+FirstLogIn.SHA1(pass.getText().toString()))
            				.equals(readFromFile("string.xml"))==true){
            						// Vérification de nom d'utilisateur et de mot de passe.
            			
            			AESAdapter.key(FirstLogIn.SHA1(user.getText().toString())
            					+FirstLogIn.SHA1(pass.getText().toString()));
            						// Le hash est la clé de chiffrement/déchiffrement.
            			
            			startActivity(new Intent(getApplicationContext(),NoteList.class));
            						// Démarrer l'activité NoteList.
            			finish();	// Terminer cette activité.
            		}
            		else 
            			Toast.makeText(Login.this, "Le nom d'utilisateur ou le mot de passe est incorrect!"
            					, Toast.LENGTH_SHORT).show();
            						// Notification en cas de nom d'utilisateur/mot de passe érroné.
            		
           		} catch (NoSuchAlgorithmException e) {
           				// Capter cette exception si elle existe.
            		e.printStackTrace();
            	} catch (UnsupportedEncodingException e) {
            			// Capter cette exception si elle existe.
            		e.printStackTrace();
				}
            	
            
            }
            });
		
	}
	
	public String readFromFile(String fileName) {
			// Lecture de texte à partir d'un fichier.
		String ret = "", b = null;
		
		try {
			
	        InputStream inputStream = openFileInput(fileName);
	        			// Flux d'entrée.
	        
	        if ( inputStream != null ) {
	        	
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            
	            while ((b = bufferedReader.readLine()) != null){
	            		// Tant qu'il ya une nouvelle ligne non nulle.
	            	ret += b;
	            }
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("Login activity", "Fichier non trouvé: " + e.toString());
	    } 
	    catch (IOException e) {
	        Log.e("Login activity", "Echec de lecture de fichier: " + e.toString());
	    }

	    return ret;
	}
}