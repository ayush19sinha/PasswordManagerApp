package my.android.passwordmanagerapp

import AddEditPasswordScreen
import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import my.android.passwordmanagerapp.data.Password
import my.android.passwordmanagerapp.data.PasswordDatabase
import my.android.passwordmanagerapp.data.PasswordRepository
import my.android.passwordmanagerapp.screens.PasswordDetailsScreen
import my.android.passwordmanagerapp.ui.theme.PasswordManagerAppTheme
import my.android.passwordmanagerapp.viewmodel.PasswordViewModel
import my.android.passwordmanagerapp.viewmodel.PasswordViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasswordManagerAppTheme {
                val database = PasswordDatabase.getDatabase(applicationContext)
                val repository = PasswordRepository(database.passwordDao())
                val viewModelFactory = PasswordViewModelFactory(repository)
                val passwordViewModel: PasswordViewModel = ViewModelProvider(this, viewModelFactory).get(PasswordViewModel::class.java)

                PasswordManagerApp(passwordViewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordManagerApp(viewModel: PasswordViewModel) {
        var currentScreen by remember { mutableStateOf("home") }
        var selectedPassword by remember { mutableStateOf<Password?>(null) }
        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                Column {TopAppBar(
                    title = { Text("Password Manager") }
                )
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                }

                },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedPassword = null
                        currentScreen = "addEdit"
                        showBottomSheet = true
                    },
                   containerColor = Color(0xFF3F7DE3),
                    contentColor = Color.White


                ){Icon(Icons.Filled.Add, "Add Account Floating Button")}
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                HomeScreen(
                    passwords = viewModel.passwords,
                    onAddClicked = {
                        selectedPassword = null
                        currentScreen = "addEdit"
                        showBottomSheet = true
                    },
                    onEditClicked = {
                        selectedPassword = it
                        currentScreen = "details"
                        showBottomSheet = true
                    },
                    onDeleteClicked = {
                        viewModel.deletePassword(it)
                    }
                )

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        when (currentScreen) {
                            "addEdit" -> AddEditPasswordScreen(
                                password = selectedPassword,
                                onSaveClicked = {
                                    if (it.id == 0) viewModel.addPassword(it)
                                    else viewModel.updatePassword(it)
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        showBottomSheet = false
                                    }
                                    currentScreen = "home"
                                }
                            )
                            "details" -> selectedPassword?.let {
                                PasswordDetailsScreen(
                                    password = it,
                                    onEditClicked = {
                                        currentScreen = "addEdit"
                                            showBottomSheet = true

                                    },
                                    onDeleteClicked = {
                                        viewModel.deletePassword(it)
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            showBottomSheet = false
                                        }
                                        currentScreen = "home"
                                    }
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
