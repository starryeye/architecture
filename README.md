# architecture

## Hexagonal Architecture study
- Hexagonal Architecture
  - My Article : https://starryeye.tistory.com/152
  - Domain : pay(account)
    - Api : send money, check balance
    - Package Dependency Diagram
      - <img width="926" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/d4a906a2-a804-4201-a586-c3f212a8622b">
    - Test
      - domain, application, adapter layer 격리 Unit Test
      - System Test
      - Architecture Test
        - package 의존 방향 Test
    - Package Structure
      - main
        - <img width="501" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/5f5a9428-4654-458b-af22-0a6bbd96dbab">
      - test
        - <img width="520" alt="image" src="https://github.com/starryeye/architecture/assets/33487061/f23a0ca6-54e5-4f7d-ad8f-cf3573152072">
  - Domain : order
    - TODO
  
  
## dependency
- Java 17
- Spring Boot 3.0.5
- Spring Web
- Spring Data Jpa
- Validation
- Lombok
- H2
- Archunit

## Clean Architecture study
- TODO  
