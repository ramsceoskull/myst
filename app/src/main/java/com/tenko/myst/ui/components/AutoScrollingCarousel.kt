package com.tenko.myst.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tenko.myst.R
import kotlinx.coroutines.delay

val carouselImages = listOf(
    R.drawable.mujer0,
    R.drawable.mujer1,
    R.drawable.mujer2,
    R.drawable.mujer3,
    R.drawable.mujer4,
    R.drawable.mujer5,
    R.drawable.mujer6,
)

@Composable
fun AutoScrollingCarousel(scrollDelay: Long = 2000L) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = Int.MAX_VALUE / 2
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(scrollDelay)

            listState.animateScrollToItem(
                listState.firstVisibleItemIndex + 1
            )
        }
    }

    LazyRow(
        state = listState,
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 34.dp)
    ) {
        items( count = Int.MAX_VALUE ) { index ->
            val image = carouselImages[index % carouselImages.size]

            Image(
                painter = painterResource(id = image),
                contentDescription = "Carrusel de usuarias",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 120.dp, height = 180.dp)
                    .clip(RoundedCornerShape(20.dp))
//                    .rotate(if (index % 2 == 0) -8f else 8f)
            )
        }
    }
}