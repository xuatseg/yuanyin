# Embedding 模块

该模块提供了文本嵌入向量生成、存储和搜索的全套功能，支持多种嵌入模型和量化方式。

## 核心接口

### IEmbeddingService
嵌入服务主接口，提供：
- 单文本嵌入向量生成
- 批量文本嵌入向量生成
- 向量相似度计算
- 相似向量搜索

### IVectorStore
向量存储接口，提供：
- 向量存储和检索
- 批量存储
- 向量删除
- 高级搜索功能（带过滤条件）

### IEmbeddingModelManager
嵌入模型管理接口，提供：
- 模型加载/卸载
- 模型更新
- 模型信息获取
- 模型验证

### IEmbeddingMonitor
性能监控接口，提供：
- 性能指标记录
- 性能统计获取
- 实时性能监控

## 数据模型

### EmbeddingVector
嵌入向量表示，包含：
- 浮点数值数组
- 向量维度

### VectorEntry
向量存储条目，包含：
- 唯一ID
- 嵌入向量
- 元数据

### SimilarityResult
相似度结果，包含：
- 向量条目
- 相似度分数

### SearchResult
搜索结果，包含：
- 向量ID
- 相似度分数
- 向量数据
- 元数据

## 模型配置

### EmbeddingModelConfig
模型配置参数：
- 模型路径
- 向量维度
- 量化类型（NONE/INT8/INT4/FLOAT16）
- 设备类型（CPU/GPU/NPU）
- 批处理大小
- 线程数

### QuantizationType
量化类型枚举：
- NONE: 无量化
- INT8: 8位整数量化
- INT4: 4位整数量化
- FLOAT16: 16位浮点

### DeviceType
设备类型枚举：
- CPU: CPU计算
- GPU: GPU加速
- NPU: 神经网络处理器

## 性能监控

### EmbeddingPerformanceStats
性能统计数据：
- 平均嵌入时间
- 平均搜索时间
- 总嵌入次数
- 总搜索次数
- 内存使用量
- 缓存命中率

### PerformanceMetric
实时性能指标：
- 指标类型（嵌入时间/搜索时间/内存使用等）
- 指标值
- 时间戳

## 使用示例

```kotlin
// 初始化嵌入服务
val embeddingService: IEmbeddingService = // 获取服务实例

// 生成嵌入向量
val vector = embeddingService.generateEmbedding("Hello world")

// 搜索相似向量
val results = embeddingService.searchSimilar(vector, limit = 5)

// 计算相似度
val similarity = embeddingService.calculateSimilarity(vector1, vector2)
```

## 实现建议

1. 模型实现：
   - 支持ONNX、TensorFlow Lite等格式
   - 实现模型缓存机制
   - 支持模型热更新

2. 向量存储：
   - 支持内存、SQLite、FAISS等多种后端
   - 实现向量索引优化
   - 支持元数据过滤

3. 性能优化：
   - 批处理优化
   - 多线程支持
   - 量化加速
   - 缓存机制
