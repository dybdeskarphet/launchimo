package com.ahmetardakavakci.launchimo

import android.app.Activity
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ahmetardakavakci.launchimo.ui.theme.LaunchimoTheme
import com.ahmetardakavakci.launchimo.view.*

var thumbColor: Color = Color(0xFF2C2C2C)
var uncheckedThumbColor: Color = Color(0xFF2C2C2C)
var settingsBackground: Color = Color(0xFF262626)
var descTextColor: Color = Color.LightGray

@Composable
fun SettingsScreen(navController: NavHostController) {

    // Switch mutables
    val darkMode = remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", true)) }
    val hideSettings = remember { mutableStateOf(sharedPreferences.getBoolean("hideSettings", false)) }


    // Context
    val activity = LocalContext.current as Activity
    val intent = activity.intent

    checkDarkMode()

    if(darkMode.value) {
        w.navigationBarColor = android.graphics.Color.DKGRAY
        w.statusBarColor = android.graphics.Color.DKGRAY
        settingsBackground = Color(0xFF262626)
        thumbColor = Color(0xFF262626)
        uncheckedThumbColor = Color.LightGray
        descTextColor = Color.LightGray
    } else {
        w.navigationBarColor = android.graphics.Color.LTGRAY
        w.statusBarColor = android.graphics.Color.LTGRAY
        settingsBackground = Color(0xFFFFFFFF)
        thumbColor = Color.LightGray
        uncheckedThumbColor = Color.DarkGray
        descTextColor = Color.DarkGray
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
                        .padding(start = 15.dp, end = 15.dp, top = 20.dp, bottom = 15.dp)
                        .size(24.dp),
                    tint = accentColor
                )
            }

            SettingsSwitch("Dark mode","Makes the app dark", "darkMode", darkMode, navController)
            SettingsSwitch("Transparent Settings icon", "Makes the settings icon transparent. You can still click it but the icon will not be visible.", "hideSettings", hideSettings, navController)
            SettingsText(text = "Restart launcher") {
                activity.finish()
                activity.startActivity(intent)
            }

        }
    }
}

@Composable
fun SettingsText(text: String, textClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(
                start = 15.dp,
                end = 15.dp,
                bottom = 15.dp)
            )
            .clip(rounded)
            .background(colorItemBackground)
    ) {
        Text(
            modifier = Modifier
                .clickable { textClicked() }
                .fillMaxWidth()
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
            ,text = text,

            color = textColor,
            fontSize = 20.sp
        )
    }
}

@Composable
fun SettingsSwitch(text: String, descText: String, sharedKey: String, checkedState: MutableState<Boolean>, navController: NavHostController) {
    Box(Modifier.padding(PaddingValues(start = 15.dp, end = 15.dp, bottom = 15.dp))) {
        Row(
            modifier = Modifier
                .clip(rounded)
                .background(colorItemBackground)
            ,horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(7.8f)
                    .fillMaxWidth()
                    .padding(PaddingValues(start = 17.dp, top = 17.dp, bottom = 17.dp))
            ) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = descText,
                    color = descTextColor,
                    fontSize = 14.sp
                )
            }

            Switch(
                modifier = Modifier
                    .weight(2.2f)
                    .fillMaxWidth()
                    .padding(end = 7.dp)
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
                    if (text == "Dark mode"){
                        navController.navigate("main")
                        navController.navigate("settings")
                    }
                }
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    LaunchimoTheme {
        Column() {
            SettingsSwitch("Dark mode", "Lorem ipsum dolor sit amet aşsldfjşaslfjdaşlsfdaşsldfsaflşasşfafdşkasfdşlasdfljkafadaşsldkfjaşksdfaşsdfaşsldkfjasşlfjasfdklasfdaslkdfjaşldfkjaskfdladfklasdfjasklşdfjasfdk", "darkMode", remember { mutableStateOf(true) }, navController = rememberNavController())
            SettingsSwitch("Dark mode", "Lorem ipsum dolor sit amet aşsldfjşaslfjdaşlsfdaşsldfsaflşasşfafdşkasfdşlasdfljkafadaşsldkfjaşksdfaşsdf", "darkMode", remember { mutableStateOf(true) }, navController = rememberNavController())
            SettingsText(text = "Hey") {

            }
        }
    }
}
