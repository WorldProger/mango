package com.worldproger.mango.app.auth.phone_input

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.joelkanyi.jcomposecountrycodepicker.component.KomposeCountryCodePicker
import com.joelkanyi.jcomposecountrycodepicker.component.rememberKomposeCountryCodePickerState
import com.worldproger.mango.R
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun PhoneInputScreen(
    onNavigateToCodeInput: (String) -> Unit,
    viewModel: PhoneInputViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is PhoneInputSideEffect.NavigateToCodeInput -> onNavigateToCodeInput(effect.phoneNumber)
                is PhoneInputSideEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.fillMaxHeight(0.3f))

            Text(
                text = stringResource(R.string.enter_phone_number),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentLocale: Locale = Locale.getDefault()
            val countryCode: String = currentLocale.country
            val fieldState = rememberKomposeCountryCodePickerState(
                defaultCountryCode = countryCode,
            )

            KomposeCountryCodePicker(
                modifier = Modifier
                    .fillMaxWidth(),
                text = state.phoneNumber,
                onValueChange = { viewModel.processIntent(PhoneInputIntent.EnterPhoneNumber(it)) },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                state = fieldState,
                interactionSource = MutableInteractionSource(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.phone_placeholder),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
            )

            Spacer(Modifier.weight(1.0f))

            Button(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .height(54.dp),
                onClick = {
                    viewModel.processIntent(PhoneInputIntent.SendCode(fieldState.getCountryPhoneCode() + state.phoneNumber))
                },
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.send_code))
                }
            }
        }
    }
}
