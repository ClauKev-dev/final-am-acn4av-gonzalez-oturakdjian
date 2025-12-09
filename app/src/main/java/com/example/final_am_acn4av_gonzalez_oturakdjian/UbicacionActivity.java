package com.example.final_am_acn4av_gonzalez_oturakdjian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class UbicacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    
    // Random address coordinates: Av. Corrientes 1234, Buenos Aires, Argentina
    private static final double LATITUDE = -34.603722;
    private static final double LONGITUDE = -58.381592;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        mapView = findViewById(R.id.map_view);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        setupBottomNavigation();
        setupTopNavigation();
        setupDrawer();
        navigateToTab(4);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Create location
        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        
        // Add marker
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Medi Pocket")
                .snippet("Av. Corrientes 1234, Buenos Aires"));
        
        // Move camera to location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        
        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    protected void setupBottomNavigation() {
        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabDescuentos = findViewById(R.id.tab_descuentos);
        LinearLayout tabTienda = findViewById(R.id.tab_tienda);
        LinearLayout tabCuadrado = findViewById(R.id.tab_cuadrado);
        LinearLayout tabMenu = findViewById(R.id.tab_menu);

        final int TAB_HOME = 0;
        final int TAB_DESCUENTOS = 1;
        final int TAB_TIENDA = 2;
        final int TAB_CUADRADO = 3;
        final int TAB_MENU = 4;

        View.OnClickListener listener = v -> {
            int tabIndex = -1;
            int id = v.getId();

            if (id == R.id.tab_home) {
                tabIndex = TAB_HOME;
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (id == R.id.tab_descuentos) {
                tabIndex = TAB_DESCUENTOS;
                startActivity(new Intent(this, DescuentosActivity.class));
                finish();
            } else if (id == R.id.tab_tienda) {
                tabIndex = TAB_TIENDA;
                startActivity(new Intent(this, CarritoActivity.class));
                finish();
            } else if (id == R.id.tab_cuadrado) {
                tabIndex = TAB_CUADRADO;
                startActivity(new Intent(this, LootboxActivity.class));
                finish();
            } else if (id == R.id.tab_menu) {
                tabIndex = TAB_MENU;
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                NavigationView navView = findViewById(R.id.nav_view);
                if (drawerLayout != null && navView != null) {
                    drawerLayout.openDrawer(navView);
                }
            }

            if (tabIndex != -1) {
                navigateToTab(tabIndex);
            }
        };

        if (tabHome != null) tabHome.setOnClickListener(listener);
        if (tabDescuentos != null) tabDescuentos.setOnClickListener(listener);
        if (tabTienda != null) tabTienda.setOnClickListener(listener);
        if (tabCuadrado != null) tabCuadrado.setOnClickListener(listener);
        if (tabMenu != null) tabMenu.setOnClickListener(listener);
    }

    protected void navigateToTab(int tabIndex) {
        resetTabs();

        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabDescuentos = findViewById(R.id.tab_descuentos);
        LinearLayout tabTienda = findViewById(R.id.tab_tienda);
        LinearLayout tabCuadrado = findViewById(R.id.tab_cuadrado);
        LinearLayout tabMenu = findViewById(R.id.tab_menu);

        switch (tabIndex) {
            case 0:
                if (tabHome != null) tabHome.setAlpha(1f);
                break;
            case 1:
                if (tabDescuentos != null) tabDescuentos.setAlpha(1f);
                break;
            case 2:
                if (tabTienda != null) tabTienda.setAlpha(1f);
                break;
            case 3:
                if (tabCuadrado != null) tabCuadrado.setAlpha(1f);
                break;
            case 4:
                if (tabMenu != null) tabMenu.setAlpha(1f);
                break;
        }
    }

    protected void resetTabs() {
        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabDescuentos = findViewById(R.id.tab_descuentos);
        LinearLayout tabTienda = findViewById(R.id.tab_tienda);
        LinearLayout tabCuadrado = findViewById(R.id.tab_cuadrado);
        LinearLayout tabMenu = findViewById(R.id.tab_menu);

        if (tabHome != null) tabHome.setAlpha(0.6f);
        if (tabDescuentos != null) tabDescuentos.setAlpha(0.6f);
        if (tabTienda != null) tabTienda.setAlpha(0.6f);
        if (tabCuadrado != null) tabCuadrado.setAlpha(0.6f);
        if (tabMenu != null) tabMenu.setAlpha(0.6f);
    }

    protected void setupTopNavigation() {
        ImageView ivProfile = findViewById(R.id.iv_profile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            });
            ivProfile.setClickable(true);
            ivProfile.setFocusable(true);
        }
    }

    protected void setupDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_pedidos_curso) {
                    Intent intent = new Intent(this, PedidosEnCursoActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_configuracion) {
                    Toast.makeText(this, "Configuración - Próximamente", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_carga_documentos) {
                    Intent intent = new Intent(this, SubirRecetaActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_donde_estamos) {
                    Toast.makeText(this, "Ya estás viendo nuestra ubicación", Toast.LENGTH_SHORT).show();
                }
                
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(navigationView);
                }
                return true;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }
}
