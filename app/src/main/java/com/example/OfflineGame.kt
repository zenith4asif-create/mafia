package com.example

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class GameObstacle(
  var x: Float,
  val width: Float = 36f,
  val height: Float = 55f,
  val color: Color = Color(0xFFF97316)
)

@Composable
fun OfflineCartoonAndGameScreen(
  onRetry: () -> Unit,
  accentColor: Color,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  var isGameModeActive by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = if (isDark) {
            listOf(Color(0xFF0F172A), Color(0xFF090D16))
          } else {
            listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
          }
        )
      )
  ) {
    if (isGameModeActive) {
      OfflineRunnerGame(
        accentColor = accentColor,
        isDark = isDark,
        onCloseGame = { isGameModeActive = false }
      )
    } else {
      OfflineCartoonLanding(
        onStartGame = { isGameModeActive = true },
        onRetry = onRetry,
        accentColor = accentColor,
        isDark = isDark
      )
    }
  }
}

@Composable
fun OfflineCartoonLanding(
  onStartGame: () -> Unit,
  onRetry: () -> Unit,
  accentColor: Color,
  isDark: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Animated Cartoon Character
    AnimatedCartoonAstronaut(
      accentColor = accentColor,
      isDark = isDark,
      modifier = Modifier.size(190.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = stringResource(R.string.offline_title),
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = stringResource(R.string.offline_subtitle),
      style = MaterialTheme.typography.bodyMedium,
      color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Launch Offline Game button
    Button(
      onClick = onStartGame,
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .height(52.dp)
        .testTag("play_offline_game_button"),
      shape = RoundedCornerShape(26.dp),
      colors = ButtonDefaults.buttonColors(containerColor = accentColor)
    ) {
      Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(R.string.play_runner_game),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Retry connection button
    OutlinedButton(
      onClick = onRetry,
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .height(50.dp)
        .testTag("retry_offline_button"),
      shape = RoundedCornerShape(26.dp)
    ) {
      Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(R.string.retry_connection),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun AnimatedCartoonAstronaut(
  accentColor: Color,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "astronaut_float")

  // Floating bobbing animation
  val floatOffset by transition.animateFloat(
    initialValue = -12f,
    targetValue = 12f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float_offset"
  )

  // Radar wave pulsing animation
  val radarPulse by transition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_pulse"
  )

  // Eye blink animation
  val eyeHeightFraction by transition.animateFloat(
    initialValue = 1f,
    targetValue = 0.1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eye_blink"
  )

  Canvas(modifier = modifier) {
    val centerX = size.width / 2f
    val centerY = (size.height / 2f) + floatOffset

    // 1. Radar wave rings when offline searching
    val maxRadius = size.width * 0.46f
    val currentPulseRadius = maxRadius * radarPulse
    val pulseAlpha = (1f - radarPulse).coerceIn(0f, 0.7f)
    drawCircle(
      color = accentColor.copy(alpha = pulseAlpha),
      radius = currentPulseRadius,
      center = Offset(centerX, centerY - 60f),
      style = Stroke(width = 3.5f)
    )

    // 2. Head Helmet (Glass dome)
    val helmetRadius = size.width * 0.22f
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          if (isDark) Color(0xFF334155) else Color(0xFFFFFFFF),
          if (isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
        ),
        center = Offset(centerX - 10f, centerY - 30f),
        radius = helmetRadius
      ),
      radius = helmetRadius,
      center = Offset(centerX, centerY - 20f)
    )

    // 3. Helmet Visor (Dark glossy face screen)
    val visorWidth = helmetRadius * 1.45f
    val visorHeight = helmetRadius * 1.15f
    drawRoundRect(
      color = Color(0xFF0F172A),
      topLeft = Offset(centerX - (visorWidth / 2f), centerY - 32f),
      size = Size(visorWidth, visorHeight),
      cornerRadius = CornerRadius(22f, 22f)
    )

    // Visor Glass Glare Sheen
    drawArc(
      color = Color.White.copy(alpha = 0.35f),
      startAngle = 200f,
      sweepAngle = 60f,
      useCenter = false,
      topLeft = Offset(centerX - (visorWidth / 2f) + 4f, centerY - 30f),
      size = Size(visorWidth - 8f, visorHeight - 8f),
      style = Stroke(width = 5f)
    )

    // 4. Cartoon Glowing Eyes
    val eyeWidth = 14f
    val eyeHeight = (18f * eyeHeightFraction).coerceAtLeast(3f)
    // Left eye
    drawRoundRect(
      color = accentColor,
      topLeft = Offset(centerX - 24f, centerY - 18f),
      size = Size(eyeWidth, eyeHeight),
      cornerRadius = CornerRadius(6f, 6f)
    )
    // Right eye
    drawRoundRect(
      color = accentColor,
      topLeft = Offset(centerX + 10f, centerY - 18f),
      size = Size(eyeWidth, eyeHeight),
      cornerRadius = CornerRadius(6f, 6f)
    )

    // Cute smile in visor
    drawArc(
      color = accentColor.copy(alpha = 0.85f),
      startAngle = 20f,
      sweepAngle = 140f,
      useCenter = false,
      topLeft = Offset(centerX - 12f, centerY - 6f),
      size = Size(24f, 14f),
      style = Stroke(width = 3.5f)
    )

    // 5. Antenna on top of helmet
    drawLine(
      color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
      start = Offset(centerX, centerY - 20f - helmetRadius),
      end = Offset(centerX, centerY - 45f - helmetRadius),
      strokeWidth = 5f
    )
    // Blinking antenna satellite bulb
    drawCircle(
      color = accentColor,
      radius = 8f,
      center = Offset(centerX, centerY - 48f - helmetRadius)
    )

    // 6. Body & Jetpack
    val bodyWidth = 54f
    val bodyHeight = 44f
    drawRoundRect(
      color = if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0),
      topLeft = Offset(centerX - (bodyWidth / 2f), centerY + 14f),
      size = Size(bodyWidth, bodyHeight),
      cornerRadius = CornerRadius(14f, 14f)
    )

    // O2 Chest Emblem
    drawCircle(
      color = accentColor,
      radius = 10f,
      center = Offset(centerX, centerY + 36f)
    )

    // Cute arms
    drawRoundRect(
      color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
      topLeft = Offset(centerX - (bodyWidth / 2f) - 16f, centerY + 20f),
      size = Size(14f, 28f),
      cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
      color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
      topLeft = Offset(centerX + (bodyWidth / 2f) + 2f, centerY + 16f),
      size = Size(14f, 28f),
      cornerRadius = CornerRadius(8f, 8f)
    )
  }
}

