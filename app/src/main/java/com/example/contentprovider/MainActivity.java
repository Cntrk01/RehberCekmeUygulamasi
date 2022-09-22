package com.example.contentprovider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.contentprovider.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ListView listView=findViewById(R.id.listView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_CONTACTS},1); //api23 den sonra bu izinleri istiyoruz

        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    ContentResolver contentResolver=getContentResolver(); //İçerik sağlayıcıdan içerik alacağız.
                    String[] projection={ContactsContract.Contacts.DISPLAY_NAME}; //Rehberdeki birçok şey var isim,fotoğraf,muzik, gibi özel veriler
                    Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI
                            ,projection //string dizisini verdik
                            ,null //kullanmıyoruz
                            ,null,
                            ContactsContract.Contacts.DISPLAY_NAME);//Display name e göre veri çekeceğiz dedik

                    if(cursor !=null){
                        ArrayList<String> contactList=new ArrayList<String>();
                        String columnIx=ContactsContract.Contacts.DISPLAY_NAME;
                        while(cursor.moveToNext()){
                            contactList.add(cursor.getString(cursor.getColumnIndex(columnIx)));
                        }
                        cursor.close();

                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,contactList);
                        listView.setAdapter(adapter);
                    }

                }else{
                         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_CONTACTS)){
                                    ActivityCompat.requestPermissions(MainActivity.this
                                            ,new String[] {Manifest.permission.READ_CONTACTS},1);
                                }else{ //Kullanıcı eğer bidaha izin vermezse izin için uygulamanın kurulu olduğu yere götürüp izin almamızı sağlayacak appinfodan uygulama bilgileri ayarlarından
                                    Intent intent=new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//İzin vermek için direk uygulamanın kurulu oldugu sayfaya gidecek
                                    //uri kullanma sebebimiz biz bir yola yani sayfaya gidiyoruz.Giderken url olarak alıyoruz.Bunu böyle belirtiyoruz.!
                                    Uri uri=Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                                    //1)scheme paket istiyor "package" verdik,2)Paket ismini veriyoruz.3)Fragment varmı yokmu yok null dedik
                                    intent.setData(uri);
                                    MainActivity.this.startActivity(intent);
                                }



                            }
                        }).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}