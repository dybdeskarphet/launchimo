package com.ahmetardakavakci.launchimo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

var thumbColor: Color = Color(0xFF2C2C2C)
var uncheckedThumbColor: Color = Color(0xFF2C2C2C)

@Composable
fun SettingsScreen(navController: NavHostController) {

    val darkMode = remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }
    var settingsBackground: Color

    checkDarkMode()

    if(darkMode.value) {
        w.navigationBarColor = android.graphics.Color.DKGRAY
        w.statusBarColor = android.graphics.Color.DKGRAY
        settingsBackground = Color(0xFF262626)
        thumbColor = Color.DarkGray
        uncheckedThumbColor = Color.LightGray
    } else {
        w.navigationBarColor = android.graphics.Color.LTGRAY
        w.statusBarColor = android.graphics.Color.LTGRAY
        settingsBackground = Color(0xFFFFFFFF)
        thumbColor = Color.LightGray
        uncheckedThumbColor = Color.DarkGray
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = settingsBackground,
    ) {
        Column {

            Box(
               modifier = Modifier
                   .background(Color.Transparent)
                   .clickable {
                       navController.navigate("main")
                   }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, top = 20.dp, bottom = 5.dp)
                        .size(24.dp),
                    tint = accentColor
                )
            }

            SettingsSwitch("Dark mode", "darkMode", darkMode)
        }
    }
}

@Composable
fun SettingsSwitch(text: String, sharedKey: String, checkedState: MutableState<Boolean>) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(vertical = 10.dp, horizontal = 10.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(PaddingValues(start = 13.dp))
                ,text = text,
                color = textColor,
                fontSize = 20.sp
            )

            Switch(
                modifier = Modifier
                    .padding(PaddingValues(end = 13.dp))
                ,checked = checkedState.value,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = thumbColor,
                    uncheckedThumbColor = uncheckedThumbColor,
                    checkedTrackColor = accentColor
                ),
                onCheckedChange = {
                    checkedState.value = it
                    editor.putBoolean(sharedKey, it)
                    editor.commit()
                }
            )
        }
    }
}