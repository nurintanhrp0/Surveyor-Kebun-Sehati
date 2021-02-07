package ventures.g45.kebunsehati.surveyor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity {

    EditText inpNohp, inpPin;
    Button btnLogin, btnCobalagi, btnGantipin;
    LinearLayout blockInput, blockGagal, blockSukses;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String defaultUrl, urlSignIn, urlUpdatePin, noHp;
    Integer login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        defaultUrl = ((KebunSehati) getApplication()).getUrl();
        urlSignIn = defaultUrl + "signinpinkurir.html";
        urlUpdatePin = defaultUrl + "dokurirupdatepin.html";

        sharedPreferences = getSharedPreferences("kebunsehati", 0);
        editor = sharedPreferences.edit();

        inpNohp = findViewById(R.id.inpNohp);
        inpPin = findViewById(R.id.inpPin);
        btnLogin = findViewById(R.id.btnLogin);
        btnCobalagi = findViewById(R.id.btnCobalagi);
        btnGantipin = findViewById(R.id.btnGantiPIN);
        blockInput = findViewById(R.id.blockInput);
        blockGagal = findViewById(R.id.blockGagal);
        blockSukses = findViewById(R.id.blockSukses);
        inpNohp.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        inpNohp.setSingleLine(true);
        inpPin.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        inpPin.setSingleLine(true);
        inpPin.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login().execute();
            }
        });

        btnCobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login().execute();
            }
        });

        btnGantipin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGantiPin();
            }
        });
    }

    private class Login extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("phone", inpNohp.getText().toString()));
            params.add(new BasicNameValuePair("pin", inpPin.getText().toString()));

            JSONObject json = jsonParser.makeHttpRequest(urlSignIn, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                        if (result.getInt("error") == 2) {
                           btnLogin.setVisibility(View.GONE);
                           inpNohp.requestFocus();
                           inpPin.getText().clear();
                           inpNohp.getText().clear();
                           inpPin.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_half_radius_border));
                           inpPin.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
                           inpNohp.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_half_radius_border));
                           inpNohp.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
                           blockGagal.setVisibility(View.VISIBLE);
                        } else {
                            noHp = result.getString("noHp");
                            login = result.getInt("login");
                            editor.putString("noHp", noHp);
                            editor.putInt("login", login);
                            editor.apply();
                            if (result.getString("aktivasi").equals("belum")){
                                blockSukses.setVisibility(View.VISIBLE);
                                blockInput.setVisibility(View.GONE);
                                blockGagal.setVisibility(View.GONE);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void dialogGantiPin(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignIn.this, R.style.DialogPutih);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_input_pin, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        Button btnPin = (Button) dialogView.findViewById(R.id.btnSimpan);
        final EditText inpPin = (EditText) dialogView.findViewById(R.id.inpPin);
        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (inpPin.getText().length() > 0){
                   new UpdatePin().execute(inpPin.getText().toString());
               }else {
                   Toast.makeText(SignIn.this, "PIN tidak boleh kosong!", Toast.LENGTH_SHORT).show();
               }
            }
        });

        alertDialog.show();
    }

    private class UpdatePin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Sending data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            ArrayList params = new ArrayList();

            params.add(new BasicNameValuePair("phone", noHp));
            params.add(new BasicNameValuePair("pin", args[0]));

            JSONObject json = jsonParser.makeHttpRequest(urlUpdatePin, "POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            if ((pDialog != null) && pDialog.isShowing())
                pDialog.dismiss();
            pDialog = null;

            try {
                if (result != null) {
                    if (result.getInt("error") == 1) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        System.exit(0);
    }
}
