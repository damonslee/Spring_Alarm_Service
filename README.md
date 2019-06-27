# Spring_Alarm_Service
메세지 알람 서비스 RestAPI

V1
지원 기능 : 이메일, 카카오톡, SMS
- 이메일 : amazon ses 사용
- 카카오톡 : bizmsg 사용
- sms : amazon sns sms 사용


RestAPI에 대한 요청과 메세지를 비동기화 처리하기 위해 메세지 풀을 사용.

V2
지원 기능 : Amazon SQS에 데이터 푸쉬
- Rest API 지원
- request / Response / HttpStatus 응답 구현
