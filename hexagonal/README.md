# architecture

## Hexagonal Architecture study
- Hexagonal Architecture
  - My Article : https://starryeye.tistory.com/152
  - Domain : pay(account)
    - Api : send money, check balance
    - Package Dependency Diagram
      - <img width="998" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/7b8b5a6d-a5dd-435a-a0fb-3df95dacc469">
    - Test
      - domain, application, adapter layer 격리 Mock Unit Test
      - System Test
      - Architecture Test
        - package 의존 방향 Test
    - Package Structure
      - main
        - <img width="501" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/5f5a9428-4654-458b-af22-0a6bbd96dbab">
      - test
        - <img width="520" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/f23a0ca6-54e5-4f7d-ad8f-cf3573152072">
  
## 다른 리포지토리
- [hellopay](https://github.com/starryeye/hellopay)



## dependency
- Java 17
- Spring Boot 3.0.5
- Spring Web
- Spring Data Jpa
- Validation
- Lombok
- H2
- Archunit 1.0.1
- Spring Boot Test

