package com.example.plugins_webview;

import android.os.Bundle;
import android.webkit.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final int CAMERA_AND_MICROPHONE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript en el WebView
        webSettings.setMediaPlaybackRequiresUserGesture(false); // Permitir la reproducción de medios sin interacción del usuario

        // Configurar un WebChromeClient para manejar las solicitudes de acceso a la cámara y al micrófono
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    request.grant(request.getResources());
                } else {
                    // Solicitar permisos de cámara y micrófono
                    requestCameraAndMicrophonePermission();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient()); // Controla la navegación en el WebView

        // Cargar la URL del sitio web que deseas mostrar en el WebView
        webView.loadUrl("https://www.example.com");
    }

    private void requestCameraAndMicrophonePermission() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        // Solicita los permisos de cámara y micrófono al mismo tiempo
        ActivityCompat.requestPermissions(this, permissions, CAMERA_AND_MICROPHONE_PERMISSION_CODE);
    }

    // Manejo de resultados de solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Permiso otorgado por el usuario
            webView.reload();
        } else {
            // - Mensaje por permisos denegados
            // - notificar al usuario que sin estos permisos no puede acceder a la videollamada
            // - hacer un "webView.reload();"
            Toast.makeText(this, "Permisos necesarios para la cámara y el micrófono", Toast.LENGTH_SHORT).show();
        }
    }
}