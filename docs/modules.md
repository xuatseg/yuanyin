已思考若干秒


以下为**四层架构**中**每个模块**的**主要功能**及其**输入/输出接口**的详细设计信息，便于各团队独立开发与集成。

---

# 1. UI 层（Presentation）

> **职责**：页面渲染、用户交互、导航控制。
> **技术**：Jetpack Compose + ViewModel + Navigation

| 子模块                    | 主要功能                    | 输入接口（来自 ViewModel）                                                                        | 输出接口（调用 ViewModel）                                                                       |
| ---------------------- | ----------------------- | ----------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| **StandbyScreen**      | 手机吸附待机动画，设备状态提示         | `val isDocked: StateFlow<Boolean>`                                                        | `fun onStartView()`<br>`fun onStopView()`                                                |
| **ControlScreen**      | 语音波形展示、指令按钮、小车状态（Emoji） | `val asrState: StateFlow<AsrUiState>`<br>`val cmdState: StateFlow<CommandUiState>`        | `fun startListening()`<br>`fun stopListening()`<br>`fun sendCommand(dir: MoveDirection)` |
| **FaceTrackScreen**    | 实时摄像头预览、跟踪框绘制、丢失提示      | `val faces: StateFlow<List<FaceBox>>`                                                     | `fun startTracking()`<br>`fun stopTracking()`                                            |
| **ProjectionScreen**   | 文档/视频列表、投影控制按钮、投影状态指示   | `val docs: StateFlow<List<ContentItem>>`<br>`val projState: StateFlow<ProjectionUiState>` | `fun selectContent(id: String)`<br>`fun startProjection()`<br>`fun stopProjection()`     |
| **NotificationScreen** | 推送消息列表、详情弹窗             | `val messages: StateFlow<List<PushMessage>>`                                              | `fun refresh()`<br>`fun markAsRead(id: String)`                                          |

---

# 2. Application 层（Use Case / Service）

> **职责**：封装业务流程，协调 Domain 与 Adapters。
> **模式**：每个模块一个 UseCase，注入相应 Outbound Ports。

| 模块                      | 主要功能                            | 输入接口（Inbound Port）                                                                    | 输出接口（Outbound Port）                                                                                        |
| ----------------------- | ------------------------------- | ------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| **VoiceUseCase**        | 唤醒词检测 → ASR → 意图解析 → TTS → 动画触发 | `interface VoiceUseCase { suspend fun listenAndProcess(): Unit }`                     | `ASRProvider.listen(): Flow<RecognizedText>`<br>`TTSProvider.speak(text)`<br>`AnimationTrigger.show(anim)` |
| **RobotUseCase**        | 接收移动指令 → 发送 BLE → 监听设备反馈 → 更新状态 | `interface RobotUseCase { suspend fun move(dir: MoveDirection): Unit }`               | `BLEClient.connect(mac): Boolean`<br>`BLEClient.sendData(bytes)`                                           |
| **FaceUseCase**         | 启动摄像头 → 检测人脸 → 维护跟踪状态 → 丢失重连    | `interface FaceUseCase { fun start(): Flow<List<FaceBox>> }`                          | `CameraProvider.stream(): Flow<Frame>`<br>`FaceDetector.detect(frame): List<FaceBox>`                      |
| **ProjectionUseCase**   | 列表内容 → 发起/停止投影 → 监控投影状态         | `interface ProjectionUseCase { suspend fun project(id: String); suspend fun stop() }` | `DisplayEngine.start(display, stream)`<br>`DocumentParser.parse(id): ContentStream`                        |
| **NotificationUseCase** | 注册订阅 → 接收消息 → 存储/派发到 UI         | `interface NotificationUseCase { fun onReceive(msg: PushMessage) }`                   | `PushService.subscribe(topic)`<br>`PushService.ack(msgId)`                                                 |
| **LLMUseCase**          | 构造请求 → 调用 LLM → 缓存/降级 → 返回结果    | `interface LLMUseCase { suspend fun ask(prompt: String): String }`                    | `LLMApiProvider.request(prompt): String`<br>`LLMCache.get/put(prompt, result)`                             |

