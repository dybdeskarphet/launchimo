package com.ahmetardakavakci.launchimo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ahmetardakavakci.launchimo.ui.theme.LaunchimoTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.util.*

private lateinit var pm: PackageManager
private lateinit var appsList: List<App>
private lateinit var appsListUnsorted: ArrayList<App>
private lateinit var allApps: List<ResolveInfo>
private val rounded = RoundedCornerShape(30.dp)
private val colorBackground = Color(0xF2262626)
private val colorItemBackground = Color(0xFF404040)
private val textColor = Color.White
private val accentColor = Color(0xFFEF9A9A)

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pm = this.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        allApps = pm.queryIntentActivities(intent, 0)
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
            LaunchimoTheme {
                    MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {

   var searchInput by remember { mutableStateOf(TextFieldValue())}

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        Column() {
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

            Box(Modifier.weight(1f)
            ) {
                TextField(
                    modifier = Modifier
                        .padding(PaddingValues(top = 5.dp, bottom = 10.dp, start = 10.dp, end = 10.dp))
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .clip(rounded)
                        .align(Alignment.Center)
                    ,colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorBackground,
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
                                .size(24.dp),
                            tint = accentColor
                        )
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

@Composable
fun AppLine(context: Context, label: String, icon: Drawable, intent: Intent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                context.startActivity(intent)
            }
        ,shape = RoundedCornerShape(23.dp),
        backgroundColor = colorItemBackground,
        elevation = 10.dp,

        ) {
        Row(
            modifier = Modifier
                .padding(PaddingValues(vertical = 8.dp, horizontal = 8.dp))
            ,verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(PaddingValues(start = 5.dp))
                    .width(40.dp)
                    .height(40.dp)
                ,painter = rememberDrawablePainter(drawable = icon),
                contentDescription = label
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                color = textColor,
                fontSize = 20.sp
            )
        }

    }
}

@Composable
fun AppList(apps: List<App>, state: TextFieldValue) {
    val context = LocalContext.current
    var filteredApps: List<App>

    // App List
    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp),
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
                    intent = filteredApps[app].intent
                )
                Spacer(modifier = Modifier.height(10.dp))
        })
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LaunchimoTheme {
        AppLine(context = LocalContext.current, label = "App name", icon = ContextCompat.getDrawable(
            LocalContext.current, R.drawable.ic_launcher_background)!!, intent = Intent())
    }
}