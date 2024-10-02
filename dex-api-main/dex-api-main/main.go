package main

import (
	"DexAPI/config"
	"DexAPI/service"
	"DexAPI/utils/log"
	"flag"
	"go.uber.org/zap"
)

func main() {

	flag.Parse()

	log.Init("wow")

	//defer func() {
	//	if r := recover(); r != nil {
	//		stackTrace := fmt.Sprintf("%s", r)
	//		log.Error("panic", zap.String("err", stackTrace), zap.String("stack", string(debug.Stack())))
	//	}
	//}()

	if err := config.ConfInit(); err != nil {
		log.Error("ConfInit", zap.String("data", err.Error()))
		return
	}

	svr, err := service.NewService()
	if err != nil {
		log.Error("NewService", zap.String("data", err.Error()))
		return
	}

	svr.Run()
}
