# MachineDataService

MachineDataService is a Java 17 Spring Boot service for receiving machine data, consuming Kafka channel messages, and writing time-series data to IoTDB.

## What It Does

- Provides REST APIs for IoTDB write, query, update, and delete operations.
- Optionally consumes Kafka messages produced by the C++ collector.
- Converts channel payloads such as `chan1` to `chan64` into IoTDB devices like `root.machine.channel_01`.
- Accepts JD50 `DataCollection` field payloads using `schema=jd50.fields.v1`, `fields`, and `fieldTypes`.
- Uses Redis for Kafka message idempotency when Redis is enabled.
- Falls back to in-memory idempotency when Redis is disabled, which is useful for local development.

## V1.3 Update Notes

- Added compatibility for JD50 `DataCollection` JSON messages produced after each CSV row is collected.
- Kept the original `channels` Kafka payload handling for the 64-channel collector.
- Extended `ChannelEntity` with `schema`, `machineIp`, `device`, `source`, `fields`, and `fieldTypes`.
- JD50 field messages are written as one IoTDB record under `root.test.<device>.<source-without-csv>`.
- Added flexible timestamp parsing for millisecond and microsecond precision timestamps.
- Added a consumer unit test covering JD50 `fields/fieldTypes` conversion.

## Local Run

By default, Nacos and Redis are disabled, while Kafka consumption is enabled for the C++ 64-channel collector defaults:

- Kafka broker: `10.1.40.171:9092`
- Kafka topic: `test-topic`
- IoTDB database prefix: `root.test`

```powershell
cd "D:\AAA_课题组任务\工业软件\数据处理\IndustrySoftware\MachineDataService"
mvn spring-boot:run
```

Open:

```text
http://localhost:8054/
http://localhost:8054/iotdb/examples
http://localhost:8054/actuator/health
```

## Enable Kafka Upload Flow

The default upload flow matches the C++ collector defaults in `Hydaq64Collector`:

- Kafka: `10.1.40.171:9092`
- Topic: `test-topic`
- IoTDB: `127.0.0.1:6667` by default, override `IOTDB_HOST` for your server

Start or provide these services first:

- Kafka: `10.1.40.171:9092`
- IoTDB: `127.0.0.1:6667`
- Redis: optional, `127.0.0.1:6379`

Then run with environment variables:

```powershell
$env:MACHINE_KAFKA_CONSUMER_ENABLED="true"
$env:MACHINE_KAFKA_TOPIC="test-topic"
$env:KAFKA_BOOTSTRAP_SERVERS="10.1.40.171:9092"
$env:IOTDB_HOST="127.0.0.1"
$env:IOTDB_DATABASE="root.test"
mvn spring-boot:run
```

To use Redis idempotency:

```powershell
$env:MACHINE_REDIS_ENABLED="true"
$env:MACHINE_REDIS_ADDRESS="redis://127.0.0.1:6379"
mvn spring-boot:run
```

## Kafka Message Format

The C++ 64-channel collector should send JSON like:

```json
{
  "messageId": "msg-20260525120000-000001-1",
  "time": "2026-05-25 12:00:00.123456",
  "channels": {
    "chan1": 1.23,
    "chan2": 2.34
  }
}
```

The timestamp format must be:

```text
yyyy-MM-dd HH:mm:ss.SSSSSS
```

JD50 `DataCollection` can also publish structured field messages while still saving CSV locally:

```json
{
  "messageId": "jd50-169_254_0_26-spin_data_csv-20260518094928559-1",
  "schema": "jd50.fields.v1",
  "machineIp": "169.254.0.26",
  "device": "jd50_169_254_0_26",
  "source": "spin_data.csv",
  "time": "2026-05-18 09:49:28.559000",
  "fields": {
    "FeedRate": 100,
    "SpindleCurrent": 483.0
  },
  "fieldTypes": {
    "FeedRate": "INT64",
    "SpindleCurrent": "DOUBLE"
  }
}
```

These messages are written to IoTDB under:

```text
root.test.<device>.<source-without-csv>
```

For example, `source=spin_data.csv` and `device=jd50_169_254_0_26` are written under:

```text
root.test.jd50_169_254_0_26.spin_data
```

## Tests

Run normal local tests:

```powershell
mvn test
```

Real Kafka/Redis/IoTDB integration tests are skipped by default. Enable them only when the real services are reachable:

```powershell
mvn test -Dreal.integration.tests=true
```