---

# 3. Domain 层（Entities / Value Objects）

> **职责**：核心业务模型、领域规则，与任何技术、框架无关。

| 实体 / 值对象          | 属性 & 行为                                                                             | 输入 / 输出（内部 API）                                       |
| ----------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------- |
| **RobotCar**      | `status: CarStatus`<br>`position: Coordinates`<br>`enqueue(cmd)`<br>`executeNext()` | `enqueue(command: MoveCommand)`<br>`onFeedback(data)` |
| **VoiceCommand**  | `text: String`<br>`intent: IntentType`<br>`params: Map`                             | `parse(text): VoiceCommand`                           |
| **FaceTrack**     | `faces: List<FaceBox>`<br>`lostCount: Int`<br>`reconnect()`                         | `update(faces: List<FaceBox>)`                        |
| **ProjectionJob** | `contentId: String`<br>`status: JobStatus`<br>`start()`<br>`stop()`                 | `start()`<br>`stop()`                                 |
| **PushMessage**   | `id: String`<br>`topic: String`<br>`payload: String`                                | n/a (immutable data)                                  |
| **LLMRequest**    | `prompt: String`<br>`response: String?`<br>`retryCount: Int`                        | `incrementRetry()`                                    |

---

# 4. Adapters 层（Inbound/Outbound Adapters）

> **职责**：实现 Ports 接口，负责与设备、第三方服务或持久化层对接。

| 适配器                        | 实现接口               | 主要功能            | 输入 / 输出                                 |
| -------------------------- | ------------------ | --------------- | --------------------------------------- |
| **BLEAdapter**             | `BLEClient`        | 扫描、连接、读写 BLE 数据 | 输入：MAC 地址、ByteArray<br>输出：连接状态、ACK      |
| **ASRAdapter**             | `ASRProvider`      | 本地/云端语音识别       | 输出：`Flow<RecognizedText>`               |
| **TTSAdapter**             | `TTSProvider`      | 文本转语音           | 输入：文本                                   |
| **AnimationAdapter**       | `AnimationTrigger` | 播放 Lottie/帧动画   | 输入：动画 key                               |
| **CameraAdapter**          | `CameraProvider`   | 摄像头流采集          | 输出：`Flow<Frame>`                        |
| **MLKitAdapter**           | `FaceDetector`     | ML Kit 人脸检测     | 输入：`Frame`<br>输出：`List<FaceBox>`        |
| **MediaProjectionAdapter** | `DisplayEngine`    | 屏幕镜像 / 内容渲染     | 输入：`ContentStream`<br>输出：DisplaySurface |
| **DocumentParserAdapter**  | `DocumentParser`   | PPT/PDF/视频解析    | 输入：文件ID<br>输出：渲染数据流                     |
| **FCMAdapter**             | `PushService`      | 接收 FCM 消息、主题订阅  | 输出：`PushMessage`                        |
| **LLMApiAdapter**          | `LLMApiProvider`   | 调用大模型 REST/gRPC | 输入：Prompt<br>输出：Response                |
| **CacheAdapter**           | `LLMCache`         | 本地缓存结果          | 输入：Key, Value<br>输出：Cached Value        |
| **RoomSettingsAdapter**    | `SettingsStore`    | 持久化用户配置         | 输入/输出：Key, Value                        |

---

## 🚀 并行开发与集成

* **高内聚**：每个模块都清晰只做一件事，内部封装完善。
* **低耦合**：模块间仅通过接口（Ports）通信，替换任一 Adapter 或 Use Case 不影响其他模块。
* **并行开发**：前端、后台、算法、硬件团队可基于同一份接口契约文档，独立实现并行交付。

