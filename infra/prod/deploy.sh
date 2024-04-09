#!/bin/bash

echo "### Whatpl Application 배포를 시작합니다."

# 1. stop 되어 있던 애플리케이션에 변경사항 적용
IS_GREEN=$(docker ps | grep whatpl-green)
if [ -z "$IS_GREEN" ]
then
  echo "### whatpl-green 컨테이너를 pull 합니다."
  docker-compose pull whatpl-green
  echo "### whatpl-green 컨테이너를 up 합니다."
  docker-compose up -d whatpl-green
  BEFORE_CONTAINER="whatpl-blue"
  AFTER_CONTAINER="whatpl-green"
  BEFORE_PORT=8080
  AFTER_PORT=8081
else
  echo "### whatpl-blue 컨테이너를 pull 합니다."
  docker-compose pull whatpl-blue
  echo "### whatpl-blue 컨테이너를 up 합니다."
  docker-compose up -d whatpl-blue
  BEFORE_CONTAINER="whatpl-green"
  AFTER_CONTAINER="whatpl-blue"
  BEFORE_PORT=8081
  AFTER_PORT=8080
fi
echo "### ${AFTER_CONTAINER} 컨테이너가 up 되었습니다. (port:${AFTER_PORT})"

# 2. up한 애플리케이션이 정상 응답을 반환하는지 확인
for RETRY_COUNT in {1..15}
do
	echo "### ${AFTER_CONTAINER} 애플리케이션에 health check를 시도합니다. (${RETRY_COUNT}/15)";
	HEALTH_CHECK=$(curl -s localhost:${AFTER_PORT}/health-check)

	if [ "${HEALTH_CHECK}" != "I'm alive" ]
  then
    sleep 10
    continue
  else
    echo "### ${AFTER_CONTAINER} 애플리케이션 health check에 성공했습니다."
    break
	fi
done

if [ "${RETRY_COUNT}" -eq 15 ]
then
  echo "### ${AFTER_CONTAINER} 애플리케이 health check에 실패했습니다. nginx 설정을 바꾸지 않고 배포를 종료합니다."
  exit 1
fi

# 3. 애플리케이션이 정상적으로 실행되면 기존의 애플리케이션에서 새로운 애플리케이션으로 nginx 리버스 프록시 설정을 변경한다.
echo "### nginx 리버스 프록시 설정을 변경합니다."
sed -i "s/${BEFORE_PORT}/${BEFORE_PORT} down/" ./config/inc.d/blue-green.inc
sed -i "s/${AFTER_PORT} down/${AFTER_PORT}/" ./config/inc.d/blue-green.inc
docker exec nginx nginx -s reload

# 4. 기존의 애플리케이션을 종료한다.
echo "### $BEFORE_CONTAINER 컨테이너를 down 합니다. (port:${BEFORE_PORT})"
docker-compose down ${BEFORE_CONTAINER}

echo "### Whatpl Application 배포가 완료되었습니다."
