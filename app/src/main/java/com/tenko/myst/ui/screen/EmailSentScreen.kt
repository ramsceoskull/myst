package com.tenko.myst.ui.screen

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenko.myst.R
import com.tenko.myst.ui.theme.Monserrat
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.StarsLove
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun EmailSentScreen(
    title: String,
    description: String,
    actionLabel: String,
    onClick: () -> Unit,
    onResendClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoSection()

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        MailIconCircle(visible)

        Spacer(modifier = Modifier.height(40.dp))

        val offset by animateDpAsState(
            targetValue = if (visible) 0.dp else 40.dp,
            animationSpec = tween(1000)
        )
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(1500)
        )
        Column (
            modifier = Modifier
                .offset(y = offset)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = PompAndPower),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            ResendEmail(onResendClick)
        }
    }
}

@Composable
fun LogoSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            contentDescription = "Tenko Avatar",
            painter = painterResource(R.drawable.tenko_avatar),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Myst",
            color = Tekhelet,
            fontSize = 60.sp,
            fontFamily = StarsLove,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = 10.dp)
        )
    }
}

@Composable
fun MailIconCircle(visible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = tween(2000, easing = EaseOutBack)
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(Color(0xFFEAEAEA)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.envelope_regular_full),
            contentDescription = "Mail Icon",
            tint = PompAndPower,
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun ResendEmail(onResendClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        append("¿No recibiste el correo? ")

        pushStringAnnotation(tag = "resend", annotation = "resend")
        withStyle(
            SpanStyle(
                color = PompAndPower,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Reenviar") }
        pop()
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Monserrat,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                if (it.tag == "resend") {
                    onResendClick()
                }
            }
        }
    )
}