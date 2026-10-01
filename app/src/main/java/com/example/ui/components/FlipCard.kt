package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipCardBorderDark
import com.example.ui.theme.FlipCardDark
import com.example.ui.theme.FlipCardDarkBottom
import com.example.ui.theme.FlipDividerDark
import com.example.ui.theme.FlipHinge
import com.example.ui.theme.FlipTextLight
import com.example.util.SoundManager

/**
 * Split-Flap mechanical flip card for a single digit or text string (e.g. "0"-"9", "AM", "PM")
 */
@Composable
fun FlipCard(
    value: String,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 64.dp,
    cardHeight: Dp = 92.dp,
    fontSize: TextUnit = 54.sp,
    isDarkMode: Boolean = true
) {
    var previousValue by remember { mutableStateOf(value) }
    var currentValue by remember { mutableStateOf(value) }
    val flipAnimation = remember { Animatable(0f) }

    LaunchedEffect(value) {
        if (value != currentValue) {
            previousValue = currentValue
            currentValue = value
            SoundManager.playFlipClick()
            flipAnimation.snapTo(0f)
            flipAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
        }
    }

    val rotation = flipAnimation.value * 180f
    val isFirstHalf = rotation <= 90f

    val topBg = if (isDarkMode) FlipCardDark else Color(0xFFFFFFFF)
    val bottomBg = if (isDarkMode) FlipCardDarkBottom else Color(0xFFF0F0F3)
    val textColor = if (isDarkMode) FlipTextLight else Color(0xFF18181B)
    val borderColor = if (isDarkMode) FlipCardBorderDark else Color(0xFFD1D1D8)
    val dividerColor = if (isDarkMode) FlipDividerDark else Color(0xFFBFBFCA)

    Box(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.Transparent, RoundedCornerShape(8.dp))
    ) {
        // Base Lower Half (Shows CURRENT value's bottom half)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(cardHeight / 2)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(bottomBg)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                )
        ) {
            HalfCardText(
                text = currentValue,
                fontSize = fontSize,
                textColor = textColor,
                isTopHalf = false,
                cardHeight = cardHeight
            )
        }

        // Base Upper Half (Shows CURRENT value's top half)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(cardHeight / 2)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(topBg)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        ) {
            HalfCardText(
                text = currentValue,
                fontSize = fontSize,
                textColor = textColor,
                isTopHalf = true,
                cardHeight = cardHeight
            )
        }

        // Flipping Upper Flap (0..90 deg: PREVIOUS value top half)
        if (isFirstHalf) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(cardHeight / 2)
                    .graphicsLayer {
                        rotationX = -rotation
                        cameraDistance = 14f * density
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(topBg)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            ) {
                HalfCardText(
                    text = previousValue,
                    fontSize = fontSize,
                    textColor = textColor,
                    isTopHalf = true,
                    cardHeight = cardHeight
                )
                // Dynamic shadow over flipping face
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = (rotation / 90f) * 0.45f))
                )
            }
        } else {
            // Flipping Lower Flap (90..180 deg: CURRENT value bottom half rotated down)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(cardHeight / 2)
                    .graphicsLayer {
                        rotationX = 180f - rotation
                        cameraDistance = 14f * density
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    }
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(bottomBg)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
            ) {
                HalfCardText(
                    text = currentValue,
                    fontSize = fontSize,
                    textColor = textColor,
                    isTopHalf = false,
                    cardHeight = cardHeight
                )
                // Dynamic shadow as it lands
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = ((180f - rotation) / 90f) * 0.45f))
                )
            }
        }

        // Horizontal Split Seam line
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(1.5.dp)
                .background(dividerColor)
        )

        // Left Hinge Notch
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(width = 4.dp, height = 7.dp)
                .background(FlipHinge, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
        )

        // Right Hinge Notch
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 4.dp, height = 7.dp)
                .background(FlipHinge, RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
        )
    }
}

/**
 * Text rendered clipped to either upper half or lower half
 */
@Composable
private fun HalfCardText(
    text: String,
    fontSize: TextUnit,
    textColor: Color,
    isTopHalf: Boolean,
    cardHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight / 2)
            .drawWithContent {
                clipPath(Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height))
                }) {
                    this@drawWithContent.drawContent()
                }
            }
    ) {
        // Centered inside an imaginary full-height box positioned appropriately
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .graphicsLayer {
                    translationY = if (isTopHalf) 0f else -size.height / 2f
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                lineHeight = fontSize
            )
        }
    }
}

/**
 * Digit Pair with 2 flip cards side by side (e.g. "08")
 */
@Composable
fun FlipDigitPair(
    value: String, // Expecting 2 digits e.g. "09"
    modifier: Modifier = Modifier,
    cardWidth: Dp = 58.dp,
    cardHeight: Dp = 86.dp,
    fontSize: TextUnit = 50.sp,
    isDarkMode: Boolean = true
) {
    val d1 = if (value.isNotEmpty()) value[0].toString() else "0"
    val d2 = if (value.length > 1) value[1].toString() else "0"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlipCard(
            value = d1,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            fontSize = fontSize,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.width(3.dp))
        FlipCard(
            value = d2,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            fontSize = fontSize,
            isDarkMode = isDarkMode
        )
    }
}

/**
 * Retro Flashing Separator Colon
 */
@Composable
fun FlipColon(
    isDarkMode: Boolean = true,
    dotSize: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val dotColor = if (isDarkMode) FlipTextLight else Color(0xFF18181B)
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .size(dotSize)
                .background(dotColor, CircleShape)
        )
    }
}
