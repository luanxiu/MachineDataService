# Release Notes V1.1

Date: 2026-06-28

## 本次改动

- 数据处理服务默认 Kafka 地址改为：
  - `10.10.24.102:19092`
  - `10.10.24.102:19093`
- 默认消费 topic 改为 `machine-data-64chan`。
- Kafka 消费默认保持开启。
- Nacos、Redis 和数据库自动配置保持关闭/可选，便于现场只验证 Kafka 数据流。

## 当前功能

- 启动后监听 Kafka topic `machine-data-64chan`。
- 消费精雕和 64 通道采集程序写入的 JSON 数据。
- 对消息进行幂等处理，默认使用内存幂等；如启用 Redis，可切换为 Redis 幂等。
- 可继续对接 IoTDB 写入流程。

## Kafka 修改位置

默认配置文件：

```text
src/main/resources/application.yml
```

默认值：

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:10.10.24.102:19092,10.10.24.102:19093}

machine:
  kafka:
    consumer:
      topics:
        - ${MACHINE_KAFKA_TOPIC:machine-data-64chan}
```

现场也可以不改文件，直接用环境变量覆盖。

## 启动方式

```powershell
cd D:\AAA_课题组任务\工业软件\数据处理\MachineDataService
$env:KAFKA_BOOTSTRAP_SERVERS="10.10.24.102:19092,10.10.24.102:19093"
$env:MACHINE_KAFKA_TOPIC="machine-data-64chan"
java -jar .\target\MachineData-Service-0.0.2-kafka-receiver-SNAPSHOT.jar
```

## 现场注意

- 数据处理服务建议先于采集程序启动。
- Kafka 需要内部 VPN 可达。
- 如果只测试采集端是否发 Kafka，可以先不开 MySQL。