> **Tip**：建议用 **OpenAPI/Protobuf IDL** 或 **Kotlin Interfaces** 生成 Mock 实现，保证早期测试与验证。



已思考 1 秒


在六边形架构中，**Domain 层**（领域层）和 **Application 层**（用例/服务层，也常称作 Use Case 层）职责分明，但相互配合，共同实现业务价值。

---

## 🏷️ 定义与职责

| 层级           | 主要内容                                                                                                                                                             | 关注点                                               |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------- |
| **Domain**   | - **实体（Entities）**、**值对象（Value Objects）**、**领域服务（Domain Services）**<br>- 纯业务模型与业务规则<br>- 不依赖任何外部技术或框架                                                            | — 业务核心<br>— 保证模型不变性与一致性                           |
| **Use Case** | - **用例服务（Application Services）**，也称 Use Case<br>- 协调多个实体、领域服务完成一次业务流程<br>- 定义“输入端口”（Inbound Port），为外部（UI、API）提供服务<br>- 调用“输出端口”（Outbound Port）访问外部资源（持久化、消息、第三方） | — 业务流程编排<br>— 安全边界与事务管理<br>— 调度 Domain 与 Adapters |

---

## 🔄 联系与数据流

1. **输入 → 用例层（Use Case）**

   * UI 或外部调用一个 Use Case 接口，比如：

     ```kotlin
     orderUseCase.placeOrder(cmd: PlaceOrderCommand)
     ```
2. **用例层执行业务流程**

   * 校验输入、开启事务
   * 调用 Domain 实体或领域服务执行核心规则：

     ```kotlin
     val order = Order.create(cmd.items, cmd.customerId)
     order.calculateTotal()
     ```
3. **用例层调用输出端口**

   * 与外部系统交互（如持久化、推送、调用外部 API）：

     ```kotlin
     orderRepository.save(order)        // Outbound Port → Adapter
     paymentGateway.charge(order.total)
     ```
4. **Domain 层富模型**

   * `Order` 实体内部封装折扣、库存检查、状态机等业务规则
   * 领域服务（如 `StockService.reserve(order)`)）提供跨实体的操作
5. **用例层收尾**

   * 事务提交/回滚
   * 返回简单 DTO 给调用者，或抛出业务异常

---

## ⚖️ 区别小结

| 维度       | Domain 层             | Use Case 层                      |
| -------- | -------------------- | ------------------------------- |
| **粒度**   | **微观**：概念级（订单、客户、库存） | **宏观**：场景级（一键下单、语音控制）           |
| **关注点**  | 业务规则、不变式             | 业务流程、事务边界、外部交互                  |
| **依赖方向** | 不依赖任何其他层             | 依赖 Domain 层 + Outbound Ports 接口 |
| **变更频率** | 较低（核心业务规则稳定）         | 可能较高（流程与需求经常演进）                 |

---

## 🎯 举例（Looi Robot App）

* **Domain 实体**

  ```kotlin
  class RobotCar {
    fun enqueue(command: MoveCommand) { … }    // 领域内指令队列逻辑
    fun executeNext() { … }                    // 业务规则：前后左右转顺序
  }
  ```

* **Use Case 服务**

  ```kotlin
  class RobotUseCase(
    private val car: RobotCar,
    private val ble: BLEClient
  ) {
    suspend fun move(direction: MoveDirection) {
      car.enqueue(MoveCommand(direction))      // Domain 行为
      ble.sendData(direction.toBytes())        // 与外部适配器交互
    }
  }
  ```

* **流程对比**

  * **Domain**：`RobotCar` 知道如何在本地维护和执行指令队列；
  * **Use Case**：`RobotUseCase` 知道何时将指令落地到 BLE 硬件，并处理外部失败重试等流程。

---

通过这种分层，**业务核心（Domain）** 得以纯粹、持续演进；**用例层（Use Case）** 灵活响应需求变化，且可独立测试和替换外部技术实现。
