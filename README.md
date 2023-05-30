# Nogari 포팅메뉴얼

![Nogari_Manual](/images/Nogari_Manual.png)

## 1. 서비스 소개

‘Nogari’는 ‘**Notion에서 가는 이야기**’의 줄임말로, 원스톱으로 **노션에서 타 포스팅 플랫폼으로 페이지를 발행해주는 서비스**입니다. 노션은 2500만명의 유저를 갖는 올인원 생산성 앱으로 동시 협업, 실시간 동기화, 탬플릿 활용을 통해 효율적인 기록을 가능하게 합니다. 

다만, 노션의 경우 개인적인 기록 및 관리 도구로 타인에게 직접 공유하거나 웹에서 링크로 공유해야 공유가 됩니다. 따라서 타 포스팅 플랫폼를 사용하는 블로거들이 겪어야하는 별도의 포스팅 과정의 불편함을 해결하기 위해 서비스를 기획하게 되었습니다. 

### 타깃

- 노션, 티스토리, 깃헙 등 여러 번의 포스팅 과정의 불편함을 겪는 유저들  

### 주요기능

1. Notion 페이지 Tistory 블로그 발행 및 수정 기능
2. Notion 페이지 Github 레포지토리 발행 및 수정 기능
3. Tistory, Github 전체 발행 이력 조회 기능(무한스크롤)
4. Chrome Extension을 활용한 서비스 접근성 향상

## 2. 개발환경

- 프론트엔드 : React 18.2.0, TypeScript 4.9.5, react-hook-form 7.43.9
- 백엔드 : Java 11, AWS Lambda, SpringBoot 2.7.10, SpringSecurity, JWT,Redis
- DB : MySQL : 8.0.33, Redis 7.0.11
- 서버: Ubuntu 20.04 LTS (AWS EC2)
- 인프라 : Docker 23.0.5, Jenkins 2.387.1, Nginx 1.18.0
- IDE : IntelliJ IDEA 2022.3.2, VSCode
- 기획 : Figma, Notion, JIRA, GitLab, ERDCloud
- 기타 : PostMan, Swagger, Mattermost

## 3. 빌드 방법

[빌드 방법(Jenkins)](https://www.notion.so/Jenkins-b306348c288f4a6589750a56bdc3f423)

## 4. 외부 기술

- JWT & Redis
- [AWS Lambda](https://www.notion.so/AWS-Lambda-733190948f3f4a4bbb96c65d543396bc)
- [Querydsl ](https://www.notion.so/Querydsl-559a247757f34284ad839c1d5ddb8835)
- [Jenkins & Dockers](https://www.notion.so/Jenkins-Dockers-72ce8fe0e808414380da8c519af2d0bc)
- [Chrome Extension](https://www.notion.so/Chrome-Extension-e52987440b7d44648e30df635dac8352)

## 5. 사용자 인터페이스

- <b>토큰 발행 (Notion, Tistory, Github)</b><br/>  
![토큰발행](/images/토큰발행.gif)<br/><br/>   

- <b>Notion에서 Tistory로 포스팅 발행</b><br/>  
![티스토리발행](/images/티스토리발행.gif)<br/><br/>   

- <b>크롬 확장프로그램 설치</b><br/>    
![확장프로그램설치](/images/확장프로그램설치.gif)<br/><br/>    

- <b>크롬 확장프로그램에서 Tistory로 포스팅</b><br/>  
![확장프로그램사용](/images/확장프로그램사용.gif)<br/><br/>    




