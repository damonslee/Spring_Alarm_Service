# Spring_Alarm_Service
메세지 알람 서비스 RestAPI


지원 기능 : 이메일, 카카오톡, SMS
- 이메일 : amazon ses 사용
- 카카오톡 : bizmsg 사용
- sms : amazon sns sms 사용


RestAPI에 대한 요청과 메세지를 비동기화 처리하기 위해 메세지 풀을 사용.
