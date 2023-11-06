# 프로젝트 소개
> 1달 간 커뮤니티 게시판과 책을 참고하여 혼자서 구현한 커뮤니티 자유 게시판 프로젝트 입니다.
>
> 
> 개발 기간: 2023.07 - 2023.09

# 사용 기술
## Backend
- ```JAVA17``` ```SpringBoot``` ```Spring Data JPA``` ```Spring Data Redis``` ```QueryDsl``` ```Spring Data Elasticsearch``` ```Spring Session Redis``` 

## DevOps
- ```AWS EC2``` ```MariaDB``` ```AWS S3``` ```Jenkins``` ```Nginx``` ```Docker``` ```Elasticsearch``` ```Redis```

## Frontend
- ```Thymeleaf``` ```JQuery```

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
      |                             └── 📄 LoginUserArgumentResolver
      |                         ├── 📂 handler
      |                             └── 📄 OAuthLoginSuccessHandler
      |                         └── 📄 JpaConfig
      |                         └── 📄 LoadScriptConfig
      |                         └── 📄 QuerydslConfig
      |                         └── 📄 RedisConfig
      |                         └── 📄 S3Config
      |                         └── 📄 SecurityConfig
      |                         └── 📄 WebConfig
      |                    ├── 📂 domain (Entity와 JpaRepository 파일)
      |                         └── 📂 comment
      |                         └── 📂 file
      |                         └── 📂 post
      |                         └── 📂 user
      |                    ├── 📂 exception
      |                         └── 📂 auth
      |                             └── 📄 CustomAuthEntryPointException
      |                         └── 📄 CustomException
      |                         └── 📄 ErrorCode
      |                         └── 📄 ExceptionHandler
      |                         └── 📄 ExceptionForm
      |                    ├── 📂 service
      |                         └── 📂 comment
      |                             └── 📄 CommentService
      |                         └── 📂 file
      |                             ├── 📂 dto
      |                                 └── 📄 FilesNameDto
      |                             └── 📄 AwsS3Upload
      |                             └── 📄 FileService
      |                         └── 📂 post
      |                             ├── 📂 constants
      |                                 └── 📄 PostKeys
      |                             ├── 📂 dto
      |                                 └── 📄 CountResponse
      |                                 └── 📄 PostRankResponse 
      |                             └── 📄 PostService
      |                         └── 📂 search
      |                             └── 📄 SearchService
      |                         └── 📂 user
      |                             ├── 📂 dto
      |                                 └── 📄 OAuthAttribute
      |                                 └── 📄 SessionUser
      |                             └── 📄 CustomOauth2UserService
      |                             └── 📄 LoginUser
      |                    ├── 📂 util
      |                         └── 📄 CommentsUtils
      |                         └── 📄 FileUtils
      |                         └── 📄 JpaCommmonUtils
      |                         └── 📄 PostUtils
      |                         └── 📄 UserUtils
      |                    ├── 📂 web
      |                         ├── 📂 dto(comment, file, post, user dto 파일)
      |                         └── 📄 CommentApiController
      |                         └── 📄 FileApiController
      |                         └── 📄 IndexController(Server side rendering)
      |                         └── 📄 PostsApiController
      |                         └── 📄 UserApiController
      |                    └── 📄 SpringBoardApplication.java
      ├── 📂 resources
      |     ├── 📂 static
      |          ├── 📂 scripts
      |               └── 📄 TemporaryUserIncr.lua
      |          ├── 📂 images
      |          ├── 📂 js
      |              ├── 📂 app
      |                    └── 📄 comment.js
      |                    └── 📄 comment-list.js
      |                    └── 📄 drag-drop.js
      |                    └── 📄 file.js
      |                    └── 📄 post.js
      |                    └── 📄 post-comment.js
      |                    └── 📄 post-list.js
      |                    └── 📄 user.js
      |     ├── 📂 templates
      |          ├── 📂 fragments
      |               └── 📄 header.js
      |          └── 📄 comment-list.html
      |          └── 📄 index.html
      |          └── 📄 post-find.html
      |          └── 📄 post-list.html
      |          └── 📄 post-save.html
      |          └── 📄 post-update.html
      |          └── 📄 search.html
      |          └── 📄 user-info.html
      |     └── 📄 application.properties
      |     └── 📄 application-oauth.properties
      |     └── 📄 application-aws.properties
📄 .gitignore    
📄 build.gradle                                                                          
📄 README.md
```
