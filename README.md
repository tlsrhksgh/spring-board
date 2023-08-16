# 프로젝트 소개
> 1달 간 커뮤니티 게시판에 있는 기능들을 참고하여 혼자서 구현한 커뮤니티 자유 게시판 프로젝트 입니다.
>
> 개발 기간: 2023.07.18 - 2023.08.16

# 요구사항 정의서

# 사용 기술
```Backend```
- JAVA17
- SpringBoot
- Spring Data JPA
- QueryDsl
- H2 Database

```DevOps```
- AWS EC2
- AWS RDS
- AWS S3
- Jenkins
- Nginx

```Frontend```
- Thymeleaf
- JQuery

# ERD 설계
![spring-board](https://github.com/tlsrhksgh/spring-board/assets/12388299/bed37d10-9aa0-4339-931a-b2dfc0b0a0da)

# 프로젝트 구조
```
📂 src
 └── 📂 main         
      ├── 📂 java          			
      |     └── 📂 com           		
      |          └── 📂 single         	
      |               └── 📂 springboard
      |                    ├── 📂 config
      |                         ├── 📂 auth
      |                             ├── 📂 dto
      |                                 └── 📄 OAuthAttribute
      |                                 └── 📄 SessionUser
      |                             └── 📄 CustomOauth2UserService
      |                             └── 📄 LoginUser
      |                             └── 📄 LoginUserArgumentResolver
      |                         └── 📄 JpaConfig
      |                         └── 📄 QuerydslConfig
      |                         └── 📄 RedisConfig
      |                         └── 📄 S3Config
      |                         └── 📄 SecurityConfig
      |                         └── 📄 WebConfig
      |                    ├── 📂 domain (Entity와 JpaRepository 파일)
      |                         └── 📂 comments
      |                         └── 📂 files
      |                         └── 📂 posts
      |                         └── 📂 user
      |                    ├── 📂 exception
      |                    ├── 📂 service
      |                         └── 📂 comments
      |                             └── 📄 CommentsService
      |                         └── 📂 files
      |                             └── 📄 AwsS3Upload
      |                             └── 📄 FilesService
      |                         └── 📂 posts
      |                             └── 📄 PostsService
      |                         └── 📂 search
      |                             └── 📄 SearchService
      |                    ├── 📂 util
      |                         └── 📄 CommentsUtils
      |                         └── 📄 DateUtils
      |                         └── 📄 FileUtils
      |                    ├── 📂 web
      |                         ├── 📂 dto
      |                         └── 📄 CommentApiController
      |                         └── 📄 IndexController(Server side rendering)
      |                         └── 📄 PostsApiController
      |                    └── 📄 SpringBoardApplication.java
      ├── 📂 resources
      |     ├── 📂 static
      |          ├── 📂 images
      |          ├── 📂 js
      |              ├── 📂 app
      |                    └── 📄 comments.js
      |                    └── 📄 posts.js
      |     ├── 📂 templates
      |          └── 📄 index.html
      |          └── 📄 post-find.html
      |          └── 📄 post-save.html
      |          └── 📄 post-update.html
      |          └── 📄 search.html
      |     └── 📄 application.properties
      |     └── 📄 application-oauth.properties
      |     └── 📄 application-aws.properties
📄 .gitignore    
📄 build.gradle                                                                          
📄 README.md
```

