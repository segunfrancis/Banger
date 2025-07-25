package com.segunfrancis.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.segunfrancis.theme.LightBackground
import com.segunfrancis.theme.R
import com.segunfrancis.theme.WallpaperDownloaderTheme
import com.segunfrancis.theme.components.AppToolbar

@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit, onMenuActionClick: () -> Unit) {
    HomeContent(
        onCategoryClick = onCategoryClick,
        onMenuActionClick = onMenuActionClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onCategoryClick: (String) -> Unit,
    onMenuActionClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppToolbar(title = "Wallpapers", actionIcon = R.drawable.ic_settings, onActionClick = {
            onMenuActionClick()
        })
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryCard(title = category.title, image = category.image, onClick = {
                    onCategoryClick(category.value)
                })
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CategoryCard(
    @DrawableRes image: Int = R.drawable.il_cars,
    title: String = "Cars",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(200.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.BottomStart) {
            Image(
                painter = painterResource(image),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = LightBackground
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WallpaperDownloaderTheme {
        HomeContent(
            onCategoryClick = {},
            onMenuActionClick = {}
        )
    }
}

data class Category(val title: String, @DrawableRes val image: Int, val value: String)

val categories = listOf(
    Category(title = "Abstract", image = R.drawable.il_abstract, value = "abstract"),
    Category(title = "Nature", image = R.drawable.il_nature, value = "nature"),
    Category(title = "Animals", image = R.drawable.il_animals, value = "animals"),
    Category(title = "Cars", image = R.drawable.il_cars, value = "cars"),
    Category(title = "Space", image = R.drawable.il_space, value = "space"),
    Category(title = "City", image = R.drawable.il_city, value = "city"),
    Category(title = "Food", image = R.drawable.il_food, value = "food"),
    Category(title = "Music", image = R.drawable.il_music, value = "music"),
    Category(title = "Sports", image = R.drawable.il_sports, value = "sports"),
    Category(title = "Technology", image = R.drawable.il_technology, value = "technology"),
    Category(title = "Fashion", image = R.drawable.il_fashion, value = "fashion"),
    Category(title = "Travel", image = R.drawable.il_travel, value = "travel"),
)
