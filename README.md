# Webview Android example
## _Bienvenidos_

Este proyecto contiene un ejemplo básico de la integración de **1DOC3** mediante un webview en **Java**
## Configuración de permisos
en tu archivo **AndroidManifest.xml** asegurate de tener los siguientes permisos:
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
```

Estos permisos son necesarios para el uso correcto de videollamada y envío de audios.


## Configuración del Webview

 Reemplaza la **URL** de ejemplo por la de tu proyecto
```
webView.loadUrl("https://www.example.com");
```
Asegúrate de configurar correctamente los permisos. En el **MainActivity.java** vas a ver un ejemplo de cómo puedes hacerlo.