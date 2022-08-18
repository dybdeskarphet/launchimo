package com.ahmetardakavakci.launchimo.view

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ahmetardakavakci.launchimo.ui.theme.LaunchimoTheme

var thumbColor: Color = Color(0xFF2C2C2C)
var uncheckedThumbColor: Color = Color(0xFF2C2C2C)
var settingsBackground: Color = Color(0xFF262626)
var settingsItemBackground: Color = Color(0xFF404040)
var settingsTextColor: Color = Color.White
var descTextColor: Color = Color.LightGray

@Composable
fun SettingsScreen(navController: NavHostController) {

    // Switch mutables
    val darkMode = remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", true)) }
    val hideSettings = remember { mutableStateOf(sharedPreferences.getBoolean("hideSettings", false)) }
    val hideIcons = remember { mutableStateOf(sharedPreferences.getBoolean("hideIcons", false)) }

    val listBackgroundAlpha = remember { mutableStateOf(sharedPreferences.getFloat("listBackgroundAlpha", 0.9f)) }
    val appBackgroundAlpha = remember { mutableStateOf(sharedPreferences.getFloat("appBackgroundAlpha", 1.0f)) }

    // Context
    val activity = LocalContext.current as Activity
    val intent = activity.intent

    checkDarkMode()

    if(darkMode.value) {
        w.navigationBarColor = android.graphics.Color.DKGRAY
        w.statusBarColor = android.graphics.Color.DKGRAY
        settingsBackground = Color(0xFF262626)
        settingsItemBackground = Color(0xFF404040)
        settingsTextColor = Color.White
        thumbColor = Color(0xFF262626)
        uncheckedThumbColor = Color.LightGray
        descTextColor = Color.LightGray
    } else {
        w.navigationBarColor = android.graphics.Color.LTGRAY
        w.statusBarColor = android.graphics.Color.LTGRAY
        settingsBackground = Color(0xFFFFFFFF)
        settingsItemBackground = Color(0xFFE8EAF6)
        settingsTextColor = Color.Black
        thumbColor = Color.LightGray
        uncheckedThumbColor = Color.DarkGray
        descTextColor = Color.DarkGray
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = settingsBackground,
    ) {
        Column {

            // Back button
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

            // Setting options
            SettingsSwitch("Dark mode","Makes the app dark", "darkMode", darkMode, navController)
            SettingsSwitch("Transparent Settings icon", "Makes the settings icon transparent. You can still click it but the icon will not be visible.", "hideSettings", hideSettings, navController)
            SettingsSwitch("Hide app icons", "Only show the app name on the list", "hideIcons", hideIcons, navController)
            SettingsAlphaDropdown("List background alpha","Transparency value for app list background","listBackgroundAlpha", listBackgroundAlpha)
            SettingsAlphaDropdown("App background alpha","Transparency value for background of every app in the list","appBackgroundAlpha", appBackgroundAlpha)
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
            .background(settingsItemBackground)
    ) {
        Text(
            modifier = Modifier
                .clickable { textClicked() }
                .fillMaxWidth()
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
            ,text = text,

            color = settingsTextColor,
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
                .background(settingsItemBackground)
                .fillMaxWidth()
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
                    color = settingsTextColor,
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

@Composable
fun SettingsAlphaDropdown(text: String, descText: String, sharedKey: String, lastValue: MutableState<Float>) {
    var expanded by remember { mutableStateOf(false) }
    var lastDropdownValue by remember { mutableStateOf(lastValue.value.toString()) }

    Box(Modifier
        .padding(PaddingValues(start = 15.dp, end = 15.dp, bottom = 15.dp))
        .clip(rounded)
        .fillMaxWidth()
        .background(settingsItemBackground)
    ) {
        Row(
            modifier = Modifier.padding(PaddingValues(start = 17.dp, top = 17.dp, bottom = 17.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(6.7f)) {
                Text(
                    text = text,
                    color = settingsTextColor,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = descText,
                    color = descTextColor,
                    fontSize = 14.sp
                )
            }

            Box(modifier = Modifier
                .weight(3.3f)
                .clickable {
                    expanded = !expanded
                }
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                        ,value = lastDropdownValue,
                        readOnly = true,
                        enabled = false,
                        textStyle = TextStyle(fontSize = 20.sp),
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(24.dp)
                                ,tint = accentColor
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = settingsTextColor,
                            disabledTextColor = settingsTextColor,
                            backgroundColor = Color.Transparent,
                            cursorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        onValueChange = { lastDropdownValue = it }
                    )


                    DropdownMenu(
                        modifier = Modifier.background(settingsItemBackground),
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        for (i in 0..9) {
                            DropdownMenuItem(onClick = {
                                expanded = !expanded
                                lastDropdownValue = "0.$i"
                                editor.putFloat(sharedKey, ("0.${i}").toFloat())
                                editor.commit()
                            }) {
                                Text(
                                    text = "0.${i}",
                                    color = settingsTextColor
                                )
                            }
                        }

                        DropdownMenuItem(onClick = {
                            expanded = !expanded
                            lastDropdownValue = "1.0"
                            editor.putFloat(sharedKey, 1.0f)
                            editor.commit()
                        }) {
                            Text(
                                text = "1.0",
                                color = settingsTextColor
                            )
                        }
                    }

                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    LaunchimoTheme {
        Column {
        }
    }
}
