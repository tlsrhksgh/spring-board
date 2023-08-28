# 프로젝트 소개
> 1달 간 커뮤니티 게시판에 있는 기능들을 참고하여 혼자서 구현한 커뮤니티 자유 게시판 프로젝트 입니다.
>
> 
> 개발 기간: 2023.07.18 - 2023.08.20

# 💡구현 기능💡
```
🔗 File
- S3버킷 aws-sdk를 이용하여 업로드 및 삭제
- Apache Tika 라이브러리를 이용하여 mime-type을 검증 후 이미지 파일만 허용하도록 처리
- file이름 중복의 여지가 있을 수 있어 UUID로 변환하여 파일 저장
- 한 파일 당 10MB 이하 총 50MB 업로드 가능

 📝 Post
- 게시글 CRUD 기능
- 게시글 조회 수 중복 방지 기능
- 게시글 조회수를 기준으로 상위 5개의 게시글 링크 메인 페이지 상단 출력
- 게시글 검색 기능 구현(키워드로 조회 시 해당 키워드가 포함되어 있는 제목, 본문 게시글을 찾아서 출력)
- 로그인을 하지 않은 익명 사용자는 게시글 조회만 가능

 🙋‍♂ ️User
- OAuth2를 이용한 로그인만 가능 (네이버, 카카오)
- 사용자 프로필 수정 기능 (사용자 이름, 프로필 이미지만 변경 가능. 수정 시 세션에 즉시 반영)

 🧾 Comment
- 댓글 및 대댓글 기능 구현 (셀프 조인, ID를 기준으로 하여 최상위 댓글은 내림차순으로 정렬, 대댓글은 오름차순으로 정렬)
- 비밀 댓글
- 부모 댓글 삭제 시 자식 댓글 전체 삭제
```
 
# 사용 기술
## Backend
- ```JAVA17``` ```SpringBoot``` ```Spring Data JPA``` ```QueryDsl``` ```H2 Database```

## DevOps
- ```AWS EC2``` ```AWS RDS``` ```AWS S3``` ```Jenkins``` ```Nginx```

## Frontend
- ```Thymeleaf``` ```JQuery```

# ERD 설계
![spring-board](https://github.com/tlsrhksgh/spring-board/assets/12388299/bed37d10-9aa0-4339-931a-b2dfc0b0a0da)

# 인프라 구성도
![spring-board_infra drawio](https://github.com/tlsrhksgh/spring-board/assets/12388299/695c737b-216b-43de-843d-bda0ed0052e0)


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
      |                         └── 📄 JpaConfig
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
      |                             └── 📄 AwsS3Upload
      |                             └── 📄 FileService
      |                         └── 📂 post
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
      |                         └── 📄 DateUtils
      |                         └── 📄 FileUtils
      |                    ├── 📂 web
      |                         ├── 📂 dto(comment, file, post, user dto 파일)
      |                         └── 📄 CommentApiController
      |                         └── 📄 IndexController(Server side rendering)
      |                         └── 📄 PostsApiController
      |                         └── 📄 UserApiController
      |                    └── 📄 SpringBoardApplication.java
      ├── 📂 resources
      |     ├── 📂 static
      |          ├── 📂 images
      |          ├── 📂 js
      |              ├── 📂 app
      |                    └── 📄 comment.js
      |                    └── 📄 post.js
      |                    └── 📄 user.js
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

