package com.ahmetardakavakci.launchimo.view

import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ahmetardakavakci.launchimo.R
import com.ahmetardakavakci.launchimo.model.App
import com.ahmetardakavakci.launchimo.ui.theme.LaunchimoTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import java.util.*
import kotlin.math.roundToInt

// Package manager
private lateinit var pm: PackageManager

// Lists
private lateinit var appsList: List<App>
private lateinit var appsListUnsorted: ArrayList<App>
private lateinit var allApps: List<ResolveInfo>

// Alphas
var listBackgroundAlpha: Float = 0.9f // (0-255) Approximately 0.9
var appBackgroundAlpha: Float = 1.0f // (0-255) Approximately 0.9
var settingsIconAlpha: Float = 0f

// Boolean
var hideIcons = false

// Colors and shapes
val rounded = RoundedCornerShape(30.dp)
var colorBackground = Color(0xF2262626)
var colorItemBackground = Color(0xFF404040)
var dropdownBackground = Color(0xFF404040)
var dropdownTextColor = Color.White
var textColor = Color.White
var accentColor = Color(0xFFC5CAE9)
private var searchbarColor = Color(0xF2262626)

// Color related
lateinit var w: Window

// Shared preferences
lateinit var sharedPreferences: SharedPreferences
lateinit var editor: SharedPreferences.Editor

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent for list of all apps
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        // Get all the apps from package manager
        pm = this.packageManager
        allApps = pm.queryIntentActivities(intent, 0)

        // Shared preferences
        sharedPreferences = getSharedPreferences("com.ahmetardakavakci.launchimo", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Window variable for coloring
        w = window

        // Lists
        appsListUnsorted = arrayListOf()
        appsList = listOf()

        for(info in allApps) {

            val app = App(
                info.loadLabel(pm).toString(),
                info.activityInfo.packageName,
                info.activityInfo.loadIcon(pm),
                pm.getLaunchIntentForPackage(info.activityInfo.packageName)!!
            )

            appsListUnsorted.add(app)
        }

        appsList = appsListUnsorted.sortedBy { it.label }

        setContent {
            val navController = rememberNavController()
            LaunchimoTheme {
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("settings") {
                        SettingsScreen(navController)
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen(navController: NavHostController) {

    checkSettings()
    checkDarkMode()

    var searchInput by remember { mutableStateOf(TextFieldValue())}
    var expanded by remember { mutableStateOf(false) }

        Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column (
            modifier = Modifier
                .padding(rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = true,
                    applyBottom = true))
            ) {
            // App list
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(rounded)
                    .fillMaxWidth()
                    .weight(9f)
                ,color = colorBackground
            ) {
                AppList(appsList, searchInput)
            }

            DropdownMenu(
                modifier = Modifier
                    .background(colorItemBackground)
                ,expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Text(
                        text = "App Info",
                        color = textColor
                    )
                }
            }

            // Searchbar
            Box(Modifier.weight(1f)
            ) {
                TextField(
                    modifier = Modifier
                        .padding(
                            PaddingValues(
                                top = 5.dp,
                                bottom = 10.dp,
                                start = 10.dp,
                                end = 10.dp
                            )
                        )
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .clip(rounded)
                        .align(Alignment.Center)
                    ,colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = searchbarColor,
                        cursorColor = textColor,
                        textColor = textColor,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(24.dp)
                            ,tint = accentColor
                        )
                    },
                    trailingIcon = {
                        when {
                            searchInput.text.isNotEmpty() -> {
                                Box(modifier = Modifier.clickable {
                                    searchInput = TextFieldValue("")
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "",
                                        tint = accentColor,
                                        modifier = Modifier
                                            .padding(15.dp)
                                            .size(24.dp)
                                    )
                                }
                            }

                            else -> {
                                Box(modifier = Modifier.clickable {
                                    navController.navigate("settings")
                                }) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "",
                                        tint = accentColor,
                                        modifier = Modifier
                                            .padding(15.dp)
                                            .size(24.dp)
                                            .alpha(settingsIconAlpha)
                                    )
                                }
                            }
                        }
                    },
                    maxLines = 1,
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                    })

            }

        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppLine(context: Context, label: String, icon: Drawable, intent: Intent, pkgname: String) {

    var expanded by remember { mutableStateOf(false) }

    // Details Settings Intent
    val toSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val settingsPackageUri = Uri.fromParts("package", pkgname, null)
    toSettings.data = settingsPackageUri

    // Uninstall Intent
    val uninstallIntent = Intent(Intent.ACTION_DELETE)
    val uninstallPackageUri = Uri.parse("package:$pkgname")
    uninstallIntent.data = uninstallPackageUri

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
        backgroundColor = colorItemBackground,
        elevation = 0.dp,
    ) {
        Box(modifier = Modifier.combinedClickable(
            onClick = {
                context.startActivity(intent)
            },
            onLongClick = {
                expanded = !expanded
            }
        )) {
            Row(
                modifier = Modifier.padding(PaddingValues(vertical = 8.dp, horizontal = 8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (!hideIcons) {
                    Image(
                        modifier = Modifier
                            .padding(PaddingValues(start = 5.dp))
                            .width(40.dp)
                            .height(40.dp), painter = rememberDrawablePainter(drawable = icon),
                        contentDescription = label
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = label,
                    color = textColor,
                    fontSize = 20.sp
                )

                DropdownMenu(
                    modifier = Modifier
                        .background(colorItemBackground),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            context.startActivity(toSettings)
                            expanded = !expanded
                        }
                    ) {
                        Text(
                            text = "App Info",
                            color = textColor
                        )
                    }

                    DropdownMenuItem(
                        onClick = {
                            context.startActivity(uninstallIntent)
                            expanded = !expanded
                        }
                    ) {
                        Text(
                            text = "Uninstall",
                            color = textColor
                        )
                    }
                }
            }
        }

    }

}

