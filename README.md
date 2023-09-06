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
- 한 파일 당 10MB 이하 총 100MB 업로드 가능

 📝 Post
- 게시글 CRUD 기능
- 게시글 조회 수 중복 방지 기능
- 게시글 조회수를 기준으로 상위 5개의 게시글 링크 메인 페이지 상단 출력
- 게시글 검색 기능 구현(키워드로 조회 시 해당 키워드가 포함되어 있는 제목, 본문 게시글을 찾아서 출력)
- 로그인을 하지 않은 익명 사용자는 게시글 조회만 가능

 🙋‍♂ ️User
- OAuth2를 이용한 로그인만 가능 (네이버, 카카오)
- OAuth2Login 인증 시 기존 사용자와 동일한 이름을 가지고 있는 사용자인 경우에는 임시로 "사용자 + number" 로 이름 설정 처리를 하고 프로필 수정 페이지로 리다이렉트.
- 사용자 프로필 수정 기능 (사용자 이름, 프로필 이미지만 변경 가능. 수정 시 세션에 즉시 반영)

 🧾 Comment
- 댓글 및 대댓글 기능 구현 (셀프 조인, ID를 기준으로 하여 최상위 댓글은 내림차순으로 정렬, 대댓글은 오름차순으로 정렬)
- 비밀 댓글
- 부모 댓글 삭제 시 자식 댓글 전체 삭제
```
 
# 사용 기술
## Backend
- ```JAVA17``` ```SpringBoot``` ```Spring Data JPA``` ```Spring Data Redis``` ```QueryDsl``` ```H2 Database``` 

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
      |                         ├── 📂 handler
      |                             └── 📄 OAuthLoginSuccessHandler
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

## 프로젝트 성과
- 프로젝트 시작 전에는 OAuth2Login 통신 방식에 대한 이해도 부족으로 인증 후 즉시 중복된 이름에 대한 체크를 하지 못하였으나 타 블로그 및 책을 참고하여 이해를 하고 수정 처리까지 완료 하였습니다.
- 댓글 680개를 기준으로 인덱스 설정 전 128ms -> 인덱스 설정 후 24ms 약 6배 가량 처리 시간을 감소 시켰습니다.
- 게시글이 삭제될 때 같이 삭제되는 파일들을 기존에 파일 개수만큼 delete 쿼리가 발생한 부분을 bulk성 쿼리로 수정하여 하나의 쿼리만 실행되도록 처리 하였습니다.
- 메인 페이지와 게시글을 읽을 때 N + 1 문제가 발생되는 문제를 fetch join을 이용하여 하나의 쿼리가 생성되도록 성능 최적화 처리를 하였습니다.
- jenkins를 이용하여 CI/CD 파이프라인을 구축하고 빌드와 테스트를 자동화 하였습니다. 빌드와 테스트가 성공했다면 scp를 이용하여 springboot가 구동되고 있는 ec2서버로 jar파일을 배포하게 됩니다. 
- aws ec2 서버 2대에 springboot application을 구동시키고 앞단에 nginx를 통해서 업스트림 방식으로 지정된 서버에만 라우팅을 하는 방식을 통해 로드밸런싱이 구성 되어있습니다. 
- nginx에서 check modules를 이용하여 3초에 한 번씩 springboot의 /actuator/status uri에 GET 메서드를 통해서 health check를 한 후 서버의 status가 DOWN이라면 해당 인스턴스로는 라우팅되지 않는 방식의 롤링 배포 방식을 채택하여 무중단 배포를 구현하였습니다.
