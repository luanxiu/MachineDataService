# Release Notes V2.8

Date: 2026-06-29

## Version Note

- 6.29机床连接成功，Kafka发送成功。
- 数据处理服务版本更新到 V2.8，与总控、精雕采集、64通道采集版本保持一致。

## Current Function

- Kafka 消费目标与采集端保持一致：
  - brokers: `10.10.24.102:19092,10.10.24.102:19093`
  - topic: `machine-data-64chan`
- 可用于消费精雕和 64通道采集程序发送到 Kafka 的现场数据。
- MySQL 当前不是必需流程，现场可先不启动。

## How To Run

```powershell
cd D:\AAA_课题组任务\工业软件\数据处理\MachineDataService
$env:KAFKA_BOOTSTRAP_SERVERS="10.10.24.102:19092,10.10.24.102:19093"
$env:MACHINE_KAFKA_TOPIC="machine-data-64chan"
java -jar .\target\MachineData-Service-0.0.2-kafka-receiver-SNAPSHOT.jar
```

启动后观察日志中是否收到 `machine-data-64chan` 的 Kafka 消息。
