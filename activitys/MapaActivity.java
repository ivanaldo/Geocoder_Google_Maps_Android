package com.example.teste4vets.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teste4vets.R;
import com.example.teste4vets.classes.Usuarios;
import com.example.teste4vets.configuracoes.ConfiguracaoFirebase;
import com.example.teste4vets.permissoes.Permissoes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth autenticacao;
    private GoogleMap mMap;
    private String nome, telefone, rua, bairro, cidade, cep, numero;
    private DatabaseReference dados;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private String idUsuario;
    private List<Usuarios> listaDados = new ArrayList<>();

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        //inicializaComponentes
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        dados = ConfiguracaoFirebase.getFirebase();
        idUsuario = ConfiguracaoFirebase.getIdUsuario();
        recuperarDados();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void recuperarDados(){
        DatabaseReference dadosRef = dados
                .child("usuarios")
                .child(idUsuario);
        dadosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    for (DataSnapshot lista : snapshot.getChildren()) {

                        Usuarios dadosUsuario = lista.getValue(Usuarios.class);
                       // nome = usuario.getNome();
                       // telefone = usuario.getTelefone();
                        rua = dadosUsuario.getRua();
                        numero = dadosUsuario.getNumero();
                        bairro = dadosUsuario.getBairro();
                        cidade = dadosUsuario.getCidade();
                        cep = dadosUsuario.getCep();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            String endereco = "Rua São Lázaro, Santa Terezinha, Alagoinhas - BA - CEP 48011310";
            List<Address> listEndereco = geocoder.getFromLocationName(endereco,1);
            if (listEndereco != null && listEndereco.size() > 0 ){
                Double lat = listEndereco.get(0).getLatitude();
                Double longi = listEndereco.get(0).getLongitude();

                Double latitude = lat;
                Double longitude = longi;

                LatLng usuario = new LatLng(latitude, longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(usuario).title(""+ nome));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usuario, 3));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Configura evento de clique
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        //objeto responsável  por gerenciara localização do usuário
        /*locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override //localização do usuário
            public void onLocationChanged(Location location) {





            }

            @Override //quando o status do serviço de localização muda (abilitado/desabilitado)
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override //serviço de localização abilitado
            public void onProviderEnabled(String provider) {

            }

            @Override //serviço de localização desabilitado
            public void onProviderDisabled(String provider) {

            }
        };*/

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    locationManager.GPS_PROVIDER,
                    1000,
                    0,
                    locationListener
            );
        }

        //Configura evento de clique
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissoesResultado : grantResults) {
            //permissao denied (negada)
            if (permissoesResultado == PackageManager.PERMISSION_DENIED) {
                //alerta
                alertaValidacaoPermissao();
            } else if (permissoesResultado == PackageManager.PERMISSION_GRANTED) {
                //recuperar localização do usuario
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            locationManager.GPS_PROVIDER,
                            1000,
                            0,
                            locationListener
                    );
                }
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){

        switch (item.getItemId()) {
            case R.id.menu_perfil:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;
            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}