package my.android.passwordmanagerapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.android.passwordmanagerapp.data.Password
import my.android.passwordmanagerapp.ui.theme.poppinsFamily
import my.android.passwordmanagerapp.ui.theme.sfproFamily

@Composable
fun PasswordDetailsScreen(
    password: Password,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Account Details",
            fontFamily = sfproFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            color = Color(0xFF3F7DE3),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AccountInfo("Account Type", password.accountType)
        Spacer(modifier = Modifier.height(8.dp))
        AccountInfo("Username/Email", password.username)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordField(
            label = "Password",
            password = password.password,
            showPassword = showPassword,
            onVisibilityChange = { showPassword = !showPassword }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionButtons(onEditClicked, onDeleteClicked)
    }
}

@Composable
fun AccountInfo(label: String, info: String) {
    Text(text = label, color = Color.LightGray, fontSize = 11.sp)
    Text(
        text = info,
        fontSize = 16.sp,
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun PasswordField(label : String, password: String, showPassword: Boolean, onVisibilityChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) { Column {
        Text(text = label, color = Color.LightGray, fontSize = 11.sp)
        Text(
            text = if (showPassword) password else "*******",
            fontSize = 16.sp,
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.SemiBold
        )
    }
        IconButton(onClick = onVisibilityChange) {
            Icon(
                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = if (showPassword) "Hide password" else "Show password"
            )
        }
    }
}

@Composable
fun ActionButtons(onEditClicked: () -> Unit, onDeleteClicked: () -> Unit) {
    Row {
        Button(
            onClick = onEditClicked,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Edit")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onDeleteClicked,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f)
        ) {
            Text("Delete")
        }
    }
}
