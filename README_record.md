# 대상 프로젝트
## - JAVA 프로젝트들
- https://github.com/aosp-mirror/platform_frameworks_base.git
  Fork 6.3k / Star 10.4k
- https://github.com/oracle/graal.git
  Fork 1.5k / Star 18.8k
- https://github.com/elastic/elasticsearch.git
  Fork 23.3k / Star 64.5k

## - 다른언어 프로젝트들(파이썬)
- https://github.com/iperov/DeepFaceLab.git init:2018-06-04
  Fork 9.3k / Star 41.5k
- https://github.com/apache/airflow.git init:
  Fork 12.6k / Star 31k

# 작업순서
1. 깃 커밋 목록 추출
   ```
   # 모든 프로젝트 clone 
   ../ai_sample_projects/airflow
   ../ai_sample_projects/DeepFaceLab
   ../ai_sample_projects/elasticsearch
   ../ai_sample_projects/graal
   ../ai_sample_projects/platform_frameworks_base
   #
   # 모든프로젝트에서 하기 명령어 실행으로 깃 목록 추출(git bash에서)
   cd ../ai_sample_projects/platform_frameworks_base && git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" --until="2021-07-21" -1000  '*.java' > githistory_java_pfb_withBody.out && cd -
   cd ../ai_sample_projects/graal && git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" --until="2021-07-21" -200 -- '*.java' > githistory_java_graal_withBody.out && cd -
   cd ../ai_sample_projects/elasticsearch && git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" --until="2021-07-21" -200 -- '*.java' > githistory_java_elastic_withBody.out && cd -
   cd ../ai_sample_projects/DeepFaceLab && git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" --until="2021-07-21" -200 -- '*.py' '*.md' > githistory_python_deepface_withBody.out && cd -
   cd ../ai_sample_projects/airflow && git log --name-only --oneline --pretty="format:####START####%h%n%B%n####END####" --until="2021-07-21" -200 -- '*.py' '*.md' > githistory_python_airflow_withBody.out && cd -
   ```
   
   