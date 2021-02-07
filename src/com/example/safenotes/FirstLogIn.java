package com.example.safenotes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstLogIn extends Activity {
	
	private Button bValider; // Bouton valider.
	private EditText user,pass,rPass; /* Champs d'entrée ( Nom d'utilisateur, 
									   * mot de passe et répéter mot de passe).*/
	
	private AESEncryptAdapter AESAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstlogin);
		
		AESAdapter = new AESEncryptAdapter();
		
		user=(EditText)findViewById(R.id.editText1);
		pass=(EditText)findViewById(R.id.editText2);
		rPass=(EditText)findViewById(R.id.editText3);
		
		bValider=(Button)findViewById(R.id.button1);
		bValider.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
					
            	if (!(user.getText().toString().equals(""))){
            			if (pass.getText().toString().equals(rPass.getText().toString())==true){
            				if (pass.getText().toString().length()>=8){
            					if (passIsValid(pass.getText().toString())){
            						try {
            								/* Si les conditions de nom d'utilisateur et 
            								 * de mot de passe sont respectées alors...*/
            							
										saveToFile("string.xml", SHA1(SHA1(user.getText().toString())
													+SHA1(pass.getText().toString())));
													/* Hasher le hash de nom d'utilisateur + 
													 * le mot de passe puis les enregistrer.*/
										
										AESAdapter.key(SHA1(user.getText().toString())
													+SHA1(pass.getText().toString()));
													// Définir le hash comme clé de chiffrement/déchiffrement.
										
									} catch (NoSuchAlgorithmException e) {
										e.printStackTrace();
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}	
            						
            						startActivity(new Intent(getApplicationContext(),NoteList.class));
            								  // Démarrer l'activité NoteList.
            						finish(); // Terminer cette activité.
            						
            					}
            					else
            						Toast.makeText(FirstLogIn.this, "Le mot de passe doit comporter " +
            								"au moins 1 chiffre, 1 miniscule, 1 majuscule et 1 symbole!",
            									Toast.LENGTH_SHORT).show();
            				}
            				else
            					Toast.makeText(FirstLogIn.this, "Le mot de passe doit comporter " +
            							"au minimum 8 caractères!", Toast.LENGTH_SHORT).show();
            			}
            			else
            				Toast.makeText(FirstLogIn.this, "Les deux mots de passe saisies " +
            						"ne correspondent pas.", Toast.LENGTH_SHORT).show();
            		}
            		else 
            			Toast.makeText(FirstLogIn.this, "Vous n'avez pas spécifié " +
            					"le nom d'utilisateur!", Toast.LENGTH_SHORT).show();
            }
            });
		
	}
	
	public boolean passIsValid(String s){
				// Vérifier ce que le mot de passe doit comporter.
		boolean a = false,b = false,c = false,d = false;
		char x;
		for(int i=0; i<s.length() ; i++){
			x=s.charAt(i);
			if(Character.isDigit(x))
				a=true;
			else if	(Character.isLowerCase(x))
				b=true;
			else if (Character.isUpperCase(x))
				c=true;
			else    
				d=true;	
		}
		if (a&&b&&c&&d)
			return true;
		else
			return false;
	}
	
	private static String convertToHex(byte[] data) {
				// Convertir l'ensemble d'octets en hexadécimal.
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) 
                			: (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String toHash) throws NoSuchAlgorithmException,
    	UnsupportedEncodingException {
    			/* Hasher une chaine de caractères en utilisant la fonction 
    			 * de hashage SHA-1 et retourner-la en hexadécimal.*/
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(toHash.getBytes("iso-8859-1"), 0, toHash.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    
	public void saveToFile(String fileName, String text){
				// Sauvegarder le texte dans un fichier spécifié.
		try { 
			FileOutputStream out = openFileOutput(fileName,MODE_PRIVATE);
				out.write(text.getBytes());
				out.close();				
			} catch (IOException e) {
				Log.e("Exception", "Echec de création de fichier: " + e.toString());
			}
		
	}
}