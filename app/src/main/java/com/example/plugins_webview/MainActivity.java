package com.example.plugins_webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final int CAMERA_AND_MICROPHONE_PERMISSION_CODE = 1;
    private static final int FILE_CHOOSER_REQUEST_CODE = 2; // Número de solicitud para la selección de archivos
    private ValueCallback<Uri[]> mUploadMessage; // Para gestionar la carga de archivos
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript en el WebView
        webSettings.setAllowFileAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // Permitir la reproducción de medios sin interacción del usuario

        webView.setDownloadListener(new DownloadListener() {
            // Aquí puedes implementar cómo deseas manejar las descargas.
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // Por ejemplo, puedes abrir un navegador externo para la descarga.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                // Configurar un WebChromeClient para manejar las solicitudes de acceso a la cámara y al micrófono
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    request.grant(request.getResources());
                } else {
                    // Solicitar permisos de cámara y micrófono
                    requestCameraAndMicrophonePermission();
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                // Configurar un WebChromeClient para manejar las solicitudes de archivos
                Intent intent = fileChooserParams.createIntent();
                intent.setType("*/*");
                String[] mimeTypes = {"image/jpeg", "image/png", "application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                mUploadMessage = filePathCallback;
                startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient()); // Controla la navegación en el WebView

        // Cargar la URL del sitio web que deseas mostrar en el WebView
        webView.loadUrl("https://www.example.com");
    }

    // Manejo de resultados de archivos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (mUploadMessage == null) return;
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null;
        }
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