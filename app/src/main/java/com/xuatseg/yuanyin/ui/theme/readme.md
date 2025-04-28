# UI Theme 模块

该模块定义了应用的主题系统，包括颜色、排版、形状和动态主题支持。

## 主题架构

### 核心组件
1. **ColorSystem** - 颜色定义和调色板
2. **TypographySystem** - 字体和排版系统
3. **ShapeSystem** - 形状和圆角定义
4. **DynamicTheme** - 动态主题支持

## 颜色系统

### LightColors
```kotlin
val LightColors = YuanyinColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB00020),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    onError = Color(0xFFFFFFFF)
)
```

### DarkColors
```kotlin
val DarkColors = YuanyinColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xFF000000)
)
```

## 排版系统

### Typography
```kotlin
val YuanyinTypography = Typography(
    h1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    /* 其他文本样式... */
)
```

## 形状系统

### Shapes
```kotlin
val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)
```

## 主题应用

### YuanyinTheme
```kotlin
@Composable
fun YuanyinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

## 自定义主题

### 扩展颜色
```kotlin
@Immutable
data class ExtendedColors(
    val success: Color,
    val warning: Color,
    val info: Color,
    /* 其他自定义颜色... */
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        success = Color.Unspecified,
        warning = Color.Unspecified,
        info = Color.Unspecified
    )
}
```

### 扩展主题
```kotlin
@Composable
fun ExtendedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) {
        ExtendedColors(
            success = Color(0xFF4CAF50),
            warning = Color(0xFFFFC107),
            info = Color(0xFF2196F3)
        )
    } else {
        ExtendedColors(
            success = Color(0xFF81C784),
            warning = Color(0xFFFFD54F),
            info = Color(0xFF64B5F6)
        )
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        YuanyinTheme(darkTheme = darkTheme) {
            content()
        }
    }
}
```

## 主题使用

### 基本使用
```kotlin
@Composable
fun ThemedApp() {
    YuanyinTheme {
        // 应用内容
        Surface {
            Text("Hello Yuanyin")
        }
    }
}
```

### 获取主题值
```kotlin
@Composable
fun ThemedComponent() {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    Box(
        modifier = Modifier
            .background(colors.primary)
            .padding(16.dp)
    ) {
        Text(
            text = "Themed Text",
            style = typography.bodyLarge,
            color = colors.onPrimary
        )
    }
}
```

## 最佳实践

1. **主题一致性**
   - 使用主题颜色而非硬编码
   - 保持样式统一
   - 遵循设计系统

2. **动态主题**
   - 支持系统主题切换
   - 实现自定义主题选择
   - 处理动态颜色

3. **可访问性**
   - 确保足够的对比度
   - 支持大字体
   - 考虑色盲用户

4. **性能优化**
   - 避免不必要的重组
   - 使用CompositionLocal
   - 缓存主题值

## 主题预览

```kotlin
@Preview(name = "Light Theme")
@Preview(name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ThemePreview() {
    YuanyinTheme {
        Surface {
            Text("Theme Preview")
        }
    }
}
```

## 主题扩展

### 自定义主题属性
```kotlin
@Immutable
data class CustomThemeProperties(
    val cornerRadius: Dp,
    val elevation: Dp,
    val animationDuration: Int
)

val LocalCustomThemeProperties = staticCompositionLocalOf {
    CustomThemeProperties(
        cornerRadius = 4.dp,
        elevation = 1.dp,
        animationDuration = 300
    )
}
```

### 使用自定义属性
```kotlin
@Composable
fun CustomThemedComponent() {
    val properties = LocalCustomThemeProperties.current
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(properties.cornerRadius))
            .shadow(properties.elevation)
    ) {
        // 组件内容
    }
}
```
