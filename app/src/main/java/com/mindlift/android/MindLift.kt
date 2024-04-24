package com.mindlift.android

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.mindlift.android.diaryFeature.DiaryScreen
import com.mindlift.android.introScreens.EntryScreen
import com.mindlift.android.introScreens.homeScreen
import com.mindlift.android.introScreens.UserLoginScreen
import com.mindlift.android.introScreens.UserSignUpScreen
import com.mindlift.android.mapFeature.mapScreen
import com.mindlift.android.musicFeature.MusicScreen
import com.mindlift.android.yogaFeature.CameraScreen
import com.mindlift.android.yogaFeature.ScrollablePoses
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindlift.android.diaryFeature.ViewDiaryScreen
import com.mindlift.android.yogaFeature.YogaPoseViewModel
import com.example.music.model.MusicNavViewModel
import com.example.music.model.SongsViewModel
import com.mindlift.android.musicFeature.ui.PlayerScreen
import com.mindlift.android.musicFeature.ui.SongsListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MindLiftApp(navController: NavHostController){

    val userViewModel: UserViewModel = viewModel()
    val yogaPoseViewModel: YogaPoseViewModel = viewModel()
    val musicNavViewModel: MusicNavViewModel = viewModel()
    val musicViewModel: SongsViewModel = viewModel()

    NavHost(navController = navController, startDestination="entryscreen"){
        composable(route = "entryscreen"){
            // call the entry screen
            EntryScreen(){
                navController.navigate("userloginscreen")
            }
        }

        composable(route = "userloginscreen"){
            // call the user login screen
            UserLoginScreen({
                    navController.navigate("usersignupscreen")
                }, {
                    navController.navigate("userloginscreen")
                }, {
                    navController.navigate("homescreen")
                }, {
                    navController.navigate("entryscreen")
                }, userViewModel = userViewModel)
        }

        composable(route = "usersignupscreen"){
            // call the user signup screen
            UserSignUpScreen ({
                navController.navigate("userLoginScreen")
            },
                {
                    navController.navigate("entryscreen")
                })
        }

        composable(route = "homescreen"){
            // go to the home screen
            homeScreen({
                navController.navigate("musicscreen")
            }, {
                navController.navigate("diaryscreen")
            },{
                navController.navigate("yogascreen")
            },{
                navController.navigate("mapscreen")
            },{
                navController.navigate("userloginscreen")
            }, userViewModel = userViewModel)
        }

        composable(route = "posedetection"){
            // entry point for yoga pose detection
            CameraScreen(yogaPoseViewModel, userViewModel, {
                navController.navigate("homescreen")
            },{
                navController.navigate("yogascreen")
            })
        }

        composable(route = "yogascreen"){
            ScrollablePoses(userViewModel, yogaPoseViewModel) {
                navController.navigate("posedetection")
            }
        }

        composable(route = "mapscreen"){
            // call the map screen
            mapScreen(){
                navController.navigate("homescreen")
            }
        }

        composable(route = "diaryscreen"){
            // call the diary screen
            DiaryScreen({
                navController.navigate("homescreen")
            }, {
               navController.navigate("viewdiaryscreen")
            },userViewModel = userViewModel)
        }

        composable(route = "viewdiaryscreen"){
            // call the diary screen
            ViewDiaryScreen({
                navController.navigate("homescreen")
            }, {
                navController.navigate("diaryscreen")
            }, userViewModel = userViewModel)
        }

        // music part
        composable(route = "musicscreen") {
            MusicScreen(userViewModel, musicNavViewModel, {
                navController.navigate("homescreen")
            },{
                navController.navigate("musicListScreen")
            },{
                navController.navigate("musicPlayerScreen")
            })
        }

        composable(route = "musicListScreen"){
            SongsListScreen(navController,
                musicViewModel,
                musicNavViewModel,{
                navController.navigate("musicPlayerScreen")
            },{
              navController.navigate("homescreen")
              },{
                  navController.navigate("musicscreen")
                })
        }

        composable(route = "musicPlayerScreen"){
            PlayerScreen(musicNavViewModel,{
                navController.navigate("musicListScreen")
            },{
                navController.navigate("musicscreen")
            }, {
                navController.navigate("homescreen")
            })
        }

    }
}