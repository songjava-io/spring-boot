## Backend API 프로젝트


### 회원 자동로그인 기존 테이블에 컬럼 추가
ALTER TABLE profile.T_MEMBER ADD OAUTH2_PROVIDER varchar(10) NULL COMMENT 'OAuth2 공급자';
ALTER TABLE profile.T_MEMBER CHANGE OAUTH2_PROVIDER OAUTH2_PROVIDER varchar(10) NULL COMMENT 'OAuth2 공급자' AFTER MEMBER_STATE;
ALTER TABLE profile.T_MEMBER ADD OAUTH2_ID varchar(20) NULL COMMENT 'OAuth2 회원 아이디';
ALTER TABLE profile.T_MEMBER CHANGE OAUTH2_ID OAUTH2_ID varchar(20) NULL COMMENT 'OAuth2 회원 아이디' AFTER OAUTH2_PROVIDER;


### 회원 자동로그인 OAUTH2_LOGIN 연동 테이블 생성
CREATE TABLE profile.T_MEMBER_OAUTH2_LOGIN (
	MEMBER_SEQ INT NOT NULL COMMENT '회원번호',
	PROVIDER VARCHAR(10) NOT NULL COMMENT '공급자',
	PROVIDER_ID varchar(10) NULL COMMENT '공급자 고유 아이디',
	LOGIN_DATE DATETIME NOT NULL COMMENT '로그인일자',
	CONSTRAINT T_MEMBER_OAUTH2_LOGIN_PK PRIMARY KEY (MEMBER_SEQ,PROVIDER,PROVIDER_ID)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci
COMMENT='회원 OAUTH2 로그인';


### JdbcOauth2AuthorizedClientService 사용 시 DB에 적용할 테이블 스키마
CREATE TABLE oauth2_authorized_client (
  client_registration_id varchar(100) NOT NULL,
  principal_name varchar(200) NOT NULL,
  access_token_type varchar(100) NOT NULL,
  access_token_value blob NOT NULL,
  access_token_issued_at datetime NOT NULL,
  access_token_expires_at datetime NOT NULL,
  access_token_scopes varchar(1000) DEFAULT NULL,
  refresh_token_value blob DEFAULT NULL,
  refresh_token_issued_at datetime DEFAULT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (client_registration_id, principal_name)
);