@Composable
fun OfflineRunnerGame(
  accentColor: Color,
  isDark: Boolean,
  onCloseGame: () -> Unit
) {
  var isAutoPlay by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var highScore by remember { mutableIntStateOf(0) }

  // Runner player physics
  var playerY by remember { mutableFloatStateOf(0f) }
  var playerVelocityY by remember { mutableFloatStateOf(0f) }
  val gravity = 1.35f
  val jumpStrength = -23f
  val groundBaseline = 0f

  val obstacles = remember { mutableStateListOf<GameObstacle>() }

  // Game loop ticker
  LaunchedEffect(isGameOver) {
    if (isGameOver) return@LaunchedEffect

    var frameCount = 0
    while (!isGameOver) {
      delay(20) // ~50 FPS
      frameCount++

      // Apply physics to player
      playerVelocityY += gravity
      playerY += playerVelocityY

      if (playerY >= groundBaseline) {
        playerY = groundBaseline
        playerVelocityY = 0f
      }

      // Move obstacles
      val speed = 9.5f + (score / 120f)
      val iterator = obstacles.iterator()
      while (iterator.hasNext()) {
        val obs = iterator.next()
        obs.x -= speed

        // Auto-Play: Jump when obstacle gets close
        if (isAutoPlay && obs.x in 80f..180f && playerY == groundBaseline) {
          playerVelocityY = jumpStrength
        }

        // Collision detection (Player hitbox: x=60, width=38, y=playerY, height=44)
        val playerLeft = 60f
        val playerRight = 60f + 38f
        val playerBottom = -playerY
        val obsLeft = obs.x
        val obsRight = obs.x + obs.width

        if (obsLeft < playerRight && obsRight > playerLeft) {
          // Horizontal overlap, check vertical
          if (playerBottom < obs.height) {
            isGameOver = true
            if (score > highScore) highScore = score
          }
        }

        if (obs.x < -80f) {
          iterator.remove()
          score += 10
        }
      }

      // Spawn new obstacles randomly
      if (frameCount % 65 == 0 && Random.nextFloat() > 0.15f) {
        val lastObstacleX = obstacles.lastOrNull()?.x ?: 0f
        if (lastObstacleX < 550f) {
          obstacles.add(
            GameObstacle(
              x = 750f,
              width = Random.nextInt(28, 42).toFloat(),
              height = Random.nextInt(42, 68).toFloat(),
              color = if (Random.nextBoolean()) accentColor else Color(0xFFF97316)
            )
          )
        }
      }
    }
  }

  // Jump trigger
  fun jump() {
    if (isGameOver) {
      // Restart game
      obstacles.clear()
      playerY = 0f
      playerVelocityY = 0f
      score = 0
      isGameOver = false
    } else if (playerY == groundBaseline) {
      playerVelocityY = jumpStrength
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) { jump() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      // Top Controls Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SPACE RUNNER",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = accentColor
          )
          Text(
            text = "Score: $score  •  Best: $highScore",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Auto-Play toggle
          Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isAutoPlay) accentColor else Color.Gray
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Auto-Play",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
          )
          Spacer(modifier = Modifier.width(6.dp))
          Switch(
            checked = isAutoPlay,
            onCheckedChange = { isAutoPlay = it },
            modifier = Modifier.testTag("auto_play_switch"),
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = accentColor
            )
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Close game button
          Button(
            onClick = onCloseGame,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
          ) {
            Text(stringResource(R.string.close), fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Canvas Game Field
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(if (isDark) Color(0xFF030712) else Color(0xFFF1F5F9))
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val canvasHeight = size.height
          val groundY = canvasHeight - 70f

          // Ground track
          drawLine(
            color = if (isDark) Color(0xFF334155) else Color(0xFF94A3B8),
            start = Offset(0f, groundY),
            end = Offset(size.width, groundY),
            strokeWidth = 3f
          )

          // Decorative starry background or terrain dots
          for (i in 0 until 12) {
            val starX = (size.width * ((i * 0.08f) + 0.05f))
            val starY = 40f + ((i % 5) * 35f)
            drawCircle(
              color = (if (isDark) Color.White else accentColor).copy(alpha = 0.35f),
              radius = 2.5f,
              center = Offset(starX, starY)
            )
          }

          // Draw obstacles
          for (obs in obstacles) {
            drawRoundRect(
              color = obs.color,
              topLeft = Offset(obs.x, groundY - obs.height),
              size = Size(obs.width, obs.height),
              cornerRadius = CornerRadius(8f, 8f)
            )
            // Accent highlight on obstacle top
            drawCircle(
              color = Color.White.copy(alpha = 0.8f),
              radius = 4f,
              center = Offset(obs.x + (obs.width / 2f), groundY - obs.height + 6f)
            )
          }

          // Draw Runner Cartoon Hero
          val runnerX = 60f
          val runnerY = groundY - 44f + playerY
          val runnerWidth = 38f
          val runnerHeight = 44f

          // Hero Body
          drawRoundRect(
            color = accentColor,
            topLeft = Offset(runnerX, runnerY),
            size = Size(runnerWidth, runnerHeight),
            cornerRadius = CornerRadius(12f, 12f)
          )

          // Hero Helmet Visor
          drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(runnerX + 14f, runnerY + 6f),
            size = Size(20f, 16f),
            cornerRadius = CornerRadius(6f, 6f)
          )

          // Glowing eye
          drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = Offset(runnerX + 24f, runnerY + 14f)
          )

          // Animated running legs if on ground
          if (playerY == 0f) {
            val legSwing = ((score / 5) % 2) == 0
            val leg1Offset = if (legSwing) 6f else -6f
            val leg2Offset = if (legSwing) -6f else 6f

            drawLine(
              color = accentColor,
              start = Offset(runnerX + 10f, runnerY + runnerHeight),
              end = Offset(runnerX + 10f + leg1Offset, groundY),
              strokeWidth = 5f
            )
            drawLine(
              color = accentColor,
              start = Offset(runnerX + 26f, runnerY + runnerHeight),
              end = Offset(runnerX + 26f + leg2Offset, groundY),
              strokeWidth = 5f
            )
          } else {
            // Jump jet flame underneath!
            drawCircle(
              color = Color(0xFFF97316),
              radius = 7f,
              center = Offset(runnerX + (runnerWidth / 2f), runnerY + runnerHeight + 6f)
            )
          }
        }

        // Overlay Game Over or Tap to Jump banner
        if (isGameOver) {
          Surface(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            color = (if (isDark) Color(0xFF1E293B) else Color.White).copy(alpha = 0.92f),
            shadowElevation = 8.dp
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = stringResource(R.string.game_over),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.error
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Score: $score",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = { jump() },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
              ) {
                Text(stringResource(R.string.play_again), fontWeight = FontWeight.Bold)
              }
            }
          }
        } else {
          Text(
            text = if (isAutoPlay) "🤖 Auto-Pilot Running!" else stringResource(R.string.tap_to_jump),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 12.dp)
          )
        }
      }
    }
  }
}
