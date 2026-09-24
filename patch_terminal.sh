sed -i 's/\.background(MaterialTheme\.colorScheme\.surfaceContainerHighest)/\.background(Color(0xFF0A0E14))/g' app/src/main/java/com/loopstack/presentation/terminal/TerminalScreen.kt

sed -i '/import com.loopstack.core.theme.ElectricCyan/a import androidx.compose.ui.graphics.Color' app/src/main/java/com/loopstack/presentation/terminal/TerminalScreen.kt
