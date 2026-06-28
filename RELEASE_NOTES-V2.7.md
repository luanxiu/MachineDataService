# Release Notes V2.7

Date: 2026-06-28

## 鏈鏀瑰姩

- 鏁版嵁澶勭悊鏈嶅姟榛樿 Kafka 鍦板潃鏀逛负锛?  - `10.10.24.102:19092`
  - `10.10.24.102:19093`
- 榛樿娑堣垂 topic 鏀逛负 `machine-data-64chan`銆?- Kafka 娑堣垂榛樿淇濇寔寮€鍚€?- Nacos銆丷edis 鍜屾暟鎹簱鑷姩閰嶇疆淇濇寔鍏抽棴/鍙€夛紝渚夸簬鐜板満鍙獙璇?Kafka 鏁版嵁娴併€?
## 褰撳墠鍔熻兘

- 鍚姩鍚庣洃鍚?Kafka topic `machine-data-64chan`銆?- 娑堣垂绮鹃洉鍜?64 閫氶亾閲囬泦绋嬪簭鍐欏叆鐨?JSON 鏁版嵁銆?- 瀵规秷鎭繘琛屽箓绛夊鐞嗭紝榛樿浣跨敤鍐呭瓨骞傜瓑锛涘鍚敤 Redis锛屽彲鍒囨崲涓?Redis 骞傜瓑銆?- 鍙户缁鎺?IoTDB 鍐欏叆娴佺▼銆?
## Kafka 淇敼浣嶇疆

榛樿閰嶇疆鏂囦欢锛?
```text
src/main/resources/application.yml
```

榛樿鍊硷細

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

鐜板満涔熷彲浠ヤ笉鏀规枃浠讹紝鐩存帴鐢ㄧ幆澧冨彉閲忚鐩栥€?
## 鍚姩鏂瑰紡

```powershell
cd D:\AAA_璇鹃缁勪换鍔宸ヤ笟杞欢\鏁版嵁澶勭悊\MachineDataService
$env:KAFKA_BOOTSTRAP_SERVERS="10.10.24.102:19092,10.10.24.102:19093"
$env:MACHINE_KAFKA_TOPIC="machine-data-64chan"
java -jar .\target\MachineData-Service-0.0.2-kafka-receiver-SNAPSHOT.jar
```

## 鐜板満娉ㄦ剰

- 鏁版嵁澶勭悊鏈嶅姟寤鸿鍏堜簬閲囬泦绋嬪簭鍚姩銆?- Kafka 闇€瑕佸唴閮?VPN 鍙揪銆?- 濡傛灉鍙祴璇曢噰闆嗙鏄惁鍙?Kafka锛屽彲浠ュ厛涓嶅紑 MySQL銆