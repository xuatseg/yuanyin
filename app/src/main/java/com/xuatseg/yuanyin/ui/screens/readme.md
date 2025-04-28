# UI Screens 模块

该模块包含了应用的所有主要屏幕界面，定义了用户界面的主要结构和导航流程。

## 主要屏幕

### MainScreen
```kotlin
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modeSwitchViewModel: ModeSwitchViewModel,
    robotControlViewModel: RobotControlViewModel,
    modifier: Modifier = Modifier
)
```
主屏幕，整合了：
- 顶部应用栏
- 机器人控制面板
- 模式切换界面
- 状态显示区域
- 导航菜单

### 屏幕布局
```
+------------------+
|    App Bar       |
+------------------+
|    Status Bar    |
+------------------+
|                  |
|   Control Panel  |
|                  |
+------------------+
|   Mode Switch    |
+------------------+
|   Bottom Nav     |
+------------------+
```

## 屏幕状态

### MainScreenState
```kotlin
data class MainScreenState(
    val robotState: RobotState,
    val modeState: ModeSwitchState,
    val controlState: RobotControlState,
    val navigationState: NavigationState,
    val error: String? = null
)
```

### NavigationState
```kotlin
data class NavigationState(
    val currentRoute: String,
    val availableRoutes: List<String>,
    val canNavigateBack: Boolean
)
```

## 导航组件

### AppNavigation
```kotlin
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
)
```
应用导航控制器，管理：
- 屏幕路由
- 导航动画
- 深层链接
- 参数传递

### NavigationRoutes
```kotlin
object NavigationRoutes {
    const val MAIN = "main"
    const val CONTROL = "control"
    const val SETTINGS = "settings"
    const val DIAGNOSTICS = "diagnostics"
}
```

## 共享组件

### TopAppBar
```kotlin
@Composable
fun YuanyinTopAppBar(
    title: String,
    onNavigationClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
)
```
顶部应用栏，提供：
- 标题显示
- 导航按钮
- 操作菜单
- 状态指示

### BottomNavigation
```kotlin
@Composable
fun YuanyinBottomNavigation(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier
)
```
底部导航栏，包含：
- 导航项目
- 选中状态
- 徽章显示
- 动画效果

## 使用示例

### 基本屏幕设置
```kotlin
@Composable
fun YuanyinApp() {
    val navController = rememberNavController()
    
    YuanyinTheme {
        Scaffold(
            topBar = {
                YuanyinTopAppBar(
                    title = "Yuanyin",
                    onNavigationClick = { /* 处理导航 */ }
                )
            },
            bottomBar = {
                YuanyinBottomNavigation(
                    currentRoute = currentRoute,
                    onRouteSelected = { route ->
                        navController.navigate(route)
                    }
                )
            }
        ) { paddingValues ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
```

### 屏幕导航
```kotlin
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.MAIN
    ) {
        composable(NavigationRoutes.MAIN) {
            MainScreen(/* ... */)
        }
        composable(NavigationRoutes.CONTROL) {
            ControlScreen(/* ... */)
        }
        composable(NavigationRoutes.SETTINGS) {
            SettingsScreen(/* ... */)
        }
        composable(NavigationRoutes.DIAGNOSTICS) {
            DiagnosticsScreen(/* ... */)
        }
    }
}
```

## 最佳实践

1. **屏幕架构**
   - 使用单活动多屏幕模式
   - 实现预览支持
   - 保持屏幕简单

2. **状态管理**
   - 使用ViewModel管理状态
   - 实现状态恢复
   - 处理配置变更

3. **导航处理**
   - 使用类型安全的导航
   - 处理深层链接
   - 支持手势导航

4. **性能优化**
   - 延迟加载屏幕
   - 优化重组范围
   - 实现过渡动画

## 屏幕预览

```kotlin
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    YuanyinTheme {
        MainScreen(
            viewModel = previewViewModel(),
            modeSwitchViewModel = previewModeSwitchViewModel(),
            robotControlViewModel = previewRobotControlViewModel()
        )
    }
}
```

## 动画效果

```kotlin
@Composable
fun ScreenTransitionAnimation(
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        content()
    }
}
```

## 错误处理

```kotlin
@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}
```

## 加载状态

```kotlin
@Composable
fun LoadingScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = message,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
```
