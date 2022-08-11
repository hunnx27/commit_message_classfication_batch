# commit_message_classfication_batch
복합 커밋 메시지 분류기의 전처리를 담당하는 배치

# 프로세스
1. clone받은 프로젝트에서 java확장자가 변경된 commit message와 파일경로 및 이름 추출
  - 커밋 목록 추출
  ```
  git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" - 1000 -- '*.java' --until="2017-01-08"  > githistory_withBody.out
  ```
  
2. 추출된 각 커밋별 파일목록의 해당버전의 소스와 이전소스를 파일 생성(소스변경 비교를 위함)

  - 현재버전 파일 조회
  ```
  git show {hash}:{filepath} 
  ```
  - 이전버전 파일 조회
  ```
  git show {hash}~1:{filepath} 
  ```
3. 생성된 두버전의 파일을 changedistiller를 이용해 소스변경 유형을추출해 48개 배열에 그 빈도를 기록(changedistiller는 48개의 유형으로 데이터를 제공해줌)
4. 
```
String[] bugFix_keys = {"fix","test","issu","use","fail","bug_fix","report","set","error","npe"};
String[] featAdd_keys = {"test","remov","use","fix","refactor","method","chang","add","improv","new"};
String[] perfImpr_keys = {"support","add","implement","new","allow","use","method","test","set","chang"};
```
해당 키워드가 있는 경우 라벨 1 표시 아닌 경우 0 세가지 라벨이 모두 0인 경우는 performance_improvement 1 표시
- 레코드 컬럼명 설명
```
커밋메시지 | 소스변경빈도배열 | bug_fix_여부	| feature_addition_여부 |	performance_improvement_여부 의 csv형태로 파일 출력
```