@Composable
fun AppList(apps: List<App>, state: TextFieldValue) {
    val context = LocalContext.current
    var filteredApps: List<App>

    // App List
    LazyColumn(
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 0.dp,
            bottom = 8.dp
        ),
        reverseLayout = true
    ) {

        val searchedText = state.text
        filteredApps = if(searchedText.isEmpty()) {
            apps
        } else {
            val resultList = ArrayList<App>()
            for (app in apps) {
                if (app.label.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(app)
                }
            }

            resultList
        }

        items(
            count = filteredApps.size,
            key = {
                  it.inc()
            },
            itemContent = { app ->
                AppLine(
                    context = context,
                    label = filteredApps[app].label,
                    icon = filteredApps[app].icon,
                    intent = filteredApps[app].intent,
                    pkgname = filteredApps[app].pkgName
                )
                Spacer(modifier = Modifier.height(10.dp))
        })
    }

}

fun checkDarkMode() {

    w.navigationBarColor = android.graphics.Color.TRANSPARENT
    w.statusBarColor = android.graphics.Color.TRANSPARENT

    when(sharedPreferences.getBoolean("darkMode", true)) {
        true -> {
            colorBackground = Color(red = 38, blue = 38, green = 38, alpha = (listBackgroundAlpha*255).roundToInt())
            colorItemBackground = Color(red = 64, blue = 64, green = 64, alpha = (appBackgroundAlpha*255).roundToInt())
            dropdownBackground = Color(red = 64, blue = 64, green = 64, alpha = 255)
            dropdownTextColor = Color.White
            accentColor = Color(0xFFC5CAE9)
            searchbarColor = Color(0xF2262626)
            textColor = Color.White
        }
        false -> {
            colorBackground = Color(red = 255, blue = 255, green = 255, alpha = (listBackgroundAlpha*255).roundToInt())
            colorItemBackground = Color(red = 232, green = 234, blue = 246, alpha = (appBackgroundAlpha*255).roundToInt())
            dropdownBackground = Color(red = 232, green = 234, blue = 246, alpha = 255)
            dropdownTextColor = Color.Black
            accentColor = Color(0xFF3949AB)
            searchbarColor = Color(red = 232, green = 234, blue = 246, alpha = 255)
            textColor = if(listBackgroundAlpha < 0.5 && appBackgroundAlpha < 0.5) {
                Color.White
            } else {
                Color.Black
            }

        }
    }
}

fun checkSettings() {
    settingsIconAlpha = when(sharedPreferences.getBoolean("hideSettings", false)) {
        true -> 0f
        false -> 1f
    }

    hideIcons = when(sharedPreferences.getBoolean("hideIcons", false)) {
        true -> true
        false -> false
    }

    listBackgroundAlpha = sharedPreferences.getFloat("listBackgroundAlpha",  0.9f)
    appBackgroundAlpha = sharedPreferences.getFloat("appBackgroundAlpha",  1.0f)

}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    LaunchimoTheme {
        AppLine(
            context = LocalContext.current,
            label = "App name",
            icon = ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_launcher_foreground)!!,
            intent = Intent(),
            pkgname = "none"
        )
    }
}