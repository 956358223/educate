APP_HOME=`pwd`
APP_NAME="$APP_HOME/*.jar"

usage() {
  echo "Warring! Please use command: sh auto_server.sh [start|stop|restart|status]."
  exit 1
}

exists(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}'`    
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

start(){
  exists
  if [ $? -eq 0 ]; then
    echo "${APP_NAME} is already running. Pid is ${pid}."
  else
    nohup java -jar -Dloader.path=lib ${APP_NAME} >/dev/null 2>&1 &
    echo "${APP_NAME} successfully started."
  fi
}

stop(){
  exists
  if [ $? -eq "0" ]; then
    kill -9 $pid
    echo "${APP_NAME} service has been stopped."
  else
    echo "${APP_NAME} is not run."
  fi  
}

status(){
  exists
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. pid is ${pid}."
  else
    echo "${APP_NAME} is NOT running."
  fi
}

restart(){
  stop
  sleep 5
  start
}

case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac

