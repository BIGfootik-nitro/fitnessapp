package com.example.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.ui.theme.*

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(colors = listOf(Primary, PrimaryGradientEnd)))
                .then(Modifier.padding(20.dp))
        ) {
            Column { content() }
        }
    }
}

@Composable
fun StatusBadge(text: String, variant: BadgeVariant) {
    val (bg, fg) = when (variant) {
        BadgeVariant.ACTIVE -> SuccessContainer to Success
        BadgeVariant.FROZEN -> WarningContainer to Warning
        BadgeVariant.EXPIRED -> ErrorContainer to Error
        BadgeVariant.PENDING -> WarningContainer to Warning
        BadgeVariant.CONFIRMED -> SuccessContainer to Success
        BadgeVariant.CANCELLED -> ErrorContainer to Error
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = bg
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg
        )
    }
}

enum class BadgeVariant { ACTIVE, FROZEN, EXPIRED, PENDING, CONFIRMED, CANCELLED }

@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = PrimaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.W500, color = OnSurface)
                Text(subtitle, fontSize = 12.sp, color = OnSurfaceVariant)
            }
            if (badgeCount > 0) {
                Surface(shape = CircleShape, color = Error) {
                    Text(
                        "$badgeCount",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarInitials(name: String, modifier: Modifier = Modifier) {
    val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
    Surface(
        shape = CircleShape,
        color = PrimaryContainer,
        modifier = modifier.size(88.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(initials, fontSize = 32.sp, fontWeight = FontWeight.W500, color = Primary)
        }
    }
}

@Composable
fun FitnessButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
    destructive: Boolean = false
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = MaterialTheme.shapes.extraLarge,
            colors = if (destructive) ButtonDefaults.outlinedButtonColors(contentColor = Error)
                else ButtonDefaults.outlinedButtonColors()
        ) { Text(text, fontWeight = FontWeight.W500) }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = MaterialTheme.shapes.extraLarge,
            colors = if (destructive) ButtonDefaults.buttonColors(containerColor = Error)
                else ButtonDefaults.buttonColors()
        ) { Text(text, fontWeight = FontWeight.W500) }
    }
}
