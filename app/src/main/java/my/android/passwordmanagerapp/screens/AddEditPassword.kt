
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import my.android.passwordmanagerapp.data.Password
import kotlin.random.Random

@Composable
fun AddEditPasswordScreen(
    password: Password?,
    onSaveClicked: (Password) -> Unit
) {
    var accountType by remember { mutableStateOf(password?.accountType ?: "") }
    var username by remember { mutableStateOf(password?.username ?: "") }
    var passwordValue by remember { mutableStateOf(password?.password ?: "") }
    var showPassword by remember { mutableStateOf(false) }
    var accountTypeError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = accountType,
            onValueChange = {
                accountType = it
                accountTypeError = if (isValidText(accountType)) "" else "Invalid account type"
            },
            label = { Text("Account Type") },
            shape = RoundedCornerShape(percent = 20),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            isError = accountTypeError.isNotEmpty()
        )
        if (accountTypeError.isNotEmpty()) {
            Text(
                text = accountTypeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = if (isValidText(username)) "" else "Invalid username/email"
            },
            label = { Text("Username/Email") },
            shape = RoundedCornerShape(percent = 20),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            isError = usernameError.isNotEmpty()
        )
        if (usernameError.isNotEmpty()) {
            Text(
                text = usernameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordValue,
            onValueChange = {
                passwordValue = it
                passwordError = if (passwordValue.isNotEmpty()) "" else "Password cannot be empty"
            },
            label = { Text("Password") },
            shape = RoundedCornerShape(percent = 20),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            isError = passwordError.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordStrengthIndicator(passwordValue = passwordValue)

        if (passwordError.isNotEmpty()) {
            Text(
                text = passwordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            onClick = {
                passwordValue = generateRandomPassword()
                passwordError = if (passwordValue.isNotEmpty()) "" else "Password cannot be empty"
            },
            modifier = Modifier.padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text("Generate Password")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    if (isValidText(accountType) && isValidText(username) && passwordValue.isNotEmpty()) {
                        onSaveClicked(
                            Password(
                                id = password?.id ?: 0,
                                accountType = accountType,
                                username = username,
                                password = passwordValue
                            )
                        )
                    } else {
                        accountTypeError = if (isValidText(accountType)) "" else "Invalid account type"
                        usernameError = if (isValidText(username)) "" else "Invalid username/email"
                        passwordError = if (passwordValue.isNotEmpty()) "" else "Password cannot be empty"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text("Add New Account", color = Color.White)
            }
        }
    }
}

fun isValidText(text: String): Boolean {
    return text.matches(Regex("^[a-zA-Z0-9@.]+$"))
}

fun calculatePasswordStrength(password: String): Int {
    var score = 0
    if (password.length >= 8) score += 1
    if (password.any { it.isDigit() }) score += 1
    if (password.any { it.isUpperCase() }) score += 1
    if (password.any { it.isLowerCase() }) score += 1
    if (password.any { "!@#$%^&*()_+[]{}|;:,.<>?".contains(it) }) score += 1
    return score
}

fun generateRandomPassword(length: Int = 12): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+[]{}|;:,.<>?"
    return (1..length)
        .map { chars[Random.nextInt(chars.length)] }
        .joinToString("")
}

@Composable
fun PasswordStrengthIndicator(passwordValue: String) {
    val strength = calculatePasswordStrength(passwordValue)
    val strengthText = getPasswordStrengthText(strength)
    val color = when (strength) {
        0 -> Color.Red
        1 -> Color(0xFFFFA500)
        2 -> Color.Yellow
        3 -> Color.Green
        else -> Color(0xFF0C7704)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
        ) {
            (0 until 4).forEach { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (index < strength) color else Color.Gray)
                )
                if (index < 3) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = strengthText,
            modifier = Modifier.weight(.5f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun getPasswordStrengthText(strength: Int): String {
    return when (strength) {
        0 -> ""
        1 -> "Weak"
        2 -> "Could be stronger"
        3 -> "Strong Password"
        else -> "Very Strong"

    }
}
