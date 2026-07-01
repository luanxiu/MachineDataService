# Release Notes V4.0

Date: 2026-07-01

备注：数字孪生mysql专用。

## 版本说明

- 当前数字孪生 MySQL 主流程由 `接收模块` 写入本机 MySQL，Java 服务保留 Kafka 消费和 IoTDB 接口能力。
- Kafka 默认 broker 已配置为 `10.10.24.102:19092,10.10.24.102:19093`。
- Kafka 默认 topic 为 `machine-data-64chan`，用于 64 通道数据处理。
- 精雕采集侧 Kafka topic 使用 `machine-data-jingdiao`，如需 Java 服务消费精雕数据，启动时修改 `MACHINE_KAFKA_TOPIC`。

## 启动提示

```powershell
$env:KAFKA_BOOTSTRAP_SERVERS="10.10.24.102:19092,10.10.24.102:19093"
$env:MACHINE_KAFKA_TOPIC="machine-data-64chan"
mvn spring-boot:run
```

如只做数字孪生 MySQL 读取，可以不启动本 Java 服务，直接启动根目录的 MySQL 写入流程。
