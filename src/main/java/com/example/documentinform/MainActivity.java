package com.example.documentinform;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.documentinform.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ListView listView;
    private static final int REQUEST_PERMISSION = 1;
    private String[] array;
    private ArrayAdapter<String> adapter;
    private ActivityMainBinding binding;
    private int menu_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Вывод списка документов на экран content_main
        // Находим listview, который создали на форме
        listView = findViewById(R.id.lists);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            initViews();
        }
        // Инициализация массива значениями из ресурсов
        array = getResources().getStringArray(R.array.user_array);
        // Адаптер для вывода строк на экран
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array)));
        // Присвоение listview адаптера
        listView.setAdapter(adapter);

        setSupportActionBar(binding.appBarMain.toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, docs_content_activity.class);
                intent.putExtra("menu_index", menu_index);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        binding.appBarMain.toolbar.setTitle(R.string.menu_user);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_mydocs) {
            menu_index = 0;
            binding.appBarMain.toolbar.setTitle(R.string.menu_docs);
            Toast.makeText(this, "My docs opened", Toast.LENGTH_SHORT).show();
            array = getResources().getStringArray(R.array.docs_array);
            adapter.clear();
            adapter.addAll(array);
            adapter.notifyDataSetChanged();

        }else if(id == R.id.nav_familiarized){
            menu_index = 1;
            binding.appBarMain.toolbar.setTitle(R.string.menu_familiarized);
            Toast.makeText(this, "Familiarized opened", Toast.LENGTH_SHORT).show();
            array = getResources().getStringArray(R.array.familiarized_array);
            adapter.clear();
            adapter.addAll(array);
            adapter.notifyDataSetChanged();

        }else if(id == R.id.ic_user){
            menu_index = 2;
            binding.appBarMain.toolbar.setTitle(R.string.menu_user);
            Toast.makeText(this, "Private office opened", Toast.LENGTH_SHORT).show();
            array = getResources().getStringArray(R.array.user_array);
            adapter.clear();
            adapter.addAll(array);
            adapter.notifyDataSetChanged();

        }else if(id == R.id.nav_exit) {
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            initViews();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    // в разрешении отказано (в первый раз, когда чекбокс "Больше не спрашивать" ещё не показывается)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    // в разрешении отказано (выбрано "Больше не спрашивать")
                    else {
                        // показываем диалог, сообщающий о важности разрешения
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(
                                "Вы отказались предоставлять разрешение на чтение хранилища.\n\nЭто необходимо для работы приложения."
                                        + "\n\n"
                                        + "Нажмите \"Предоставить\", чтобы предоставить приложению разрешения.")
                                // при согласии откроется окно настроек, в котором пользователю нужно будет вручную предоставить разрешения
                                .setPositiveButton("Предоставить", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                // закрываем приложение
                                .setNegativeButton("Отказаться", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
                break;
            }
        }
    }
}