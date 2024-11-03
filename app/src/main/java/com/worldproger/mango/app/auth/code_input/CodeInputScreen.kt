package com.worldproger.mango.app.auth.code_input

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worldproger.mango.R
import com.worldproger.mango.app.auth.code_input.otp_field.OTPTextField
import com.worldproger.mango.app.auth.code_input.otp_field.OtpTextFieldDefaults
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeInputScreen(
    phoneNumber: String,
    onNavigateBack: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: CodeInputViewModel = koinViewModel { parametersOf(phoneNumber) }
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CodeInputSideEffect.NavigateToMain -> onNavigateToMain()
                is CodeInputSideEffect.NavigateToRegistration -> onNavigateToRegistration()
                is CodeInputSideEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.fillMaxHeight(0.3f))

            Text(
                text = stringResource(R.string.enter_code),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OTPTextField(
                value = state.code,
                onTextChanged = {
                    if (it.length > 6) {
                        println("Unable to enter more than 6 digits")
                        return@OTPTextField
                    }

                    if (state.isLoading) {
                        println("Unable to enter while loading")
                        return@OTPTextField
                    }

                    viewModel.processIntent(CodeInputIntent.EnterCode(it))

                    if (it.length == 6) {
                        viewModel.processIntent(CodeInputIntent.VerifyCode)
                    }
                },
                digitContainerStyle = OtpTextFieldDefaults.outlinedContainer(
                    size = 48.dp,
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    focusedBorderWidth = 1.5.dp,
                    unfocusedBorderWidth = 1.5.dp,
                    errorColor = Color.Red,
                ),
                spaceBetween = 6.dp,
                numDigits = 6,
                isError = state.errorMessage != null,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.sent_to_phone_number, phoneNumber),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}