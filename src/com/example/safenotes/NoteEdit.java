package com.example.safenotes;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.safenotes.R.color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class NoteEdit extends Activity{
	
	public static String curDate = "";	// La date actelle.
    private EditText mTitleText,mBodyText; // Le titre + le texte.
    private String t,b; // pour connaitre l'état de contenu final (modifié ou pas).
    private TextView mDateText; // La date.
    boolean a = false;
    
    private Long mRowId;
    private Cursor note;
    final Context context = this;
    private NotesDbAdapter mDbHelper;
      
	@SuppressLint("SimpleDateFormat")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        
        
        setContentView(R.layout.activity_note_edit);
        setTitle(R.string.app_name); // Titre de l'activité.

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDateText = (TextView) findViewById(R.id.notelist_date);

        long msTime = System.currentTimeMillis();  
        Date curDateTime = new Date(msTime);
 	
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
        curDate = formatter.format(curDateTime);        
        
        mDateText.setText(curDate);
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                                    : null;
        }
        
        populateFields(); // Remplir les champs.  
    }
	
	  public static class LineEditText extends EditText{
		  		// Dessiner les lignes de la page dans laquelle on fait l'édition du texte.
			
		  public LineEditText(Context context, AttributeSet attrs) {
				super(context, attrs);
					mRect = new Rect();
			        mPaint = new Paint();
			        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			        mPaint.setColor(color.black);
			}

			private Rect mRect;
		    private Paint mPaint;	    
		    
		    @Override
		    protected void onDraw(Canvas canvas) {
		  
		        int height = getHeight();
		        int line_height = getLineHeight();

		        int count = height / line_height;

		        if (getLineCount() > count)
		            count = getLineCount();

		        Rect r = mRect;
		        Paint paint = mPaint;
		        int baseline = getLineBounds(0, r);

		        for (int i = 0; i < count; i++) {

		            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
		            baseline += getLineHeight();

		        super.onDraw(canvas);
		    }

		}
	  }
	  
	  	@Override
	    protected void onSaveInstanceState(Bundle outState) {
	  				// Sauvegarder l'état actuel de l'activité (id de la note ouverte).
	        super.onSaveInstanceState(outState);
	        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	    }	    
	    @Override
	    protected void onResume() {
	    			// Restaurer le dernier état avant la minimisation de l'activité.
	        super.onResume();
	        populateFields();
	    }
	    
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
					// Spécifier la resource des éléments de la liste menu.
			getMenuInflater().inflate(R.menu.main_edit, menu);
			return true;		
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
					// Spécifier les actions des éléments de la liste menu.
		   switch (item.getItemId()) {
		    
		   	case R.id.copy: // Copier le texte dans le presse-papier.
		   		
		    	ClipboardManager clipboard = (ClipboardManager) 
		    			getSystemService(CLIPBOARD_SERVICE); 
		    	ClipData clip = ClipData.newPlainText("label", mBodyText.getText().toString());
		    	clipboard.setPrimaryClip(clip);
		    	Toast.makeText(NoteEdit.this, "Texte copié dans le presse-papier!"
		    					, Toast.LENGTH_SHORT).show();
		    	
		    	return true;
		    	
		    case R.id.share: // Partager le texte avec/sans chiffrement césar.
		    	
		    	LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.activity_share, null);
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);
				
				final EditText edtcle = (EditText) promptsView.findViewById(R.id.edtcle);
				final TextView txtcle = (TextView) promptsView.findViewById(R.id.txtcle);
				
				RadioGroup rg = (RadioGroup) promptsView.findViewById(R.id.radioGroup1);
				rg.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		        {
					public void onCheckedChanged(RadioGroup rgp, int checkedId) {
		            	switch(checkedId){
		                    
		            		case R.id.radio0:
		                        txtcle.setVisibility(View.GONE);
		                        edtcle.setVisibility(View.GONE);
		                        a = false;
		                        break;
		                    
		            		case R.id.radio1:
		                    	txtcle.setVisibility(View.VISIBLE);
		                        edtcle.setVisibility(View.VISIBLE);
		                        a = true;
		                        break;
		                }
		            }
		        });
				
				alertDialogBuilder.setCancelable(false).setNegativeButton("Partager",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						 getWindow().setSoftInputMode(WindowManager.LayoutParams
									.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						
						char[] c = null;
						int i = 0;
						c = mBodyText.getText().toString().toCharArray();
						
						if (a){
					    	if (edtcle.getText().toString().equals(""))
					    		edtcle.setText("0");		    	
							
					    	while(i<c.length){		            		
			            		c[i]=(char) (c[i] + Integer.parseInt(edtcle.getText().toString()));
			            		i++;
					    	}
					    }
					    
						String str = new String(c);
					    
						Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				        sharingIntent.setType("text/plain");
				        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, str);
				        startActivity(sharingIntent);
					}
				})
					.setPositiveButton("Annuler", null);
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
		    	
				return true;
		    
		    case R.id.screen_on: // Ecran reste actif.
		    	
		    	if (item.isChecked()){
		    		getWindow().clearFlags(WindowManager
		    					.LayoutParams.FLAG_KEEP_SCREEN_ON);
		    		item.setChecked(false);
		        }
		    	else{
		        	getWindow().addFlags(WindowManager
		        				.LayoutParams.FLAG_KEEP_SCREEN_ON);
		        	item.setChecked(true);
		        }
		    	
		    	return true;
		    
		    case R.id.fix_orientation: // Fixer la rotation actuelle de l'écran.
		    	
		    	if (item.isChecked()){		    		
		    		setRequestedOrientation(ActivityInfo
		    					.SCREEN_ORIENTATION_UNSPECIFIED);
		    		item.setChecked(false);
		        }
		    	else{
		    		int currentOrientation = getResources()
		    					.getConfiguration().orientation;
		    		
		    		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
		    		   setRequestedOrientation(ActivityInfo
		    				   	.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		    		}
		    		else 
		    		   setRequestedOrientation(ActivityInfo
		    				   	.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		    		item.setChecked(true);
		        }
		    	
		    	return true;
		    
		    case R.id.menu_delete: // Bouton supprimer en haut.
		    		
		    		if(note != null){
		    			note.close();
		    			note = null;
		    		}
	    			
		    		if (modified(1)){
	    				DialogInterface.OnClickListener dialogClickListener 
	    					= new DialogInterface.OnClickListener() {
	    	    			
	    					@Override
	    	    			public void onClick(DialogInterface dialog, int which) {
	    	    				switch (which){
	    	    					
	    	    					case DialogInterface.BUTTON_NEGATIVE:
	    	    						if(mRowId != null)
	    	    							mDbHelper.deleteNote(mRowId);
	    	    						Toast.makeText(NoteEdit.this, "Supprimée!"
	    	    										, Toast.LENGTH_SHORT).show();
	    	    						finish();
	    	    						break;
	    	    				}
	    	    			}
	    	    		};
	    	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	    		builder.setMessage("Supprimer vraiment cette note?")
	    	    		.setPositiveButton("Non", dialogClickListener)
	    	    		.setNegativeButton("Oui", dialogClickListener).show();
	    	    	}
	    			else {
	    				if(mRowId != null)
	    					mDbHelper.deleteNote(mRowId);
	    				Toast.makeText(NoteEdit.this, "Supprimée!"
	    								, Toast.LENGTH_SHORT).show();
						finish();
					}	    			    	
		       
		    		return true;
		    
		    case R.id.menu_save: // Bouton enregistrer en haut.
	    		
		    	saveState();
	    		Toast.makeText(NoteEdit.this, "Note Enregistrée!"
	    						, Toast.LENGTH_SHORT).show();
	    		finish();	    	
		    
		    default:
		    	return super.onOptionsItemSelected(item);
		    }
		}
	    
	    private void saveState() {
	        	// Sauvegarder la note dans la BD.
	    	String title = mTitleText.getText().toString();;
	        String body = null;
			
	        try {
				body = AESEncryptAdapter.encrypt
							(mBodyText.getText().toString());
			} catch (Exception e) {				
				e.printStackTrace();
			}	        

	        if(mRowId == null){	        	
	        	mDbHelper.createNote(title, body, curDate);					
	        }
	        else{        	
	        	mDbHelper.updateNote(mRowId, title, body, curDate);
			}
	    }  
	  
	    private void populateFields() {
	        	/* Lecture à partir de la BD et remplissage 
	        	 * des champs titre et texte dans l'éditeur.*/
			if (mRowId != null) {
	            
				note = mDbHelper.fetchNote(mRowId);
				mTitleText.setText(note.getString(note
						.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
	            
				try {
				
					mBodyText.setText(AESEncryptAdapter.decrypt(note
						.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY))));
				
				} catch (Exception e) {				
					e.printStackTrace();
				}

	            t = mTitleText.getText().toString();
	    		b = mBodyText.getText().toString();
	        }
	    }
	    
		public void onBackPressed() {
	    		// Si la touche retour est appuyée.
			if (modified(2)){
	    		DialogInterface.OnClickListener dialogClickListener 
	    			= new DialogInterface.OnClickListener() {
	    			
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				switch (which){
	    					
	    					case DialogInterface.BUTTON_NEGATIVE:
	    						saveState();
	    						Toast.makeText(NoteEdit.this, "Note Enregistrée!"
	    										, Toast.LENGTH_SHORT).show();
	    						finish();
	    						break;
	    					
	    					case DialogInterface.BUTTON_POSITIVE:
	    						finish();
	    					break;
	    				}
	    			}
	    		};
	    		
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage("Enregistrer les modifications effectuées?")
	    		.setPositiveButton("Non", dialogClickListener)
	    		.setNegativeButton("Oui", dialogClickListener).show();
	    	}
	    	else
	    		finish();
		}
	    
		private boolean modified (int i){
	    		// Retourner l'état de contenu final (modifié ou pas).
			switch(i){	    	
	    		
	    		case 1:	    			
	    			if (mTitleText.getText().toString().equals("")==false 
	    				|| mBodyText.getText().toString().equals("")==false)
	    				return true;
	    		
	    		case 2:	    			
	    			if(mRowId != null){
	    				if (mTitleText.getText().toString().equals(t)==false 
	    					|| mBodyText.getText().toString().equals(b)==false)
	    					return true;
	    			}
	    			else if (mTitleText.getText().toString().equals("")==false 
	    					|| mBodyText.getText().toString().equals("")==false){
	    				return true;
	    			}
	    	}			
	    	return false;
	    }
}
