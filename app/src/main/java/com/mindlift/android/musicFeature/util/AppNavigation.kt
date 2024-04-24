package com.example.music.util

//@Composable
//fun AppNavigation(navController: NavHostController, songsViewModel: SongsViewModel) {
//    NavHost(navController = navController, startDestination = "MusicScreenComposable") {
//        composable("MusicScreenComposable") {
//            MusicScreenComposable(navController)
//        }
//        composable("genreScreen") {
//            GenreScreen(navController, songsViewModel)
//        }
//        composable(
//            route = "songListScreen/{genreId}/{genreName}/{coverUrl}",
//            arguments = listOf(
//                navArgument("genreId") { type = NavType.StringType },
//                navArgument("genreName") { type = NavType.StringType },
//                navArgument("coverUrl") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            SongsListScreen(
//                navController = navController,
//                genreId = backStackEntry.arguments?.getString("genreId") ?: "",
//                genreName = Uri.decode(backStackEntry.arguments?.getString("genreName") ?: ""),
//                coverUrl = Uri.decode(backStackEntry.arguments?.getString("coverUrl") ?: ""),
//                viewModel = viewModel()
//            )
//        }
//        composable(
//            route = "playerScreen/{songUrl}/{songTitle}/{songArtist}/{coverUrl}",
//            arguments = listOf(
//                navArgument("songUrl") { type = NavType.StringType },
//                navArgument("songTitle") { type = NavType.StringType },
//                navArgument("songArtist") {
//                    type = NavType.StringType
//                    defaultValue = "noArtist"
//                    nullable = true
//                },
//                navArgument("coverUrl") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            PlayerScreen(
//                songUrl = backStackEntry.arguments?.getString("songUrl") ?: "",
//                songTitle = backStackEntry.arguments?.getString("songTitle") ?: "Unknown Title",
//                songArtist = backStackEntry.arguments?.getString("songArtist"), // Can be null
//                coverUrl = backStackEntry.arguments?.getString("coverUrl") ?: ""
//            )
//        }
//    }
//}