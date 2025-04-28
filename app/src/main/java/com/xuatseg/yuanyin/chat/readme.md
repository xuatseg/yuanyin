# Chat 模块

该模块提供了机器人的聊天功能相关接口定义。

## 核心接口

### IChatInterface
主要的聊天接口，提供以下功能：
- 发送文本消息
- 发送语音消息
- 获取消息历史
- 观察新消息
- 观察消息状态变化
- 重试发送消息
- 删除消息

### IVoiceProcessor
语音处理接口，提供：
- 录音功能
- 语音播放
- 语音转文字
- 文字转语音

### IChatConfig
聊天配置接口，定义：
- 消息历史保存时长
- 语音最大时长
- 消息重试次数
- 语音质量设置

### IChatStateListener
聊天状态监听接口，处理：
- 连接状态变化
- 新消息通知
- 消息状态更新
- 错误处理

## 数据模型

### ChatMessage
聊天消息数据模型，包含：
- 消息ID
- 消息内容（文本/语音）
- 发送者信息
- 时间戳
- 消息状态
- 元数据

### MessageContent
消息内容类型：
- Text: 文本消息
- Voice: 语音消息（包含音频文件、时长、转写文本）
- Error: 错误消息

### MessageSender
消息发送者类型：
- User: 用户
- Bot: 机器人
- System: 系统消息

### MessageStatus
消息状态定义：
- Sending: 发送中
- Sent: 已发送
- Delivered: 已送达
- Read: 已读
- Failed: 发送失败
