# 消息推送模块设计文档

## 1. 模块概述
消息推送模块负责处理 Firebase Cloud Messaging (FCM) 消息、本地通知管理以及消息展示控制。该模块采用六边形架构设计，支持多种消息类型和通知方式。

## 2. 核心功能
- FCM 消息接收与处理
- 本地通知管理
- 消息分类与过滤
- 通知展示控制
- 消息持久化

## 3. 接口设计

### 3.1 领域接口
```kotlin
// 消息管理接口
interface NotificationManager {
    suspend fun subscribeTopic(topic: String): Result<Unit>
    suspend fun unsubscribeTopic(topic: String): Result<Unit>
    suspend fun sendNotification(notification: PushNotification): Result<Unit>
    fun observeNotifications(): Flow<List<PushNotification>>
}

// 推送消息
data class PushNotification(
    val id: String,
    val title: String,
    val content: String,
    val type: NotificationType,
    val data: Map<String, String>,
    val timestamp: Long,
    val isRead: Boolean = false
)

// 消息类型
sealed class NotificationType {
    object System : NotificationType()
    object Alert : NotificationType()
    object Update : NotificationType()
    object Custom : NotificationType()
}
```

### 3.2 适配器接口
```kotlin
// FCM 适配器
interface FCMAdapter {
    suspend fun subscribeToTopic(topic: String): Result<Unit>
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit>
    fun observeMessages(): Flow<FCMMessage>
}

// 本地通知适配器
interface LocalNotificationAdapter {
    suspend fun showNotification(notification: PushNotification): Result<Unit>
    suspend fun cancelNotification(id: String)
    suspend fun clearAllNotifications()
}

// 消息存储适配器
interface NotificationStorage {
    suspend fun saveNotification(notification: PushNotification)
    suspend fun getNotifications(): List<PushNotification>
    suspend fun markAsRead(id: String)
    suspend fun deleteNotification(id: String)
}
```

## 4. 实现细节

### 4.1 数据模型
```kotlin
data class FCMMessage(
    val messageId: String,
    val data: Map<String, String>,
    val notification: FCMPayload?,
    val timestamp: Long
)

data class FCMPayload(
    val title: String,
    val body: String,
    val icon: String?,
    val sound: String?
)

data class NotificationState(
    val notifications: List<PushNotification>,
    val unreadCount: Int,
    val lastUpdate: Long
)
```

### 4.2 用例实现
```kotlin
class NotificationUseCase @Inject constructor(
    private val fcmAdapter: FCMAdapter,
    private val localNotificationAdapter: LocalNotificationAdapter,
    private val notificationStorage: NotificationStorage
) {
    private val _notificationState = MutableStateFlow<NotificationState>(NotificationState(emptyList(), 0, 0))
    val notificationState: StateFlow<NotificationState> = _notificationState.asStateFlow()
    
    init {
        startMessageObserving()
    }
    
    private fun startMessageObserving() {
        viewModelScope.launch {
            fcmAdapter.observeMessages()
                .collect { message ->
                    handleMessage(message)
                }
        }
    }
    
    private suspend fun handleMessage(message: FCMMessage) {
        val notification = PushNotification(
            id = message.messageId,
            title = message.notification?.title ?: "新消息",
            content = message.notification?.body ?: "",
            type = determineNotificationType(message.data),
            data = message.data,
            timestamp = message.timestamp
        )
        
        // 保存消息
        notificationStorage.saveNotification(notification)
        
        // 显示通知
        localNotificationAdapter.showNotification(notification)
        
        // 更新状态
        updateNotificationState()
    }
    
    private fun determineNotificationType(data: Map<String, String>): NotificationType {
        return when (data["type"]) {
            "system" -> NotificationType.System
            "alert" -> NotificationType.Alert
            "update" -> NotificationType.Update
            else -> NotificationType.Custom
        }
    }
    
    private suspend fun updateNotificationState() {
        val notifications = notificationStorage.getNotifications()
        val unreadCount = notifications.count { !it.isRead }
        _notificationState.value = NotificationState(
            notifications = notifications,
            unreadCount = unreadCount,
            lastUpdate = System.currentTimeMillis()
        )
    }
    
    suspend fun markAsRead(id: String) {
        notificationStorage.markAsRead(id)
        updateNotificationState()
    }
    
    suspend fun deleteNotification(id: String) {
        notificationStorage.deleteNotification(id)
        localNotificationAdapter.cancelNotification(id)
        updateNotificationState()
    }
}
```

## 5. 错误处理
- FCM 连接失败处理
- 消息解析错误处理
- 存储异常处理
- 通知显示失败处理

## 6. 测试策略
- 单元测试：模拟 FCM 和存储
- 集成测试：实际 FCM 测试
- UI 测试：通知界面交互
- 性能测试：消息处理速度

## 7. 依赖注入
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideFCMAdapter(
        @ApplicationContext context: Context
    ): FCMAdapter {
        return FCMAdapterImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideLocalNotificationAdapter(
        @ApplicationContext context: Context
    ): LocalNotificationAdapter {
        return LocalNotificationAdapterImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationStorage(
        @ApplicationContext context: Context
    ): NotificationStorage {
        return NotificationStorageImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationUseCase(
        fcmAdapter: FCMAdapter,
        localNotificationAdapter: LocalNotificationAdapter,
        notificationStorage: NotificationStorage
    ): NotificationUseCase {
        return NotificationUseCase(
            fcmAdapter,
            localNotificationAdapter,
            notificationStorage
        )
    }
}
```

## 8. 使用示例
```kotlin
class NotificationViewModel @Inject constructor(
    private val notificationUseCase: NotificationUseCase
) : ViewModel() {
    val notificationState: StateFlow<NotificationState> = notificationUseCase.notificationState
    
    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationUseCase.markAsRead(id)
        }
    }
    
    fun deleteNotification(id: String) {
        viewModelScope.launch {
            notificationUseCase.deleteNotification(id)
        }
    }
}
``` 