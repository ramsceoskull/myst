package com.tenko.myst.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tenko.myst.R

val Monserrat = FontFamily(
    fonts = listOf(
        Font(R.font.montserrat_black, FontWeight.Black),
        Font(R.font.montserrat_black_italic),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_bold_italic),
        Font(R.font.montserrat_extra_bold, FontWeight.ExtraBold),
        Font(R.font.montserrat_extra_bold_italic),
        Font(R.font.montserrat_extra_light, FontWeight.ExtraLight),
        Font(R.font.montserrat_extra_light_italic),
        Font(R.font.montserrat_italic),
        Font(R.font.montserrat_light, FontWeight.Light),
        Font(R.font.montserrat_light_italic),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_medium_italic),
        Font(R.font.montserrat_regular),
        Font(R.font.montserrat_semi_bold, FontWeight.SemiBold),
        Font(R.font.montserrat_semi_bold_italic),
        Font(R.font.montserrat_thin, FontWeight.Thin),
        Font(R.font.montserrat_thin_italic)
    )
)

val StarsLove = FontFamily(
    Font(R.font.stars_love, FontWeight.Normal)
)
val StarsLoveBottomHeavy = FontFamily(
    Font(R.font.stars_love_bottom_heavy, FontWeight.Normal)
)

val Typography = Typography(
    bodyLarge = TextStyle(
//        color = RaisinBlack,
        fontFamily = Monserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Monserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)