package com.example.informerh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button btEntrar; // Botão entrar da tela inicial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Layout

        btEntrar = findViewById(R.id.btEntrar); // Id do botão entrar
        checkBiometricSupported(); // Método de biometria suportada ou não
        Executor executor = ContextCompat.getMainExecutor(this); // Executor
        // Callback de erro, sucesso e falha de autenticação da biometria
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            // Erro na autenticação da biometria
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Mensagem com o código do erro
                Toast.makeText(MainActivity.this, "Erro de autenticação: " + errString, Toast.LENGTH_SHORT).show();
            }
            // Autenticação biométrica bem sucedida
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Mensagem indicando o sucesso da autenticação. SUBUSTITUIR a mensagem com o código para passar para a próxima tela
                Toast.makeText(MainActivity.this, "Autenticado com sucesso", Toast.LENGTH_SHORT).show();
            }
            // Falha na autenticação
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Mensagem indicando falha na autenticação
                Toast.makeText(MainActivity.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
            }
        });
        // Função de clique no botão
        btEntrar.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric(); // Puxa o prompt da digital
            promptInfo.setDeviceCredentialAllowed(true); // Checa as credencias do dispositivo (PIN, padrão, etc)
            biometricPrompt.authenticate(promptInfo.build()); // Autentica a forma de entrada
        });
    }
    // Cria o prompt para entrar com a digital
    BiometricPrompt.PromptInfo.Builder dialogMetric() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Informe RH") // Título do propmt
                .setSubtitle("Desbloqueie seu dispositivo"); // Subtítulo do prompt
    }
    // Checa se o dispositivo possui recursos biométricos
    private void checkBiometricSupported() {
        String info = ""; // String
        BiometricManager manager = BiometricManager.from(this);
        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK
                | BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            // Se o dispositivo possui recursos biométricos:
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "O app pode autenticar utilizando biometria.";
                enableButton(true);
                break;
            // Se o dispositivo não possuir recursos biométricos:
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "Não há recursos biométricos neste dispositivo.";
                enableButton(false);
                break;
            // Se os recursos biométricos estiverem indisponíveis no momento
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Os recursos biométricos estão indisponíveis no momento.";
                enableButton(false);
                break;
            // Se possuir recursos biométricos, mas não possuir uma digital previamente cadastrada
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "É nencessário registrar pelo menos uma digital.";
                enableButton(false, true);
                break;
            // Padrão
            default:
                info = "Causa desconhecida.";
                break;
        }
        // Cria um popup com as informações (info) do método checkBiometricSupported()
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();

    }
    // Permite a ativção do botão entrar
    void enableButton(boolean enable) {
        btEntrar.setEnabled(enable);
        btEntrar.setEnabled(true);
    }
    // Se a pessoa não possuir uma digital previamente cadastrada, pede para que cadastre uma
    void enableButton(boolean enable, boolean enroll) {
        enableButton(enable);
        if (!enroll) return;
        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.BIOMETRIC_WEAK);
        startActivity(enrollIntent);
    }
}