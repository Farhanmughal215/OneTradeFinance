package log

import (
	"fmt"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	lumberjackv2 "gopkg.in/natefinch/lumberjack.v2"
	"os"
	"runtime"
)

const (
	ALL = iota + 0
	DEBUG
	INFO
	WARN
	ERROR
)

var DebugLevel = DEBUG

const CallHierarchy int = 1

var Log *zap.Logger

func JSON(v interface{}) string {
	return fmt.Sprintf("%+v", v)
}
func ToJSON(v interface{}) string {
	return fmt.Sprintf("%+v", v)
}

func Info(p string, f ...zapcore.Field) {
	if DebugLevel >= INFO {
		return
	}
	_, file, line, ok := runtime.Caller(CallHierarchy)
	callerInfo := "NoCallerFile"
	if ok {
		callerInfo = fmt.Sprintf("%s:%d", file, line)
	}
	t := []zapcore.Field{zap.String("caller", callerInfo)}
	t = append(t, f...)
	Log.Info(p, t...)
}

func Warn(p string, f ...zapcore.Field) {
	if DebugLevel >= WARN {
		return
	}
	_, file, line, ok := runtime.Caller(CallHierarchy)
	callerInfo := "NoCallerFile"
	if ok {
		callerInfo = fmt.Sprintf("%s:%d", file, line)
	}
	t := []zapcore.Field{zap.String("caller", callerInfo)}
	t = append(t, f...)
	Log.Warn(p, t...)
}
func Debug(p string, f ...zapcore.Field) {
	if DebugLevel >= DEBUG {
		return
	}
	_, file, line, ok := runtime.Caller(CallHierarchy)
	callerInfo := "NoCallerFile"
	if ok {
		callerInfo = fmt.Sprintf("%s:%d", file, line)
	}
	t := []zapcore.Field{zap.String("caller", callerInfo)}
	t = append(t, f...)
	Log.Debug(p, t...)
}

func Error(p string, f ...zapcore.Field) {
	if DebugLevel >= ERROR {
		return
	}
	_, file, line, ok := runtime.Caller(CallHierarchy)
	callerInfo := "NoCallerFile"
	if ok {
		callerInfo = fmt.Sprintf("%s:%d", file, line)
	}
	t := []zapcore.Field{zap.String("caller", callerInfo)}
	t = append(t, f...)
	Log.Error(p, t...)
}

func Init(servername string) {
	Log, _ = zap.NewProduction()

	encoderConfig := zapcore.EncoderConfig{
		MessageKey:     "msg",
		TimeKey:        "time",
		LevelKey:       "level",
		NameKey:        "logger",
		StacktraceKey:  "stacktrace",
		LineEnding:     zapcore.DefaultLineEnding,
		EncodeLevel:    zapcore.LowercaseLevelEncoder, // 小写编码器
		EncodeTime:     zapcore.ISO8601TimeEncoder,    // ISO8601 UTC 时间格式
		EncodeDuration: zapcore.SecondsDurationEncoder,
		EncodeCaller:   zapcore.FullCallerEncoder, // 全路径编码器
	}

	cores := make([]zapcore.Core, 0)
	cores = append(cores, zapcore.NewCore(
		zapcore.NewJSONEncoder(encoderConfig),                   //参数1 json 编码器配置
		zapcore.NewMultiWriteSyncer(zapcore.AddSync(os.Stdout)), // 打印到控制台,
		zap.DebugLevel, //参数3 日志级别
	))

	w := zapcore.AddSync(&lumberjackv2.Logger{
		Filename:   "log/log", //日志文件路径
		MaxSize:    10240,     // 每个日志文件保存的最大尺寸 单位：M
		MaxBackups: 1,         // 日志文件最多保存多少个备份
		MaxAge:     30,        // days 文件最多保存多少天
		Compress:   true,      // 是否压缩
	})
	//参数1 控制台 编码器配置
	cores = append(cores, zapcore.NewCore(
		zapcore.NewJSONEncoder(encoderConfig), //参数1 json 编码器配置
		//	zapcore.NewConsoleEncoder(encoderConfig), //参数1 控制台 编码器配置
		zapcore.NewMultiWriteSyncer(w), //参数2 打印到某位置 控制台 or  文件
		zap.DebugLevel,                 //参数3 日志级别
	))

	e := zapcore.AddSync(&lumberjackv2.Logger{
		Filename:   "log/error", //日志文件路径
		MaxSize:    10240,       // 每个日志文件保存的最大尺寸 单位：M
		MaxBackups: 1,           // 日志文件最多保存多少个备份
		MaxAge:     30,          // days 文件最多保存多少天
		Compress:   true,        // 是否压缩
	})

	//参数2 error
	cores = append(cores, zapcore.NewCore(
		zapcore.NewJSONEncoder(encoderConfig), //参数1 json 编码器配置
		//	zapcore.NewConsoleEncoder(encoderConfig), //参数1 控制台 编码器配置
		zapcore.NewMultiWriteSyncer(e), //参数2 打印到某位置 控制台 or  文件
		zap.ErrorLevel,                 //参数3 日志级别
	))

	core := zapcore.NewTee(cores...)

	Log = zap.New(core, zap.AddCaller())
	Log = Log.WithOptions(zap.AddStacktrace(zap.FatalLevel))
}
