package com.example.final_am_acn4av_gonzalez_oturakdjian;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvEmailError, tvPasswordError, tvSuccessMessage, tvForgotPassword;
    private Button btnLogin, btnRegisterBottom;
    private FirebaseAuth mAuth;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
        setupTextWatchers();

        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            etEmail.setText(email);
        }

        checkCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            CarritoManager.cargarCarrito(new CarritoManager.CarritoLoadListener() {
                @Override
                public void onCarritoLoaded(List<Product> carrito) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvEmailError = findViewById(R.id.tv_email_error);
        tvPasswordError = findViewById(R.id.tv_password_error);
        tvSuccessMessage = findViewById(R.id.tv_success_message);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegisterBottom = findViewById(R.id.btn_register_bottom);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            if (validateForm()) {
                performLogin();
            }
        });

        if (btnRegisterBottom != null) {
            btnRegisterBottom.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        tvForgotPassword.setOnClickListener(v -> {
            showPasswordResetDialog();
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etEmail.setBackgroundResource(R.drawable.input_background_focused);
            } else {
                etEmail.setBackgroundResource(R.drawable.input_background);
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etPassword.setBackgroundResource(R.drawable.input_background_focused);
            } else {
                etPassword.setBackgroundResource(R.drawable.input_background);
            }
        });
    }

    private void setupTextWatchers() {

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvEmailError.getVisibility() == View.VISIBLE) {
                    tvEmailError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvPasswordError.getVisibility() == View.VISIBLE) {
                    tvPasswordError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        hideAllErrors();

        if (email.isEmpty()) {
            showError(tvEmailError, getString(R.string.error_email_required));
            isValid = false;
        } else if (!isValidEmail(email)) {
            showError(tvEmailError, getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (password.isEmpty()) {
            showError(tvPasswordError, getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            showError(tvPasswordError, getString(R.string.error_password_short));
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();
    }

    private void showError(TextView errorView, String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    private void hideAllErrors() {
        tvEmailError.setVisibility(View.GONE);
        tvPasswordError.setVisibility(View.GONE);
        tvSuccessMessage.setVisibility(View.GONE);
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            tvSuccessMessage.setText(getString(R.string.success_login));
                            tvSuccessMessage.setVisibility(View.VISIBLE);

                            CarritoManager.cargarCarrito(new CarritoManager.CarritoLoadListener() {
                                @Override
                                public void onCarritoLoaded(List<Product> carrito) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(String error) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    } else {
                        btnLogin.setEnabled(true);
                        btnLogin.setText(getString(R.string.login_button));
                        
                        String errorMessage = "Error al iniciar sesión";
                        if (task.getException() != null) {
                            String errorCode = task.getException().getMessage();
                            if (errorCode != null) {
                                if (errorCode.contains("user-not-found")) {
                                    errorMessage = "No existe una cuenta con este correo electrónico";
                                    showError(tvEmailError, errorMessage);
                                } else if (errorCode.contains("wrong-password")) {
                                    errorMessage = "Contraseña incorrecta";
                                    showError(tvPasswordError, errorMessage);
                                } else if (errorCode.contains("invalid-email")) {
                                    errorMessage = "Correo electrónico inválido";
                                    showError(tvEmailError, errorMessage);
                                } else if (errorCode.contains("too-many-requests")) {
                                    errorMessage = "Demasiados intentos fallidos. Intenta más tarde";
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                } else {
                                    errorMessage = "Error: " + errorCode;
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reset_password, null);
        
        EditText etResetEmail = dialogView.findViewById(R.id.et_reset_email);
        TextView tvResetError = dialogView.findViewById(R.id.tv_reset_error);
        Button btnSendReset = dialogView.findViewById(R.id.btn_send_reset);
        Button btnCancelReset = dialogView.findViewById(R.id.btn_cancel_reset);
        
        // Pre-llenar con el email del campo de login si existe
        String currentEmail = etEmail.getText().toString().trim();
        if (!currentEmail.isEmpty() && isValidEmail(currentEmail)) {
            etResetEmail.setText(currentEmail);
        }
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        
        // Listener para el botón de enviar
        btnSendReset.setOnClickListener(v -> {
            String email = etResetEmail.getText().toString().trim();
            
            if (email.isEmpty()) {
                tvResetError.setText(getString(R.string.error_email_required));
                tvResetError.setVisibility(View.VISIBLE);
                return;
            }
            
            if (!isValidEmail(email)) {
                tvResetError.setText(getString(R.string.error_email_invalid));
                tvResetError.setVisibility(View.VISIBLE);
                return;
            }
            
            tvResetError.setVisibility(View.GONE);
            btnSendReset.setEnabled(false);
            btnSendReset.setText("Enviando...");
            
            sendPasswordResetEmail(email, dialog, btnSendReset);
        });
        
        // Listener para el botón de cancelar
        btnCancelReset.setOnClickListener(v -> dialog.dismiss());
        
        // Limpiar error cuando el usuario escribe
        etResetEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvResetError.getVisibility() == View.VISIBLE) {
                    tvResetError.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        dialog.show();
    }
    
    private void sendPasswordResetEmail(String email, AlertDialog dialog, Button btnSendReset) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, 
                                getString(R.string.reset_password_success), 
                                Toast.LENGTH_LONG).show();
                    } else {
                        btnSendReset.setEnabled(true);
                        btnSendReset.setText(getString(R.string.reset_password_send));
                        
                        String errorMessage = getString(R.string.reset_password_error);
                        if (task.getException() != null) {
                            Exception exception = task.getException();
                            
                            if (exception instanceof FirebaseAuthException) {
                                FirebaseAuthException authException = (FirebaseAuthException) exception;
                                String errorCode = authException.getErrorCode();
                                
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = getString(R.string.reset_password_error_invalid_email);
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        // Por seguridad, Firebase no revela si el usuario existe o no
                                        // Pero podemos mostrar un mensaje genérico
                                        errorMessage = getString(R.string.reset_password_success);
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, 
                                                getString(R.string.reset_password_success), 
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    case "ERROR_NETWORK_REQUEST_FAILED":
                                        errorMessage = getString(R.string.reset_password_error_network);
                                        break;
                                    default:
                                        errorMessage = getString(R.string.reset_password_error) + ": " + errorCode;
                                        break;
                                }
                            } else {
                                String exceptionMessage = exception.getMessage();
                                if (exceptionMessage != null && exceptionMessage.contains("network")) {
                                    errorMessage = getString(R.string.reset_password_error_network);
                                }
                            }
                        }
                        
                        TextView tvResetError = dialog.findViewById(R.id.tv_reset_error);
                        if (tvResetError != null) {
                            tvResetError.setText(errorMessage);
                            tvResetError.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

